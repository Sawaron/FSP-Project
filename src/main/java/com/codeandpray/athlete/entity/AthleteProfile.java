package com.codeandpray.athlete.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "athlete_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AthleteProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, updatable = false)
    private long userId;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(nullable = false, length = 255)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_id")
    private Qualification qualification;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static AthleteProfile create(long userId, String fullName, Organization organization,
                                        String city, Qualification qualification, Instant now) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Некорректный пользователь");
        }
        AthleteProfile profile = new AthleteProfile();
        profile.userId = userId;
        profile.change(fullName, organization, city, qualification, now);
        profile.createdAt = now;
        return profile;
    }

    public void change(String fullName, Organization organization, String city,
                       Qualification qualification, Instant now) {
        this.fullName = requireText(fullName, "ФИО");
        this.organization = organization;
        this.city = requireText(city, "Город");
        this.qualification = qualification;
        this.updatedAt = Objects.requireNonNull(now);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank() || value.strip().length() > 255) {
            throw new IllegalArgumentException("Некорректное поле: " + field);
        }
        return value.strip();
    }
}
