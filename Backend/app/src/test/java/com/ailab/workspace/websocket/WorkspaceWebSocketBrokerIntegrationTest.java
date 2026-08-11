package com.ailab.workspace.websocket;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.dto.CreateWorkspaceRequest;
import com.ailab.workspace.dto.RealtimeError;
import com.ailab.workspace.dto.SandboxEventCommand;
import com.ailab.workspace.dto.WorkspaceDetails;
import com.ailab.workspace.dto.WorkspaceEventAck;
import com.ailab.workspace.service.WorkspaceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "local"})
class WorkspaceWebSocketBrokerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private WorkspaceService workspaceService;

    private final List<WebSocketStompClient> clients = new ArrayList<>();
    private User user;
    private WorkspaceDetails workspace;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        user = userRepository.save(new User("WS Tester " + suffix, "ws-" + suffix + "@example.com", "hash", Role.USER));
        workspace = workspaceService.createWorkspace(user.getId(), new CreateWorkspaceRequest("WS Lab " + suffix, "chemistry"));
    }

    @AfterEach
    void tearDown() {
        clients.forEach(WebSocketStompClient::stop);
    }

    @Test
    void brokerDeliversAckTopicBroadcastAndVersionErrors() throws Exception {
        StompSession session = connect("Bearer " + jwtService.issue(user));

        CompletableFuture<WorkspaceEventAck> ack = new CompletableFuture<>();
        CompletableFuture<WorkspaceEventAck> broadcast = new CompletableFuture<>();
        CompletableFuture<RealtimeError> error = new CompletableFuture<>();

        session.subscribe("/user/queue/acks", frameHandler(WorkspaceEventAck.class, ack));
        session.subscribe("/topic/workspaces/" + workspace.id(), frameHandler(WorkspaceEventAck.class, broadcast));
        session.subscribe("/user/queue/errors", frameHandler(RealtimeError.class, error));

        SandboxEventCommand addItem = new SandboxEventCommand(
                "ws-client-item",
                1L,
                "ITEM_ADDED",
                Map.of("id", "ws-flask", "equipmentType", "VOLUMETRIC_FLASK", "capacityMl", 100)
        );
        session.send("/app/workspaces/" + workspace.id() + "/events", addItem);

        WorkspaceEventAck ackMessage = ack.get(8, TimeUnit.SECONDS);
        WorkspaceEventAck broadcastMessage = broadcast.get(8, TimeUnit.SECONDS);
        assertThat(ackMessage.clientEventId()).isEqualTo("ws-client-item");
        assertThat(ackMessage.stateVersion()).isEqualTo(2);
        assertThat(broadcastMessage.clientEventId()).isEqualTo("ws-client-item");
        assertThat(broadcastMessage.stateVersion()).isEqualTo(2);

        SandboxEventCommand staleMove = new SandboxEventCommand(
                "ws-client-stale",
                1L,
                "ITEM_MOVED",
                Map.of("itemId", "ws-flask", "x", 10, "y", 20)
        );
        session.send("/app/workspaces/" + workspace.id() + "/events", staleMove);

        RealtimeError errorMessage = error.get(8, TimeUnit.SECONDS);
        assertThat(errorMessage.code()).isEqualTo("VERSION_CONFLICT");
        assertThat(errorMessage.clientEventId()).isEqualTo("ws-client-stale");
        assertThat(errorMessage.actualVersion()).isEqualTo(2);
    }

    private StompSession connect(String authorization) throws Exception {
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new MappingJackson2MessageConverter());
        clients.add(client);

        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", authorization);
        return client.connectAsync("ws://localhost:" + port + "/ws",
                        new WebSocketHttpHeaders(), headers, new StompSessionHandlerAdapter() {})
                .get(8, TimeUnit.SECONDS);
    }

    private <T> StompFrameHandler frameHandler(Class<T> payloadType, CompletableFuture<T> future) {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return payloadType;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                future.complete(payloadType.cast(payload));
            }
        };
    }
}
