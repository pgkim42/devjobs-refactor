package com.example.devjobs.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyUserListResponse {
    private Long userId;
    private String loginId;
    private String email;
    private String companyName;
    private String businessNumber;
    private String ceoName;
    private String contactPersonName;
    private String contactPhone;
    private String address;
    private Integer jobPostingCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Boolean isActive;
}