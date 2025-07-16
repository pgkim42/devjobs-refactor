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

    @PostMapping("/individual/resume")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<IndividualProfileResponse>> uploadResume(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        IndividualProfileResponse response = profileService.uploadResume(userDetails.getUserId(), file);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 경력 관련 엔드포인트
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

    // 학력 관련 엔드포인트
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

    // 기술 관련 엔드포인트
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

    // 어학 능력 관련 엔드포인트
    @PostMapping("/individual/language-skills")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<LanguageSkillDto>> addLanguageSkill(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody LanguageSkillRequest request) {
        LanguageSkillDto response = profileService.addLanguageSkill(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/individual/language-skills/{languageSkillId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<LanguageSkillDto>> updateLanguageSkill(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long languageSkillId,
            @Valid @RequestBody LanguageSkillRequest request) {
        LanguageSkillDto response = profileService.updateLanguageSkill(userDetails.getUserId(), languageSkillId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/individual/language-skills/{languageSkillId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Void>> deleteLanguageSkill(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long languageSkillId) {
        profileService.deleteLanguageSkill(userDetails.getUserId(), languageSkillId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 자격증 관련 엔드포인트
    @PostMapping("/individual/certifications")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<CertificationDto>> addCertification(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CertificationRequest request) {
        CertificationDto response = profileService.addCertification(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/individual/certifications/{certificationId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<CertificationDto>> updateCertification(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long certificationId,
            @Valid @RequestBody CertificationRequest request) {
        CertificationDto response = profileService.updateCertification(userDetails.getUserId(), certificationId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/individual/certifications/{certificationId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Void>> deleteCertification(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long certificationId) {
        profileService.deleteCertification(userDetails.getUserId(), certificationId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 기업 프로필 관련 엔드포인트
    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<CompanyProfileResponse>> getCompanyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CompanyProfileResponse response = profileService.getCompanyProfile(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<CompanyProfileResponse>> updateCompanyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateCompanyProfileRequest request) {
        CompanyProfileResponse response = profileService.updateCompanyProfile(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/company/logo")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse<CompanyProfileResponse>> uploadCompanyLogo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        CompanyProfileResponse response = profileService.uploadCompanyLogo(userDetails.getUserId(), file);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
