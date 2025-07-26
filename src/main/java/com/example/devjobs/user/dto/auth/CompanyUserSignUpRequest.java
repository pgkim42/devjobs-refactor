package com.example.devjobs.user.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUserSignUpRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    private String loginId;

    @NotBlank
    @Size(min = 8, max = 30)
    private String password;

    @NotBlank
    private String name; // 담당자 이름

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String companyName;

    @NotBlank
    private String companyAddress;

    @NotBlank
    private String companyCode; // 사업자등록번호

    @NotBlank
    private String ceoName;

    private String companyWebsite;
    private String logoUrl;
}
