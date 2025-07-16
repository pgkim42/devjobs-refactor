package com.example.devjobs.application.dto;

import com.example.devjobs.application.entity.Application;
import com.example.devjobs.application.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationResponseDTO {
    private Long applicationId;
    private Long jobPostingId;
    private String jobPostingTitle;
    private Long applicantId;
    private String applicantName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static ApplicationResponseDTO fromEntity(Application application) {
        return ApplicationResponseDTO.builder()
                .applicationId(application.getId())
                .jobPostingId(application.getJobPosting().getId())
                .jobPostingTitle(application.getJobPosting().getTitle())
                .applicantId(application.getIndividualUser().getId())
                .applicantName(application.getIndividualUser().getName()) // IndividualUser는 User로부터 getName()을 상속받습니다.
                .status(application.getStatus())
                .appliedAt(application.getCreateDate())
                .build();
    }
}
