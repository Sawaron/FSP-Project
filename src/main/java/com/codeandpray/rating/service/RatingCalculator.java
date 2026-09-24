package com.codeandpray.rating.service;

import com.codeandpray.competition.enums.CompetitionLevel;
import com.codeandpray.rating.port.RatingDataPort.RatingInput;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class RatingCalculator {
    public static final int FORMULA_VERSION = 1;

    public Calculation calculate(RatingInput input) {
        if (input.qualificationBonus() < 0) throw new IllegalArgumentException("Отрицательный бонус квалификации");
        long sum = 0;
        Set<Long> ids = new HashSet<>();
        for (var result : input.results()) {
            if (result.resultId() <= 0 || !ids.add(result.resultId()) || result.level() == null) {
                throw new IllegalArgumentException("Некорректный или повторный результат из RatingDataPort");
            }
            sum = Math.addExact(sum, pointsFor(result.place(), result.level()));
        }
        return new Calculation(sum, input.qualificationBonus(), Math.addExact(sum, input.qualificationBonus()));
    }

    public long pointsFor(Integer place, CompetitionLevel level) {
        if (place == null) return 0;
        if (place <= 0) throw new IllegalArgumentException("Место должно быть положительным");
        int base = place == 1 ? 100 : place == 2 ? 70 : place == 3 ? 50 : place <= 10 ? 20 : 5;
        int tenths = switch (level) {
            case RUSSIAN_CHAMPIONSHIP -> 20;
            case NATIONAL -> 17;
            case INTERREGIONAL -> 14;
            case DAGESTAN_CHAMPIONSHIP -> 12;
            case REGIONAL -> 10;
        };
        return (base * tenths + 5L) / 10;
    }

    public record Calculation(long resultPoints, long qualificationPoints, long totalPoints) {
    }
}
