package com.example.devjobs.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeStatisticsDto {
    private long totalJobs;
    private long activeJobs;
    private long totalCompanies;
    private long totalUsers;
}