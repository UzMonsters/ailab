package com.ailab.learning.exception;

public class LevelNotFoundException extends RuntimeException {
    public LevelNotFoundException(String message) {
        super(message);
    }
}
