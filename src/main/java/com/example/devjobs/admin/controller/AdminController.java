package com.example.devjobs.admin.controller;

import com.example.devjobs.admin.dto.IndividualUserListResponse;
import com.example.devjobs.admin.dto.CompanyUserListResponse;
import com.example.devjobs.admin.dto.AdminJobPostingListResponse;
import com.example.devjobs.admin.service.AdminService;
import com.example.devjobs.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin API", description = "관리자 전용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "JWT")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "개인회원 목록 조회", description = "관리자가 개인회원 목록을 조회합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/users/individual")
    public ResponseEntity<ApiResponse<Page<IndividualUserListResponse>>> getIndividualUsers(
            @Parameter(description = "검색어 (이름, 이메일)")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<IndividualUserListResponse> users = adminService.getIndividualUsers(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "기업회원 목록 조회", description = "관리자가 기업회원 목록을 조회합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/users/company")
    public ResponseEntity<ApiResponse<Page<CompanyUserListResponse>>> getCompanyUsers(
            @Parameter(description = "검색어 (회사명, 담당자명, 이메일)")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CompanyUserListResponse> users = adminService.getCompanyUsers(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "개인회원 상세 조회", description = "관리자가 특정 개인회원의 상세 정보를 조회합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/users/individual/{userId}")
    public ResponseEntity<ApiResponse<IndividualUserListResponse>> getIndividualUser(@PathVariable Long userId) {
        IndividualUserListResponse user = adminService.getIndividualUser(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @Operation(summary = "기업회원 상세 조회", description = "관리자가 특정 기업회원의 상세 정보를 조회합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/users/company/{userId}")
    public ResponseEntity<ApiResponse<CompanyUserListResponse>> getCompanyUser(@PathVariable Long userId) {
        CompanyUserListResponse user = adminService.getCompanyUser(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @Operation(summary = "채용공고 목록 조회", description = "관리자가 전체 채용공고 목록을 조회합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/job-postings")
    public ResponseEntity<ApiResponse<Page<AdminJobPostingListResponse>>> getJobPostings(
            @Parameter(description = "검색어 (제목, 회사명)")
            @RequestParam(required = false) String search,
            @Parameter(description = "상태 (ACTIVE, CLOSED, CANCELLED, FILLED)")
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<AdminJobPostingListResponse> jobPostings = adminService.getJobPostings(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(jobPostings));
    }
    
    @Operation(summary = "채용공고 삭제", description = "관리자가 채용공고를 삭제합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공고 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @DeleteMapping("/job-postings/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJobPosting(@PathVariable Long jobId) {
        adminService.deleteJobPosting(jobId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}