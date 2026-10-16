package com.restaurant.restaurantauthsecurity;

import com.restaurant.restaurantauthsecurity.user.Role;
import com.restaurant.restaurantauthsecurity.user.User;
import com.restaurant.restaurantauthsecurity.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevAdminInitializer {

    @Bean
    CommandLineRunner createDevelopmentAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.dev-admin.email:}") String adminEmail,
            @Value("${app.dev-admin.password:}") String adminPassword) {

        return args -> {
            if (adminEmail.isBlank() || adminPassword.isBlank()) {
                System.out.println(
                        "Development Admin not created: credentials are not configured.");
                return;
            }

            String normalizedEmail = adminEmail.trim().toLowerCase();

            if (userRepository.existsByEmail(normalizedEmail)) {
                System.out.println(
                        "Development Admin not created: this email already exists.");
                return;
            }

            User admin = new User();
            admin.setName("Development Admin");
            admin.setEmail(normalizedEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            System.out.println("Development Admin account created.");
        };
    }
}