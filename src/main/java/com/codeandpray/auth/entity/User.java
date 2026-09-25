package com.codeandpray.auth.entity;

import com.codeandpray.auth.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static User registerAthlete(String email, String passwordHash, Instant now) {
        return create(email, passwordHash, UserRole.ATHLETE, now);
    }

    public static User createOrganizer(String email, String passwordHash, Instant now) {
        return create(email, passwordHash, UserRole.ORGANIZER, now);
    }

    private static User create(String email, String passwordHash, UserRole role, Instant now) {
        User user = new User();
        user.email = normalizeEmail(email);
        user.changePasswordHash(passwordHash);
        user.role = Objects.requireNonNull(role);
        user.enabled = true;
        user.createdAt = Objects.requireNonNull(now);
        return user;
    }

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank() || email.strip().length() > 255) {
            throw new IllegalArgumentException("Некорректный email");
        }
        return email.strip().toLowerCase(Locale.ROOT);
    }

    public void changePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank() || passwordHash.length() > 255) {
            throw new IllegalArgumentException("Некорректный хеш пароля");
        }
        this.passwordHash = passwordHash;
    }
}
