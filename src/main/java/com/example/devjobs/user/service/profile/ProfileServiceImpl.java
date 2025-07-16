package com.example.devjobs.user.service.profile;

import com.example.devjobs.common.file.FileService;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.repository.CompanyUserRepository;
import com.example.devjobs.user.dto.profile.*;
import com.example.devjobs.user.entity.*;
import com.example.devjobs.user.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final IndividualUserRepository individualUserRepository;
    private final CompanyUserRepository companyUserRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final LanguageSkillRepository languageSkillRepository;
    private final CertificationRepository certificationRepository;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public IndividualProfileResponse getIndividualProfile(Long userId) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return IndividualProfileResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public IndividualProfileResponse updateIndividualProfile(Long userId, UpdateIndividualProfileRequest request) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setPortfolioUrl(request.getPortfolioUrl());
        user.setHeadline(request.getHeadline());
        user.setWorkStatus(request.getWorkStatus());

        return IndividualProfileResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public IndividualProfileResponse uploadResume(Long userId, MultipartFile file) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        String fileName = fileService.storeFile(file);
        String fileUrl = fileService.getFileUrl(fileName);

        user.setResumeUrl(fileUrl);
        individualUserRepository.save(user);

        return IndividualProfileResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public WorkExperienceDto addWorkExperience(Long userId, WorkExperienceRequest request) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        WorkExperience workExperience = WorkExperience.builder()
                .companyName(request.getCompanyName())
                .department(request.getDepartment())
                .position(request.getPosition())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .individualUser(user)
                .build();

        WorkExperience savedWorkExperience = workExperienceRepository.save(workExperience);
        return WorkExperienceDto.fromEntity(savedWorkExperience);
    }

    @Override
    @Transactional
    public WorkExperienceDto updateWorkExperience(Long userId, Long workExperienceId, WorkExperienceRequest request) {
        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new EntityNotFoundException("Work experience not found with id: " + workExperienceId));

        if (!workExperience.getIndividualUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this work experience.");
        }

        workExperience.setCompanyName(request.getCompanyName());
        workExperience.setDepartment(request.getDepartment());
        workExperience.setPosition(request.getPosition());
        workExperience.setStartDate(request.getStartDate());
        workExperience.setEndDate(request.getEndDate());
        workExperience.setDescription(request.getDescription());

        return WorkExperienceDto.fromEntity(workExperience);
    }

    @Override
    @Transactional
    public void deleteWorkExperience(Long userId, Long workExperienceId) {
        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new EntityNotFoundException("Work experience not found with id: " + workExperienceId));

        if (!workExperience.getIndividualUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this work experience.");
        }

        workExperienceRepository.delete(workExperience);
    }

    @Override
    @Transactional
    public EducationDto addEducation(Long userId, EducationRequest request) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Education education = Education.builder()
                .schoolName(request.getSchoolName())
                .major(request.getMajor())
                .degree(request.getDegree())
                .admissionDate(request.getAdmissionDate())
                .graduationDate(request.getGraduationDate())
                .gpa(request.getGpa())
                .maxGpa(request.getMaxGpa())
                .individualUser(user)
                .build();

        Education savedEducation = educationRepository.save(education);
        return EducationDto.fromEntity(savedEducation);
    }

    @Override
    @Transactional
    public EducationDto updateEducation(Long userId, Long educationId, EducationRequest request) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education not found with id: " + educationId));

        if (!education.getIndividualUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this education.");
        }

        education.setSchoolName(request.getSchoolName());
        education.setMajor(request.getMajor());
        education.setDegree(request.getDegree());
        education.setAdmissionDate(request.getAdmissionDate());
        education.setGraduationDate(request.getGraduationDate());
        education.setGpa(request.getGpa());
        education.setMaxGpa(request.getMaxGpa());

        return EducationDto.fromEntity(education);
    }

    @Override
    @Transactional
    public void deleteEducation(Long userId, Long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education not found with id: " + educationId));

        if (!education.getIndividualUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this education.");
        }

        educationRepository.delete(education);
    }

    @Override
    @Transactional
    public Set<SkillDto> addSkill(Long userId, SkillRequest request) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Skill skill = skillRepository.findByName(request.getName())
                .orElseGet(() -> skillRepository.save(Skill.builder().name(request.getName()).build()));

        user.getSkills().add(skill);

        return user.getSkills().stream()
                .map(SkillDto::fromEntity)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void deleteSkill(Long userId, Long skillId) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + skillId));

        user.getSkills().remove(skill);
    }

    @Override
    @Transactional
    public LanguageSkillDto addLanguageSkill(Long userId, LanguageSkillRequest request) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        LanguageSkill languageSkill = LanguageSkill.builder()
                .language(request.getLanguage())
                .proficiency(request.getProficiency())
                .individualUser(user)
                .build();

        LanguageSkill savedLanguageSkill = languageSkillRepository.save(languageSkill);
        return LanguageSkillDto.fromEntity(savedLanguageSkill);
    }

    @Override
    @Transactional
    public LanguageSkillDto updateLanguageSkill(Long userId, Long languageSkillId, LanguageSkillRequest request) {
        LanguageSkill languageSkill = languageSkillRepository.findById(languageSkillId)
                .orElseThrow(() -> new EntityNotFoundException("Language skill not found with id: " + languageSkillId));

        if (!languageSkill.getIndividualUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this language skill.");
        }

        languageSkill.setLanguage(request.getLanguage());
        languageSkill.setProficiency(request.getProficiency());

        return LanguageSkillDto.fromEntity(languageSkill);
    }

    @Override
    @Transactional
    public void deleteLanguageSkill(Long userId, Long languageSkillId) {
        LanguageSkill languageSkill = languageSkillRepository.findById(languageSkillId)
                .orElseThrow(() -> new EntityNotFoundException("Language skill not found with id: " + languageSkillId));

        if (!languageSkill.getIndividualUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this language skill.");
        }

        languageSkillRepository.delete(languageSkill);
    }

    @Override
    @Transactional
    public CertificationDto addCertification(Long userId, CertificationRequest request) {
        IndividualUser user = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Certification certification = Certification.builder()
                .name(request.getName())
                .issuingOrganization(request.getIssuingOrganization())
                .issueDate(request.getIssueDate())
                .individualUser(user)
                .build();

        Certification savedCertification = certificationRepository.save(certification);
        return CertificationDto.fromEntity(savedCertification);
    }

    @Override
    @Transactional
    public CertificationDto updateCertification(Long userId, Long certificationId, CertificationRequest request) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new EntityNotFoundException("Certification not found with id: " + certificationId));

        if (!certification.getIndividualUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this certification.");
        }

        certification.setName(request.getName());
        certification.setIssuingOrganization(request.getIssuingOrganization());
        certification.setIssueDate(request.getIssueDate());

        return CertificationDto.fromEntity(certification);
    }

    @Override
    @Transactional
    public void deleteCertification(Long userId, Long certificationId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new EntityNotFoundException("Certification not found with id: " + certificationId));

        if (!certification.getIndividualUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this certification.");
        }

        certificationRepository.delete(certification);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileResponse getCompanyProfile(Long userId) {
        CompanyUser user = companyUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Company user not found with id: " + userId));
        return CompanyProfileResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public CompanyProfileResponse updateCompanyProfile(Long userId, UpdateCompanyProfileRequest request) {
        CompanyUser user = companyUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Company user not found with id: " + userId));

        user.setCompanyAddress(request.getCompanyAddress());
        user.setIndustry(request.getIndustry());
        user.setCompanyWebsite(request.getCompanyWebsite());
        user.setLogoUrl(request.getLogoUrl());

        return CompanyProfileResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public CompanyProfileResponse uploadCompanyLogo(Long userId, MultipartFile file) {
        CompanyUser user = companyUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Company user not found with id: " + userId));

        String fileName = fileService.storeFile(file);
        String fileUrl = fileService.getFileUrl(fileName);

        user.setLogoUrl(fileUrl);
        companyUserRepository.save(user);

        return CompanyProfileResponse.fromEntity(user);
    }
}
