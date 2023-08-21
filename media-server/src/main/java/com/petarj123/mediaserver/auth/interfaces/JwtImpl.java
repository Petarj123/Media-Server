package com.petarj123.mediaserver.auth.interfaces;

import org.springframework.security.core.Authentication;

import java.security.Key;

public interface JwtImpl {
    String generateToken(Authentication authentication);
    String getUsername(String token);
    String getEmail(String token);
    boolean validateToken(String token);

}
