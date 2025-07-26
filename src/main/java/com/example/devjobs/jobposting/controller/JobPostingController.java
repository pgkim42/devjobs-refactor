package com.example.devjobs.jobposting.controller;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.jobposting.dto.JobPostingRequest;
import com.example.devjobs.jobposting.dto.JobPostingResponse;
import com.example.devjobs.jobposting.service.JobPostingService;
import com.example.devjobs.user.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채용공고", description = "채용공고 관리 API")
@RestController
@RequestMapping("/api/jobpostings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @Operation(summary = "채용공고 등록", description = "새로운 채용공고를 등록합니다. 기업회원만 사용 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "채용공고 등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<JobPostingResponse.Detail>> createJobPosting(
            @Valid @RequestBody JobPostingRequest.Create request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        JobPostingResponse.Detail response = jobPostingService.createJobPosting(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @Operation(summary = "채용공고 상세 조회", description = "특정 채용공고의 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "채용공고를 찾을 수 없음")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<JobPostingResponse.Detail>> getJobPosting(
            @Parameter(description = "채용공고 ID", example = "1") @PathVariable Long postId) {
        JobPostingResponse.Detail response = jobPostingService.getJobPosting(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "내 채용공고 목록 조회", description = "로그인한 기업이 등록한 채용공고 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/my")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<List<JobPostingResponse.Simple>>> getMyJobPostings(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<JobPostingResponse.Simple> response = jobPostingService.getCompanyJobPostings(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "채용공고 검색", description = "다양한 조건으로 채용공고를 검색합니다. 모든 파라미터는 선택사항입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobPostingResponse.Simple>>> searchJobPostings(
            @Parameter(description = "검색어 (제목, 내용, 회사명)") @RequestParam(required = false) String keyword,
            @Parameter(description = "근무지역") @RequestParam(required = false) String location,
            @Parameter(description = "최소급여 (만원)") @RequestParam(required = false) Integer minSalary,
            @Parameter(description = "최대급여 (만원)") @RequestParam(required = false) Integer maxSalary,
            @Parameter(description = "최소경력 (년)") @RequestParam(required = false) Integer minExperience,
            @Parameter(description = "최대경력 (년)") @RequestParam(required = false) Integer maxExperience,
            @Parameter(description = "직무 카테고리 ID") @RequestParam(required = false) Long jobCategoryId,
            @Parameter(description = "페이지 정보") Pageable pageable) {
        Page<JobPostingResponse.Simple> response = jobPostingService.searchJobPostings(
                keyword, location, minSalary, maxSalary, minExperience, maxExperience, jobCategoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "채용공고 수정", description = "등록한 채용공고를 수정합니다. 본인이 등록한 공고만 수정 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "채용공고를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{postId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<JobPostingResponse.Detail>> updateJobPosting(
            @Parameter(description = "채용공고 ID") @PathVariable Long postId,
            @Valid @RequestBody JobPostingRequest.Update request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        JobPostingResponse.Detail response = jobPostingService.updateJobPosting(postId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "채용공고 삭제", description = "등록한 채용공고를 삭제합니다. 본인이 등록한 공고만 삭제 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "채용공고를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<Void>> deleteJobPosting(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        jobPostingService.deleteJobPosting(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
