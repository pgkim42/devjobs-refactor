package com.example.devjobs.home.dto;

import java.util.List;

public record HomeResponse(
    HomeStatisticsDto statistics,
    List<SimpleJobPostingDto> recentJobs,
    List<CategoryWithCountDto> popularCategories
) {}