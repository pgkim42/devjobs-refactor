package com.example.devjobs.jobposting.entity;

import com.example.devjobs.common.BaseEntity;
import com.example.devjobs.user.entity.CompanyUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_postings")
public class JobPosting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_user_id", nullable = false)
    private CompanyUser companyUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_category_id")
    private com.example.devjobs.jobcategory.entity.JobCategory jobCategory;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private Long salary;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column
    private Integer requiredExperienceYears;

    @Column(nullable = false)
    private String workLocation;

    public void update(String title, String content, Long salary, LocalDate deadline, String workLocation, Integer requiredExperienceYears, com.example.devjobs.jobcategory.entity.JobCategory jobCategory) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (salary != null) this.salary = salary;
        if (deadline != null) this.deadline = deadline;
        if (workLocation != null) this.workLocation = workLocation;
        if (requiredExperienceYears != null) this.requiredExperienceYears = requiredExperienceYears;
        if (jobCategory != null) this.jobCategory = jobCategory;
    }
}
