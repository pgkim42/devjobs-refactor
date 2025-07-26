package com.example.devjobs.application.controller;

import com.example.devjobs.application.dto.*;
import com.example.devjobs.application.service.ApplicationService;
import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.user.service.UserDetailsImpl;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "지원 관리", description = "채용공고 지원 관리 API")
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "채용공고 지원", description = "특정 채용공고에 지원합니다. 개인회원만 사용 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "지원 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 지원한 공고")
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> createApplication(
            @RequestBody @Valid ApplicationRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ApplicationResponseDTO responseDTO = applicationService.createApplication(requestDTO, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(responseDTO));
    }

    @Operation(summary = "내 지원 현황 조회", description = "로그인한 개인회원의 지원 현황을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/my")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<List<ApplicationForIndividualResponse>>> getMyApplications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ApplicationForIndividualResponse> response = applicationService.getMyApplications(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지원 취소", description = "지원을 취소합니다. 본인의 지원만 취소 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "취소 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "지원 정보를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasRole('INDIVIDUAL') and @applicationServiceImpl.isApplicationOwner(#applicationId, principal.userId)")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @Parameter(description = "지원 ID") @PathVariable("applicationId") Long applicationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        applicationService.deleteApplication(applicationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "채용공고 지원자 목록 조회", description = "특정 채용공고의 지원자 목록을 조회합니다. 해당 공고를 등록한 기업만 조회 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "채용공고를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/job/{jobPostingId}")
    @PreAuthorize("hasRole('COMPANY') and @applicationServiceImpl.isJobPostingOwner(#jobPostingId, principal.userId)")
    public ResponseEntity<ApiResponse<List<ApplicationForCompanyResponse>>> getJobApplicants(
            @Parameter(description = "채용공고 ID") @PathVariable("jobPostingId") Long jobPostingId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ApplicationForCompanyResponse> response = applicationService.getJobApplicants(jobPostingId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지원 상태 변경", description = "지원자의 지원 상태를 변경합니다. 해당 공고를 등록한 기업만 변경 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "지원 정보를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('COMPANY') and @applicationServiceImpl.isJobPostingOwnerByApplication(#applicationId, principal.userId)")
    public ResponseEntity<ApiResponse<Void>> updateApplicationStatus(
            @Parameter(description = "지원 ID") @PathVariable("applicationId") Long applicationId,
            @RequestBody @Valid UpdateStatusRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        applicationService.updateApplicationStatus(applicationId, requestDTO, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
