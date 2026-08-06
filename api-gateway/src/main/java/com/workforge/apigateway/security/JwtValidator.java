package com.workforge.apigateway.security;

import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtValidator {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JWTClaimsSet validateToken(String token) {
        SignedJWT signedJWT;

        try {
            signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(new MACVerifier(jwtSecret.getBytes(StandardCharsets.UTF_8)))) {
                throw new InvalidJwtSignatureException("Invalid JWT signature");
            }
        } catch (InvalidJwtSignatureException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidJwtSignatureException("Malformed JWT token");
        }

        JWTClaimsSet claims;
        try {
            claims = signedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            throw new InvalidJwtSignatureException("Malformed JWT claims");
        }

        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null || expirationTime.before(new Date())) {
            throw new JwtExpiredException("JWT token expired");
        }

        return claims;
    }
}
