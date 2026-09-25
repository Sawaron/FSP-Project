package com.codeandpray.registration.entity;

import com.codeandpray.registration.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "registrations", uniqueConstraints =
@UniqueConstraint(name = "uk_registrations_comp_athlete",
        columnNames = {"competition_id", "athlete_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "competition_id", nullable = false, updatable = false)
    private Long competitionId;

    @Column(name = "athlete_id", nullable = false, updatable = false)
    private Long athleteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RegistrationStatus status;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt;

    public static Registration create(long competitionId, long athleteId, Instant now) {
        if (competitionId <= 0 || athleteId <= 0) {
            throw new IllegalArgumentException("Некорректная заявка");
        }
        Registration result = new Registration();
        result.competitionId = competitionId;
        result.athleteId = athleteId;
        result.registeredAt = Objects.requireNonNull(now);
        result.status = RegistrationStatus.REGISTERED;
        return result;
    }

    public void cancel() {
        status = RegistrationStatus.CANCELLED;
    }

    public void restore() {
        status = RegistrationStatus.REGISTERED;
    }
}