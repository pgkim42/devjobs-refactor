package com.example.devjobs.application.entity;

import com.example.devjobs.common.BaseEntity;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.user.entity.IndividualUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "applications")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "createDate", column = @Column(name = "submission_date"))
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private IndividualUser individualUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;
}
