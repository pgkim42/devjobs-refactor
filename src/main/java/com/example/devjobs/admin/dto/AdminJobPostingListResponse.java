package com.example.devjobs.admin.dto;

import com.example.devjobs.jobposting.entity.enums.JobPostingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminJobPostingListResponse {
    private Long id;
    private String title;
    private String companyName;
    private Long companyUserId;
    private String categoryName;
    private String workLocation;
    private Integer requiredExperienceYears;
    private Long salary;
    private LocalDate deadline;
    private JobPostingStatus status;
    private Integer viewCount;
    private Integer applicationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}