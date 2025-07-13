package com.example.devjobs.user.controller.profile;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.user.dto.profile.*;
import com.example.devjobs.user.service.UserDetailsImpl;
import com.example.devjobs.user.service.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/individual")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<IndividualProfileResponse>> getIndividualProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        IndividualProfileResponse response = profileService.getIndividualProfile(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/individual")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<IndividualProfileResponse>> updateIndividualProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateIndividualProfileRequest request) {
        IndividualProfileResponse response = profileService.updateIndividualProfile(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Work Experience Endpoints
    @PostMapping("/individual/work-experiences")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<WorkExperienceDto>> addWorkExperience(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody WorkExperienceRequest request) {
        WorkExperienceDto response = profileService.addWorkExperience(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/individual/work-experiences/{workExperienceId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<WorkExperienceDto>> updateWorkExperience(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long workExperienceId,
            @Valid @RequestBody WorkExperienceRequest request) {
        WorkExperienceDto response = profileService.updateWorkExperience(userDetails.getUserId(), workExperienceId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/individual/work-experiences/{workExperienceId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Void>> deleteWorkExperience(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long workExperienceId) {
        profileService.deleteWorkExperience(userDetails.getUserId(), workExperienceId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // Education Endpoints
    @PostMapping("/individual/educations")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<EducationDto>> addEducation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody EducationRequest request) {
        EducationDto response = profileService.addEducation(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/individual/educations/{educationId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<EducationDto>> updateEducation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long educationId,
            @Valid @RequestBody EducationRequest request) {
        EducationDto response = profileService.updateEducation(userDetails.getUserId(), educationId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/individual/educations/{educationId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long educationId) {
        profileService.deleteEducation(userDetails.getUserId(), educationId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // Skill Endpoints
    @PostMapping("/individual/skills")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Set<SkillDto>>> addSkill(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody SkillRequest request) {
        Set<SkillDto> response = profileService.addSkill(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @DeleteMapping("/individual/skills/{skillId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long skillId) {
        profileService.deleteSkill(userDetails.getUserId(), skillId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
