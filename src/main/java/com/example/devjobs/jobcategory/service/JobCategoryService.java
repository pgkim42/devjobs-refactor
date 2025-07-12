package com.example.devjobs.jobcategory.service;

import com.example.devjobs.jobcategory.dto.JobCategoryDto;

import java.util.List;

public interface JobCategoryService {
    JobCategoryDto.Response createCategory(JobCategoryDto.Request request);
    List<JobCategoryDto.Response> getAllCategories();
    JobCategoryDto.Response updateCategory(Long categoryId, JobCategoryDto.Request request);
    void deleteCategory(Long categoryId);
}