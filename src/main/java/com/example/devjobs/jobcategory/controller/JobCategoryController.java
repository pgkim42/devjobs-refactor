package com.example.devjobs.jobcategory.controller;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.jobcategory.dto.JobCategoryDto;
import com.example.devjobs.jobcategory.service.JobCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "직무 카테고리", description = "직무 카테고리 관리 API")
@RestController
@RequestMapping("/api/job-categories")
@RequiredArgsConstructor
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;

    @Operation(summary = "직무 카테고리 생성", description = "새로운 직무 카테고리를 생성합니다. 관리자만 사용 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<JobCategoryDto.Response>> createCategory(@Valid @RequestBody JobCategoryDto.Request request) {
        JobCategoryDto.Response response = jobCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @Operation(summary = "직무 카테고리 전체 조회", description = "모든 직무 카테고리 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobCategoryDto.Response>>> getAllCategories() {
        List<JobCategoryDto.Response> response = jobCategoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "직무 카테고리 수정", description = "기존 직무 카테고리를 수정합니다. 관리자만 사용 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<JobCategoryDto.Response>> updateCategory(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId,
            @Valid @RequestBody JobCategoryDto.Request request) {
        JobCategoryDto.Response response = jobCategoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "직무 카테고리 삭제", description = "직무 카테고리를 삭제합니다. 관리자만 사용 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId) {
        jobCategoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}