package com.example.devjobs.user.dto.profile;

import com.example.devjobs.jobposting.dto.JobPostingResponse;
import com.example.devjobs.user.entity.CompanyUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponse {

    private Long id;
    private String email;
    private String companyName;
    private String companyAddress;
    private String companyCode;
    private String ceoName;
    private String industry;
    private String companyWebsite;
    private String logoUrl;
    private List<JobPostingResponse.Simple> jobPostings;

    public static CompanyProfileResponse fromEntity(CompanyUser companyUser) {
        return CompanyProfileResponse.builder()
                .id(companyUser.getId())
                .email(companyUser.getEmail())
                .companyName(companyUser.getCompanyName())
                .companyAddress(companyUser.getCompanyAddress())
                .companyCode(companyUser.getCompanyCode())
                .ceoName(companyUser.getCeoName())
                .industry(companyUser.getIndustry())
                .companyWebsite(companyUser.getCompanyWebsite())
                .logoUrl(companyUser.getLogoUrl())
                .jobPostings(companyUser.getJobPostings().stream()
                        .map(JobPostingResponse.Simple::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
