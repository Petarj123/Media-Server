package com.petarj123.mediaserver.auth.controller;

import com.petarj123.mediaserver.auth.DTO.LoginRequest;
import com.petarj123.mediaserver.auth.DTO.RegistrationRequest;
import com.petarj123.mediaserver.auth.exceptions.*;
import com.petarj123.mediaserver.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.usernameOrEmail(), loginRequest.password());
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegistrationRequest request) throws PasswordMismatchException, InvalidPasswordException, UsernameExistsException, EmailExistsException, InvalidEmailException {
        authService.register(request.username(), request.email(), request.password(), request.confirmPassword());
    }
}