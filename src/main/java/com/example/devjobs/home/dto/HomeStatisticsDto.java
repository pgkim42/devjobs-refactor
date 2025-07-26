package com.example.devjobs.home.dto;

public record HomeStatisticsDto(
    long totalJobs,
    long activeJobs,
    long totalCompanies,
    long totalUsers
) {}