package com.petarj123.mediaserver.auth.service;

import com.petarj123.mediaserver.auth.DTO.LoginResponse;
import com.petarj123.mediaserver.auth.exceptions.*;
import com.petarj123.mediaserver.auth.jwt.service.JwtService;
import com.petarj123.mediaserver.auth.user.model.Role;
import com.petarj123.mediaserver.auth.user.model.User;
import com.petarj123.mediaserver.auth.user.repository.UserRepository;
import com.petarj123.mediaserver.uploader.exceptions.FolderException;
import com.petarj123.mediaserver.uploader.folder.model.Folder;
import com.petarj123.mediaserver.uploader.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FolderRepository folderRepository;
    @Value("${fileStorage.path}")
    private String serverFolderPath;

    public LoginResponse login(String usernameOrEmail, String password) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                usernameOrEmail, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return LoginResponse.builder()
                .token(jwtService.generateToken(authentication))
                .build();
    }
    public void register(String username, String email, String password, String confirmPassword) throws PasswordMismatchException, InvalidPasswordException, InvalidEmailException, UsernameExistsException, EmailExistsException, FolderException {

        if (userRepository.existsByUsername(username)) {
            throw new UsernameExistsException("Username already exists");
        } else if (userRepository.existsByEmail(email)) {
            throw new EmailExistsException("Email already exists");
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new InvalidEmailException("Invalid email format");
        }

        if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new InvalidPasswordException("Password does not meet the requirements");
        }

        if (!password.equals(confirmPassword)){
            throw new PasswordMismatchException("Passwords do not match");
        }

        User user = createUser(username, email, password);
        Folder folder = createUserFolder(email);

        userRepository.save(user);
        folderRepository.save(folder);
    }
    private User createUser(String username, String email, String password) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(new HashSet<>())
                .userFolderPath(serverFolderPath + email)
                .createdAt(new Date())
                .isLocked(false)
                .build();
        user.setRoles(new HashSet<>(List.of(Role.USER)));
        return user;
    }
    private Folder createUserFolder(String name) throws FolderException {
        try {
            Path path = Paths.get(serverFolderPath);
            if (!Files.exists(path)){
                Files.createDirectory(path);
            }
            Path folderPath = path.resolve(name);
            if (!Files.exists(folderPath)){
                Files.createDirectory(folderPath);
                return Folder.builder()
                        .name(name)
                        .path(String.valueOf(folderPath))
                        .parentFolderId(null)
                        .createdAt(new Date())
                        .modifiedAt(new Date())
                        .build();
            } else {
                throw new FolderException("Folder " + name + " already exists.");
            }
        } catch (IOException e) {
            throw new FolderException("Failed to create folder. Error: " + e.getMessage());
        }
    }
}
