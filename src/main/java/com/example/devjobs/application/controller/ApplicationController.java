package com.example.devjobs.application.controller;

import com.example.devjobs.application.dto.*;
import com.example.devjobs.application.service.ApplicationService;
import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.user.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> createApplication(
            @RequestBody @Valid ApplicationRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ApplicationResponseDTO responseDTO = applicationService.createApplication(requestDTO, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(responseDTO));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<List<ApplicationForIndividualResponse>>> getMyApplications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ApplicationForIndividualResponse> response = applicationService.getMyApplications(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasRole('INDIVIDUAL') and @applicationServiceImpl.isApplicationOwner(#applicationId, principal.userId)")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        applicationService.deleteApplication(applicationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/job/{jobPostingId}")
    @PreAuthorize("hasRole('COMPANY') and @applicationServiceImpl.isJobPostingOwner(#jobPostingId, principal.userId)")
    public ResponseEntity<ApiResponse<List<ApplicationForCompanyResponse>>> getJobApplicants(
            @PathVariable("jobPostingId") Long jobPostingId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ApplicationForCompanyResponse> response = applicationService.getJobApplicants(jobPostingId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('COMPANY') and @applicationServiceImpl.isJobPostingOwnerByApplication(#applicationId, principal.userId)")
    public ResponseEntity<ApiResponse<Void>> updateApplicationStatus(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody @Valid UpdateStatusRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        applicationService.updateApplicationStatus(applicationId, requestDTO, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
