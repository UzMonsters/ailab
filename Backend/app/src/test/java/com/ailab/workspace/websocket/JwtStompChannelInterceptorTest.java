package com.ailab.workspace.websocket;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles({"test", "local"})
class JwtStompChannelInterceptorTest {

    @Autowired
    private JwtStompChannelInterceptor interceptor;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        workspaceRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(new User("Owner", "owner-stomp@example.com", "hash", Role.USER));
        otherUser = userRepository.save(new User("Other", "other-stomp@example.com", "hash", Role.USER));
        workspaceRepository.save(new WorkspaceEntity("ws-stomp-owner", owner.getId(), "Owner Workspace", "chemistry", "exp-stomp-owner"));
    }

    @Test
    void connectRejectsMissingOrInvalidToken() {
        assertThatThrownBy(() -> interceptor.preSend(connectMessage(null), noopChannel()))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);

        assertThatThrownBy(() -> interceptor.preSend(connectMessage("Bearer not-a-jwt"), noopChannel()))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    void connectAcceptsValidTokenAndSetsPrincipal() {
        Message<?> out = interceptor.preSend(connectMessage("Bearer " + jwtService.issue(owner)), noopChannel());

        assertThat(StompHeaderAccessor.wrap(out).getUser()).isNotNull();
        assertThat(StompHeaderAccessor.wrap(out).getUser().getName()).isEqualTo(owner.getId());
    }

    @Test
    void sendAndSubscribeRejectCrossUserWorkspaceAndExperimentDestinations() {
        assertThatThrownBy(() -> interceptor.preSend(authenticatedMessage(
                StompCommand.SUBSCRIBE,
                "/topic/workspaces/ws-stomp-owner",
                otherUser.getId()), noopChannel()))
                .isInstanceOf(AccessDeniedException.class);

        assertThatThrownBy(() -> interceptor.preSend(authenticatedMessage(
                StompCommand.SEND,
                "/app/experiments/exp-stomp-owner/commands",
                otherUser.getId()), noopChannel()))
                .isInstanceOf(AccessDeniedException.class);

        Message<?> allowed = interceptor.preSend(authenticatedMessage(
                StompCommand.SUBSCRIBE,
                "/topic/experiments/exp-stomp-owner",
                owner.getId()), noopChannel());
        assertThat(allowed).isNotNull();
    }

    private Message<byte[]> connectMessage(String authorization) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        if (authorization != null) {
            accessor.addNativeHeader("Authorization", authorization);
        }
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private Message<byte[]> authenticatedMessage(StompCommand command, String destination, String userId) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setLeaveMutable(true);
        accessor.setDestination(destination);
        accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null));
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private MessageChannel noopChannel() {
        return (message, timeout) -> true;
    }
}
