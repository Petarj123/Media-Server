package com.petarj123.mediaserver.auth.DTO;

public record LoginRequest(String usernameOrEmail, String password) {
}
