package com.petarj123.mediaserver.auth;

import com.petarj123.mediaserver.auth.DTO.LoginResponse;
import com.petarj123.mediaserver.auth.exceptions.*;
import com.petarj123.mediaserver.auth.jwt.service.JwtService;
import com.petarj123.mediaserver.auth.service.AuthService;
import com.petarj123.mediaserver.auth.user.model.User;
import com.petarj123.mediaserver.auth.user.repository.UserRepository;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.folder.repository.FolderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private FolderRepository folderRepository;
    @Mock
    private AuthenticationManager authenticationManager;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        authService = new AuthService(authenticationManager, userRepository, passwordEncoder, jwtService, folderRepository);
        ReflectionTestUtils.setField(authService, "serverFolderPath", "/home/petarjankovic/Documents/Server");


    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
        Files.deleteIfExists(Path.of("/home/petarjankovic/Documents/Server/test@email.com"));
    }
    @Test
    void register_whenUsernameExists_throwsUsernameExistsException() {
        // Setup
        when(userRepository.existsByUsername("Petarj123")).thenReturn(true);

        // Assertion
        assertThrows(UsernameExistsException.class, () -> {
            authService.register("Petarj123", "test@email.com", "Validpassword1!", "Validpassword1!");
        });
    }

    @Test
    void register_whenEmailExists_throwsEmailExistsException() {
        // Setup
        when(userRepository.existsByEmail("petarjank1@gmail.com")).thenReturn(true);

        // Assertion
        assertThrows(EmailExistsException.class, () -> {
            authService.register("Petar44", "petarjank1@gmail.com", "Validpassword1!", "Validpassword1!");
        });
    }

    @Test
    void register_whenEmailFormatIsInvalid_throwsInvalidEmailException() {
        // Assertion
        assertThrows(InvalidEmailException.class, () -> {
            authService.register("newUser", "invalidEmailFormat.com", "Validpassword1!", "Validpassword1!");
        });
    }

    @Test
    void register_whenPasswordFormatIsInvalid_throwsInvalidPasswordException() {
        // Assertion
        assertThrows(InvalidPasswordException.class, () -> {
            authService.register("newUser", "test@email.com", "invalidPassword", "invalidPassword");
        });
    }

    @Test
    void register_whenPasswordsDoNotMatch_throwsPasswordMismatchException() {
        // Assertion
        assertThrows(PasswordMismatchException.class, () -> {
            authService.register("newUser", "test@email.com", "Validpassword1!", "Differentpassword2!");
        });
    }

    @Test
    void register_whenRegistrationIsSuccessful_noExceptionsThrown() throws Exception {
        // Setup
        when(userRepository.existsByUsername("newUser1")).thenReturn(false);
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);

        // Invocation
        authService.register("newUser1", "test@email.com", "Validpassword1!", "Validpassword1!");

        // Verification
        verify(userRepository, times(1)).save(any(User.class));
        verify(folderRepository, times(1)).save(any(Folder.class));
    }
    @Test
    void login_withInvalidCredentials_throwsBadCredentialsException() {
        // Arrange
        String usernameOrEmail = "invalidUser";
        String password = "invalidPassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(usernameOrEmail, password);
        });

        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
        verify(jwtService, never()).generateToken(any());
    }
    @Test
    void login_withValidCredentials_returnsLoginResponse() {
        // Arrange
        String usernameOrEmail = "testUser";
        String password = "testPassword";
        Authentication mockAuthentication = mock(Authentication.class);
        String mockToken = "mockToken";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtService.generateToken(mockAuthentication)).thenReturn(mockToken);

        // Act
        LoginResponse response = authService.login(usernameOrEmail, password);

        // Assert
        assertNotNull(response);
        assertEquals(mockToken, response.token());
        assertEquals(mockAuthentication, SecurityContextHolder.getContext().getAuthentication());

        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
        verify(jwtService, times(1)).generateToken(mockAuthentication);
    }
}
