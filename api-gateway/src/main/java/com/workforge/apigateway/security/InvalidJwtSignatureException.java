package com.workforge.apigateway.security;

public class InvalidJwtSignatureException extends JwtValidationException {
    public InvalidJwtSignatureException(String message) {
        super(message);
    }
}
