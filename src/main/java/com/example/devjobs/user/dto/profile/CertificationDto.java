package com.example.devjobs.user.dto.profile;

import com.example.devjobs.user.entity.Certification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDto {
    private Long id;
    private String name;
    private String issuingOrganization;
    private LocalDate issueDate;

    public static CertificationDto fromEntity(Certification certification) {
        return CertificationDto.builder()
                .id(certification.getId())
                .name(certification.getName())
                .issuingOrganization(certification.getIssuingOrganization())
                .issueDate(certification.getIssueDate())
                .build();
    }
}
