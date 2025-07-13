package com.example.devjobs.user.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationRequest {

    @NotBlank(message = "Certification name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Issuing organization is required")
    @Size(max = 100)
    private String issuingOrganization;

    @NotNull(message = "Issue date is required")
    @PastOrPresent(message = "Issue date must be in the past or present")
    private LocalDate issueDate;
}
