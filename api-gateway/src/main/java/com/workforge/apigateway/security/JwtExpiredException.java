package com.workforge.apigateway.security;

public class JwtExpiredException extends JwtValidationException {
    public JwtExpiredException(String message) {
        super(message);
    }
}
