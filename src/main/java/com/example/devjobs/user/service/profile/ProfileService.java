package com.example.devjobs.user.service.profile;

import com.example.devjobs.user.dto.profile.*;

import java.util.Set;

public interface ProfileService {
    IndividualProfileResponse getIndividualProfile(Long userId);

    IndividualProfileResponse updateIndividualProfile(Long userId, UpdateIndividualProfileRequest request);

    IndividualProfileResponse uploadResume(Long userId, org.springframework.web.multipart.MultipartFile file);

    // 경력
    WorkExperienceDto addWorkExperience(Long userId, WorkExperienceRequest request);
    WorkExperienceDto updateWorkExperience(Long userId, Long workExperienceId, WorkExperienceRequest request);
    void deleteWorkExperience(Long userId, Long workExperienceId);

    // 학력
    EducationDto addEducation(Long userId, EducationRequest request);
    EducationDto updateEducation(Long userId, Long educationId, EducationRequest request);
    void deleteEducation(Long userId, Long educationId);

    // 기술
    Set<SkillDto> addSkill(Long userId, SkillRequest request);
    void deleteSkill(Long userId, Long skillId);

    // 어학 능력
    LanguageSkillDto addLanguageSkill(Long userId, LanguageSkillRequest request);
    LanguageSkillDto updateLanguageSkill(Long userId, Long languageSkillId, LanguageSkillRequest request);
    void deleteLanguageSkill(Long userId, Long languageSkillId);

    // 자격증
    CertificationDto addCertification(Long userId, CertificationRequest request);
    CertificationDto updateCertification(Long userId, Long certificationId, CertificationRequest request);
    void deleteCertification(Long userId, Long certificationId);

    // 기업 프로필
    CompanyProfileResponse getCompanyProfile(Long userId);
    CompanyProfileResponse updateCompanyProfile(Long userId, UpdateCompanyProfileRequest request);
    CompanyProfileResponse uploadCompanyLogo(Long userId, org.springframework.web.multipart.MultipartFile file);
}
