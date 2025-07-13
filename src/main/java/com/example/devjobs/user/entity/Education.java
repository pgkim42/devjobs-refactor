package com.example.devjobs.user.entity;

import com.example.devjobs.common.BaseEntity;
import com.example.devjobs.user.entity.enums.Degree;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "education")
public class Education extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    private String major;

    @Enumerated(EnumType.STRING)
    private Degree degree;

    private LocalDate admissionDate;

    private LocalDate graduationDate;

    private Double gpa;

    private Double maxGpa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private IndividualUser individualUser;
}
