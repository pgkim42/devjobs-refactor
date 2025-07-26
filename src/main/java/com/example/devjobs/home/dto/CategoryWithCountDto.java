package com.example.devjobs.home.dto;

public record CategoryWithCountDto(
    Long id,
    String categoryName,
    long jobCount
) {}