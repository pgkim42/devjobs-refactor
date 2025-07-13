package com.example.devjobs.user.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyProfileRequest {
    private String companyAddress;
    private String industry;
    private String companyWebsite;
    private String logoUrl;
}
