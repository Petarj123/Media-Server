package com.petarj123.mediaserver.auth.DTO;

public record RegistrationRequest(String username, String email, String password, String confirmPassword) {
}
