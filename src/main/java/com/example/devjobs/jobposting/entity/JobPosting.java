package com.example.devjobs.jobposting.entity;

import com.example.devjobs.application.entity.Application;
import com.example.devjobs.common.BaseEntity;
import com.example.devjobs.user.entity.CompanyUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "job_posting")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(name = "createDate", column = @Column(name = "posting_date"))
public class JobPosting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_code", nullable = false)
    private Integer jobCode;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "recruit_job", nullable = false)
    private String recruitJob;

    @Column(name = "recruit_field", nullable = false)
    private int recruitField;

    @Column(name = "salary")
    private String salary;

    @Column(name = "posting_deadline", nullable = false)
    private LocalDateTime postingDeadline;

    @Column(name = "posting_status", nullable = false)
    private boolean postingStatus;

    @Column(name = "work_experience", nullable = false)
    private Integer workExperience;

    @Column(name = "tag")
    private String tag;

    @Column(name = "job_category", nullable = false)
    private String jobCategory;

    @Column(name = "img_file_name", length = 100)
    private String imgFileName;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "skill", nullable = false, length = 255)
    private String skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code")
    private CompanyUser companyUser;

    @Transient
    private int matchScore;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Application> applications;

}
