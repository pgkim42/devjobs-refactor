package com.example.devjobs.user.dto.profile;

import com.example.devjobs.user.entity.enums.Degree;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationRequest {

    @NotBlank(message = "School name is required")
    @Size(max = 100)
    private String schoolName;

    @NotBlank(message = "Major is required")
    @Size(max = 100)
    private String major;

    private Degree degree;

    @NotNull(message = "Admission date is required")
    @PastOrPresent(message = "Admission date must be in the past or present")
    private LocalDate admissionDate;

    @PastOrPresent(message = "Graduation date must be in the past or present")
    private LocalDate graduationDate;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "4.5") // 최대 학점은 학교 규정에 맞게 사용
    private Double gpa;

    @DecimalMin(value = "0.0")
    private Double maxGpa;
}
