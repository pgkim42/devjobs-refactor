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
public class IndividualUserListResponse {
    private Long userId;
    private String loginId;
    private String email;
    private String name;
    private String phone;
    private String gender;
    private LocalDateTime birthDate;
    private String address;
    private Integer applicationCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Boolean isActive;
}