package com.ailab.auth.token;

public class RefreshTokenReuseException extends RuntimeException {
    public RefreshTokenReuseException(String message) { super(message); }
}
