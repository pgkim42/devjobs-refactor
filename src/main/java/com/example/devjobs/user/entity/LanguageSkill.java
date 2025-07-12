package com.example.devjobs.user.entity;

import com.example.devjobs.common.BaseEntity;
import com.example.devjobs.user.entity.enums.Proficiency;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "language_skill")
public class LanguageSkill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String language;

    @Enumerated(EnumType.STRING)
    private Proficiency proficiency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private IndividualUser individualUser;
}
