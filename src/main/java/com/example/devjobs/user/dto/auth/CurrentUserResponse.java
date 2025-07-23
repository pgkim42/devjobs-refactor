package com.example.devjobs.user.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserResponse {
    private Long userId;
    private String loginId;
    private String name;
    private String email;
    private String role;
    private String userType; // "individual" or "company"
    
    // Company specific fields (null for individual users)
    private String companyCode;
    private String companyName;
    private String industry;
    private String ceoName;
    private String companyAddress;
    private String companyWebsite;
}