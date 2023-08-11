package com.petarj123.mediaserver.auth.service;

import com.petarj123.mediaserver.auth.exceptions.*;
import com.petarj123.mediaserver.auth.user.model.Role;
import com.petarj123.mediaserver.auth.user.model.User;
import com.petarj123.mediaserver.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(String username, String email, String password, String confirmPassword) throws PasswordMismatchException, InvalidPasswordException, InvalidEmailException, UsernameExistsException, EmailExistsException {

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

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(new HashSet<>())
                .createdAt(new Date())
                .build();

        user.setRoles(new HashSet<>(List.of(Role.USER, Role.ADMIN)));
        userRepository.save(user);
    }
    public void promoteUserToAdmin(String usernameOrEmail){
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<Role> roles = user.getRoles();

        if (roles.contains(Role.ADMIN)) {
            throw new IllegalStateException("User already has the ADMIN role");
        }

        roles.add(Role.ADMIN);
        user.setRoles(roles);

        userRepository.save(user);
    }
    public void demoteUserFromAdmin(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<Role> roles = user.getRoles();

        if (!roles.contains(Role.ADMIN)) {
            throw new IllegalStateException("User doesn't have the ADMIN role to be demoted");
        }

        roles.remove(Role.ADMIN);
        user.setRoles(roles);

        userRepository.save(user);
    }

}
