package com.example.devjobs.application.dto;

import com.example.devjobs.application.entity.Application;
import com.example.devjobs.application.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationForCompanyResponse {
    private Long applicationId;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static ApplicationForCompanyResponse fromEntity(Application application) {
        return ApplicationForCompanyResponse.builder()
                .applicationId(application.getId())
                .applicantId(application.getIndividualUser().getId())
                .applicantName(application.getIndividualUser().getName())
                .applicantEmail(application.getIndividualUser().getEmail())
                .status(application.getStatus())
                .appliedAt(application.getCreateDate())
                .build();
    }
}
