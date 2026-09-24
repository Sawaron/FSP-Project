package com.codeandpray.competition.entity;
import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.competition.enums.CompetitionFormat;
import com.codeandpray.competition.enums.CompetitionLevel;
import java.time.Instant;

/** Значение предметной области: валидное расписание и редактируемые реквизиты. */
public record CompetitionDetails(String title, CompetitionLevel level, long disciplineId,
        Instant startsAt, Instant endsAt, CompetitionFormat format, String venue,
        String description, Instant registrationOpensAt, Instant registrationClosesAt) {
    public CompetitionDetails {
        title = title == null ? "" : title.strip();
        description = description == null ? "" : description.strip();
        venue = venue == null || venue.isBlank() ? null : venue.strip();
        if (title.isBlank() || title.length() > 200 || description.length() > 5000
                || disciplineId <= 0 || level == null || format == null) {
            throw BusinessException.badRequest("Некорректные реквизиты соревнования");
        }
        if (startsAt == null || endsAt == null || registrationOpensAt == null || registrationClosesAt == null
                || !endsAt.isAfter(startsAt) || !registrationClosesAt.isAfter(registrationOpensAt)
                || registrationClosesAt.isAfter(startsAt)) {
            throw BusinessException.badRequest("Проверьте порядок дат регистрации и соревнования");
        }
        if ((format != CompetitionFormat.ONLINE && venue == null) || (venue != null && venue.length() > 500)) {
            throw BusinessException.badRequest("Для очного/смешанного формата нужно место проведения до 500 символов");
        }
    }
}
