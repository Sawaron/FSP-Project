package com.codeandpray.athlete;
import com.codeandpray.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
@Entity
@Table(name="athlete_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AthleteProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name="user_id",unique=true,nullable=false)
    // User жду
    private User user;
    @Column(name="full_name",nullable = false)
    private String fullName;
    @ManyToOne
    @JoinColumn(name="organization_id")
    private Organization organization;
    @Column(name="city",nullable=false)
    private String city;
    @ManyToOne
    @JoinColumn(name="qualification_id")
    // nullable = true означает, что поле может отсутствовать (быть null)
    private Qualification qualification;
    private  Instant createdAt;
    private Instant updatedAt;
}
