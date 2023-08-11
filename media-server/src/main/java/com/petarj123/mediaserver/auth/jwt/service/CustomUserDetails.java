package com.petarj123.mediaserver.auth.jwt.service;

import com.petarj123.mediaserver.auth.user.model.SecureUser;
import com.petarj123.mediaserver.auth.user.model.User;
import com.petarj123.mediaserver.auth.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetails implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username or email: " + usernameOrEmail));

        return new SecureUser(user);
    }
}
