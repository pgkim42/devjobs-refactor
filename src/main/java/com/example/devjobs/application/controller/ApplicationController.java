package com.example.devjobs.application.controller;

import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.application.dto.ApplicationResponseDTO;
import com.example.devjobs.application.dto.UpdateStatusRequestDTO;
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
    public ResponseEntity<ApiResponse<List<ApplicationResponseDTO>>> getMyApplications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ApplicationResponseDTO> response = applicationService.getMyApplications(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        applicationService.deleteApplication(applicationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<Void>> updateApplicationStatus(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody @Valid UpdateStatusRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        applicationService.updateApplicationStatus(applicationId, requestDTO, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
