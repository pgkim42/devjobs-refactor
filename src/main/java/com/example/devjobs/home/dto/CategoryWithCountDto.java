package com.example.devjobs.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithCountDto {
    private Long id;
    private String categoryName;
    private long jobCount;
}