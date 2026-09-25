package com.codeandpray.athlete.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qualifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qualification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "rating_bonus", nullable = false)
    private int ratingBonus;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
