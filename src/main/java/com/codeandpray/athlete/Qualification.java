package com.codeandpray.athlete;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "qualifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
}