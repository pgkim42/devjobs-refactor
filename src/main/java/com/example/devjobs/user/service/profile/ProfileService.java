package com.example.devjobs.user.service.profile;

import com.example.devjobs.user.dto.profile.*;

import java.util.Set;

public interface ProfileService {
    IndividualProfileResponse getIndividualProfile(Long userId);

    IndividualProfileResponse updateIndividualProfile(Long userId, UpdateIndividualProfileRequest request);

    // Work Experience
    WorkExperienceDto addWorkExperience(Long userId, WorkExperienceRequest request);
    WorkExperienceDto updateWorkExperience(Long userId, Long workExperienceId, WorkExperienceRequest request);
    void deleteWorkExperience(Long userId, Long workExperienceId);

    // Education
    EducationDto addEducation(Long userId, EducationRequest request);
    EducationDto updateEducation(Long userId, Long educationId, EducationRequest request);
    void deleteEducation(Long userId, Long educationId);

    // Skill
    Set<SkillDto> addSkill(Long userId, SkillRequest request);
    void deleteSkill(Long userId, Long skillId);

    // Language Skill
    LanguageSkillDto addLanguageSkill(Long userId, LanguageSkillRequest request);
    LanguageSkillDto updateLanguageSkill(Long userId, Long languageSkillId, LanguageSkillRequest request);
    void deleteLanguageSkill(Long userId, Long languageSkillId);

    // Certification
    CertificationDto addCertification(Long userId, CertificationRequest request);
    CertificationDto updateCertification(Long userId, Long certificationId, CertificationRequest request);
    void deleteCertification(Long userId, Long certificationId);

    // Company Profile
    CompanyProfileResponse getCompanyProfile(Long userId);
    CompanyProfileResponse updateCompanyProfile(Long userId, UpdateCompanyProfileRequest request);
}
