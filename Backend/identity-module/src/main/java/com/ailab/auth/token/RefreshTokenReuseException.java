package com.ailab.auth.token;

public class RefreshTokenReuseException extends InvalidRefreshTokenException {
    public RefreshTokenReuseException(String message) { super(message); }
}
