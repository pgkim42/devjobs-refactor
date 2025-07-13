package com.example.devjobs.application.dto;

import com.example.devjobs.application.entity.Application;
import com.example.devjobs.application.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationForIndividualResponse {
    private Long applicationId;
    private Long jobPostingId;
    private String jobPostingTitle;
    private String companyName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static ApplicationForIndividualResponse fromEntity(Application application) {
        return ApplicationForIndividualResponse.builder()
                .applicationId(application.getId())
                .jobPostingId(application.getJobPosting().getId())
                .jobPostingTitle(application.getJobPosting().getTitle())
                .companyName(application.getJobPosting().getCompanyUser().getCompanyName())
                .status(application.getStatus())
                .appliedAt(application.getCreateDate())
                .build();
    }
}
