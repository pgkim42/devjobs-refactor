package com.example.devjobs.jobposting.controller;

import com.example.devjobs.application.dto.ApplicationResponseDTO;
import com.example.devjobs.application.service.ApplicationService;
import com.example.devjobs.jobposting.dto.JobPostingDTO;
import com.example.devjobs.jobposting.service.JobPostingService;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jobpostings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;
    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> createJobPosting(
            @RequestPart("dto") @Valid JobPostingDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile uploadFile) {

        Integer jobCode = jobPostingService.register(dto, uploadFile);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(jobCode)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<JobPostingDTO>> getAllJobPostings() {
        List<JobPostingDTO> jobPostings = jobPostingService.getList();
        return ResponseEntity.ok(jobPostings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostingDTO> getJobPostingById(@PathVariable("id") Integer id) {
        JobPostingDTO jobPosting = jobPostingService.read(id);
        return ResponseEntity.ok(jobPosting);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> updateJobPosting(
            @PathVariable("id") Integer id,
            @RequestPart("dto") @Valid JobPostingDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile uploadFile) {

        jobPostingService.modify(id, dto, uploadFile);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteJobPosting(@PathVariable("id") Integer id) {
        jobPostingService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/all")
    public ResponseEntity<Long> countAllJobPostings() {
        return ResponseEntity.ok(jobPostingService.countAllJobPostings());
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveJobPostings() {
        return ResponseEntity.ok(jobPostingService.countActiveJobPostings());
    }

    @GetMapping("/{jobPostingId}/applications")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<ApplicationResponseDTO>> getJobApplicants(
            @PathVariable("jobPostingId") Long jobPostingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(applicationService.getJobApplicants(jobPostingId, userDetails.getUsername()));
    }
}
