package com.example.devjobs.jobcategory.controller;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.jobcategory.dto.JobCategoryDto;
import com.example.devjobs.jobcategory.service.JobCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-categories")
@RequiredArgsConstructor
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<JobCategoryDto.Response>> createCategory(@Valid @RequestBody JobCategoryDto.Request request) {
        JobCategoryDto.Response response = jobCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobCategoryDto.Response>>> getAllCategories() {
        List<JobCategoryDto.Response> response = jobCategoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<JobCategoryDto.Response>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody JobCategoryDto.Request request) {
        JobCategoryDto.Response response = jobCategoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        jobCategoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}