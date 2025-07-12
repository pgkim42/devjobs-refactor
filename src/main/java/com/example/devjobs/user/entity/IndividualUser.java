package com.example.devjobs.user.entity;

import com.example.devjobs.application.entity.Application;
import com.example.devjobs.user.entity.enums.WorkStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "individual_users")
@DiscriminatorValue("INDIVIDUAL")
public class IndividualUser extends User {

    private String phoneNumber;

    private String address;

    private String portfolioUrl;

    private String profileImageUrl;

    @Column(length = 200)
    private String headline;

    @Enumerated(EnumType.STRING)
    private WorkStatus workStatus;

    @Builder.Default
    @OneToMany(mappedBy = "individualUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkExperience> workExperiences = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "individualUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "individualUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LanguageSkill> languageSkills = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "individualUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "individualUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "individual_user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();
}