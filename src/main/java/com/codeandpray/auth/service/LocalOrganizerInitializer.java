package com.codeandpray.auth.service;

import com.codeandpray.auth.entity.User;
import com.codeandpray.auth.enums.UserRole;
import com.codeandpray.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Clock;

@Component
@Profile("local")
@ConditionalOnProperty(name = "app.local-organizer.enabled", havingValue = "true")
@RequiredArgsConstructor
public class LocalOrganizerInitializer implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Value("${app.local-organizer.email}")
    private String email;

    @Value("${app.local-organizer.password}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String normalizedEmail = User.normalizeEmail(email);
        validatePassword(password);
        var existing = users.findByEmail(normalizedEmail);
        if (existing.isPresent()) {
            User user = existing.get();
            if (user.getRole() != UserRole.ORGANIZER || !user.isEnabled()) {
                throw new IllegalStateException("Local organizer email is already occupied");
            }
            return;
        }
        users.saveAndFlush(User.createOrganizer(
                normalizedEmail,
                passwordEncoder.encode(password),
                clock.instant()
        ));
    }

    private void validatePassword(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("LOCAL_ORGANIZER_PASSWORD is required");
        }
        int length = value.getBytes(StandardCharsets.UTF_8).length;
        if (length < 8 || length > 72) {
            throw new IllegalStateException("Local organizer password must contain 8 to 72 UTF-8 bytes");
        }
    }
}
