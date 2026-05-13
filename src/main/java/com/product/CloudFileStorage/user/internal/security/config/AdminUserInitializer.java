package com.product.CloudFileStorage.user.internal.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.product.CloudFileStorage.user.internal.model.entity.User;
import com.product.CloudFileStorage.user.internal.model.enums.Role;
import com.product.CloudFileStorage.user.internal.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeAdmin() {

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists, skipping initialization");
            return;
        }

        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setFullname("Admin User");
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);

        log.info("Admin user initialized successfully");
    }
}
