package com.example.devjobs.user.entity;

import com.example.devjobs.jobposting.entity.JobPosting;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company_users")
@DiscriminatorValue("COMPANY")
public class CompanyUser extends User {

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyAddress;

    @Column(unique = true, nullable = false)
    private String companyCode; // 사업자등록번호

    @Column(nullable = false)
    private String ceoName;

    @Column
    private String industry;

    private String companyWebsite;

    private String logoUrl;

    @Builder.Default
    @OneToMany(mappedBy = "companyUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobPosting> jobPostings = new ArrayList<>();
}