package com.example.devjobs.user.dto.profile;

import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.entity.enums.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndividualProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String portfolioUrl;
    private String profileImageUrl;
    private String headline;
    private WorkStatus workStatus;
    private List<WorkExperienceDto> workExperiences;
    private List<EducationDto> educations;
    private List<LanguageSkillDto> languageSkills;
    private List<CertificationDto> certifications;
    private Set<SkillDto> skills;

    public static IndividualProfileResponse fromEntity(IndividualUser user) {
        return IndividualProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .portfolioUrl(user.getPortfolioUrl())
                .profileImageUrl(user.getProfileImageUrl())
                .headline(user.getHeadline())
                .workStatus(user.getWorkStatus())
                .workExperiences(user.getWorkExperiences().stream().map(WorkExperienceDto::fromEntity).collect(Collectors.toList()))
                .educations(user.getEducations().stream().map(EducationDto::fromEntity).collect(Collectors.toList()))
                .languageSkills(user.getLanguageSkills().stream().map(LanguageSkillDto::fromEntity).collect(Collectors.toList()))
                .certifications(user.getCertifications().stream().map(CertificationDto::fromEntity).collect(Collectors.toList()))
                .skills(user.getSkills().stream().map(SkillDto::fromEntity).collect(Collectors.toSet()))
                .build();
    }
}
