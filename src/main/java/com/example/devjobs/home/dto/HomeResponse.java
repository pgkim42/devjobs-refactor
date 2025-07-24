package com.example.devjobs.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponse {
    private HomeStatisticsDto statistics;
    private List<SimpleJobPostingDto> recentJobs;
    private List<CategoryWithCountDto> popularCategories;
}