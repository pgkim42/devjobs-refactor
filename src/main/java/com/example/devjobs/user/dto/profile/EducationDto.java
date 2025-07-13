package com.example.devjobs.user.dto.profile;

import com.example.devjobs.user.entity.Education;
import com.example.devjobs.user.entity.enums.Degree;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDto {
    private Long id;
    private String schoolName;
    private String major;
    private Degree degree;
    private LocalDate admissionDate;
    private LocalDate graduationDate;
    private Double gpa;
    private Double maxGpa;

    public static EducationDto fromEntity(Education education) {
        return EducationDto.builder()
                .id(education.getId())
                .schoolName(education.getSchoolName())
                .major(education.getMajor())
                .degree(education.getDegree())
                .admissionDate(education.getAdmissionDate())
                .graduationDate(education.getGraduationDate())
                .gpa(education.getGpa())
                .maxGpa(education.getMaxGpa())
                .build();
    }
}
