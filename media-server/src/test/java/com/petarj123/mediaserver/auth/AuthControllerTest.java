package com.petarj123.mediaserver.auth;

import com.petarj123.mediaserver.auth.DTO.LoginRequest;
import com.petarj123.mediaserver.auth.DTO.LoginResponse;
import com.petarj123.mediaserver.auth.DTO.RegistrationRequest;
import com.petarj123.mediaserver.auth.controller.AuthController;
import com.petarj123.mediaserver.auth.exceptions.PasswordMismatchException;
import com.petarj123.mediaserver.auth.service.AuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testLoginEndpoint_ReturnsOK() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResponse expectedResponse = new LoginResponse("token");

        when(authService.login(loginRequest.usernameOrEmail(), loginRequest.password()))
                .thenReturn(expectedResponse);

        // Act
        LoginResponse actualResponse = authController.login(loginRequest);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(authService, times(1)).login(loginRequest.usernameOrEmail(), loginRequest.password());
    }


    @Test
    public void testLogin_Negative() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("username", "password");

        when(authService.login(loginRequest.usernameOrEmail(), loginRequest.password()))
                .thenThrow(new RuntimeException("Login failed"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> authController.login(loginRequest));
        verify(authService, times(1)).login(loginRequest.usernameOrEmail(), loginRequest.password());
    }

    @Test
    public void testRegister_Positive() throws Exception {
        // Arrange
        RegistrationRequest registrationRequest = new RegistrationRequest("username", "email", "password", "password");

        // Act and Assert
        assertDoesNotThrow(() -> authController.register(registrationRequest));
        verify(authService, times(1)).register(
                registrationRequest.username(),
                registrationRequest.email(),
                registrationRequest.password(),
                registrationRequest.confirmPassword()
        );
    }

    @Test
    public void testRegister_Negative() throws Exception {
        // Arrange
        RegistrationRequest registrationRequest = new RegistrationRequest("username", "email", "password", "password");

        doThrow(new PasswordMismatchException("Passwords do not match"))
                .when(authService).register(
                        registrationRequest.username(),
                        registrationRequest.email(),
                        registrationRequest.password(),
                        registrationRequest.confirmPassword()
                );

        // Act and Assert
        assertThrows(PasswordMismatchException.class, () -> authController.register(registrationRequest));
        verify(authService, times(1)).register(
                registrationRequest.username(),
                registrationRequest.email(),
                registrationRequest.password(),
                registrationRequest.confirmPassword()
        );
    }
}
