package com.codeandpray;

import com.codeandpray.auth.entity.User;
import com.codeandpray.auth.enums.UserRole;
import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.competition.enums.*;
import com.codeandpray.registration.entity.Registration;
import com.codeandpray.registration.port.RegistrationContext.CompetitionView;
import com.codeandpray.rating.port.RatingDataPort.*;
import com.codeandpray.rating.service.RatingCalculator;
import com.codeandpray.result.entity.Result;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import static org.assertj.core.api.Assertions.*;

class DomainRulesTest {
    private final Instant now = Instant.parse("2026-09-25T10:00:00Z");

    @Test
    void emailNormalizationDoesNotDependOnLocale() {
        Locale before = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            User user = User.registerAthlete("INFO@EXAMPLE.COM", "encoded", now);
            assertThat(user.getEmail()).isEqualTo("info@example.com");
            assertThat(user.getRole()).isEqualTo(UserRole.ATHLETE);
            assertThat(user.isEnabled()).isTrue();
        } finally {
            Locale.setDefault(before);
        }
    }

    @Test
    void restoringRegistrationPreservesOriginalTimestamp() {
        Registration registration = Registration.create(1, 2, now);
        registration.cancel();
        registration.restore();
        assertThat(registration.getRegisteredAt()).isEqualTo(now);
    }

    @Test
    void closingBoundaryAndCancelledCompetitionAreRejected() {
        var open = new CompetitionView(1, 1, CompetitionStatus.UPCOMING,
                now.minusSeconds(60), now.plusSeconds(60));
        assertThat(open.registrationOpen(now)).isTrue();
        assertThat(open.registrationOpen(now.plusSeconds(60))).isFalse();
        var cancelled = new CompetitionView(1, 1, CompetitionStatus.CANCELLED,
                now.minusSeconds(60), now.plusSeconds(60));
        assertThat(cancelled.registrationOpen(now)).isFalse();
    }

    @Test
    void repeatedPublicationDoesNotMutateResult() {
        Result result = Result.createDraft(1, 1, 2, null, null, now);
        assertThat(result.publish(98, 1, now)).isTrue();
        assertThat(result.publish(1000, 2, now.plusSeconds(1))).isFalse();
        assertThat(result.getRatingPoints()).isEqualTo(98);
        assertThat(result.getPublishedAt()).isEqualTo(now);
        assertThatThrownBy(() -> result.updateDraft(1, null, null, now))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void calculationUsesStoredPointsAndRejectsDuplicates() {
        var calculator = new RatingCalculator();
        var result = new PublishedResult(1, 98, 1);
        assertThat(calculator.calculate(new RatingInput(30, List.of(result))).totalPoints())
                .isEqualTo(128);
        assertThatThrownBy(() -> calculator.calculate(new RatingInput(0, List.of(result, result))))
                .isInstanceOf(IllegalArgumentException.class);
    }
}