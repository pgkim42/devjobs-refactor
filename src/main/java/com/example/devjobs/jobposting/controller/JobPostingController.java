package com.example.devjobs.jobposting.controller;

import com.example.devjobs.jobposting.dto.JobPostingRequest;
import com.example.devjobs.jobposting.dto.JobPostingResponse;
import com.example.devjobs.jobposting.service.JobPostingService;
import com.example.devjobs.user.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobpostings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobPostingResponse.Detail> createJobPosting(
            @Valid @RequestBody JobPostingRequest.Create request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        JobPostingResponse.Detail response = jobPostingService.createJobPosting(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<JobPostingResponse.Detail> getJobPosting(@PathVariable Long postId) {
        JobPostingResponse.Detail response = jobPostingService.getJobPosting(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<JobPostingResponse.Simple>> getAllJobPostings(Pageable pageable) {
        Page<JobPostingResponse.Simple> response = jobPostingService.getAllJobPostings(pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{postId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobPostingResponse.Detail> updateJobPosting(
            @PathVariable Long postId,
            @Valid @RequestBody JobPostingRequest.Update request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        JobPostingResponse.Detail response = jobPostingService.updateJobPosting(postId, request, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> deleteJobPosting(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        jobPostingService.deleteJobPosting(postId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
