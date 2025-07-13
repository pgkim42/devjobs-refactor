package com.example.devjobs.user.dto.profile;

import com.example.devjobs.user.entity.WorkExperience;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceDto {
    private Long id;
    private String companyName;
    private String department;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public static WorkExperienceDto fromEntity(WorkExperience workExperience) {
        return WorkExperienceDto.builder()
                .id(workExperience.getId())
                .companyName(workExperience.getCompanyName())
                .department(workExperience.getDepartment())
                .position(workExperience.getPosition())
                .startDate(workExperience.getStartDate())
                .endDate(workExperience.getEndDate())
                .description(workExperience.getDescription())
                .build();
    }
}
