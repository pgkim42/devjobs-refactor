package com.example.devjobs.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleJobPostingDto {
    private Long id;
    private String title;
    private String content;
    private Long salary;
    private LocalDate deadline;
    private String workLocation;
    private Integer requiredExperienceYears;
    private Long jobCategoryId;
    private String jobCategoryName;
    private String companyName;
    private String companyCode;
    private LocalDateTime createdAt;
}