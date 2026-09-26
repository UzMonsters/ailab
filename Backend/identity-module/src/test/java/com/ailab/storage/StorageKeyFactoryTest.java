package com.ailab.storage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StorageKeyFactoryTest {

    private final StorageKeyFactory factory = new StorageKeyFactory();

    @Test
    void createsScopedKeys() {
        assertThat(factory.assetKey("ast_123")).isEqualTo("assets/ast_123");
        assertThat(factory.avatarKey("usr_123", "avatar_456")).isEqualTo("avatars/usr_123/avatar_456");
        assertThat(factory.workspacePreviewKey("ws_1", "prev_2", "asset_3"))
                .isEqualTo("workspaces/ws_1/previews/prev_2/asset_3");
    }

    @Test
    void rejectsUnsafeSegments() {
        assertThatThrownBy(() -> factory.assetKey("../evil"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid storage key segment");
    }
}
