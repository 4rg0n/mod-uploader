package com.github.argon.moduploader.core;

import jakarta.annotation.Nullable;

import java.time.Duration;

public interface Blockable {
    default void block() {
        block(null);
    };

    void block(@Nullable Duration timeout);
}
