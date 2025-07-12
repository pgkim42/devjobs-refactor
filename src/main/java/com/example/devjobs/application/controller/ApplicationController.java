package com.example.devjobs.application.controller;

import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.application.dto.ApplicationResponseDTO;
import com.example.devjobs.application.dto.UpdateStatusRequestDTO;
import com.example.devjobs.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApplicationResponseDTO> createApplication(
            @RequestBody @Valid ApplicationRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApplicationResponseDTO responseDTO = applicationService.createApplication(requestDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ApplicationResponseDTO>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(applicationService.getMyApplications(userDetails.getUsername()));
    }

    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        applicationService.deleteApplication(applicationId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> updateApplicationStatus(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody @Valid UpdateStatusRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        applicationService.updateApplicationStatus(applicationId, requestDTO, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
