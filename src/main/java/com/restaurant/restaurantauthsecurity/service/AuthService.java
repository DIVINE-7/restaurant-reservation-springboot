
package com.restaurant.restaurantauthsecurity.service;

import com.restaurant.restaurantauthsecurity.user.Role;
import com.restaurant.restaurantauthsecurity.user.User;
import com.restaurant.restaurantauthsecurity.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String name, String email, String rawPassword) {

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(normalizedEmail);

        // Never save a plain-text password.
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Public registration must never create staff or admin accounts.
        user.setRole(Role.CUSTOMER);

        return userRepository.save(user);
    }
}
