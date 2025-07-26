package com.example.devjobs.user.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndividualUserSignUpRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    private String loginId;

    @NotBlank
    @Size(min = 8, max = 30)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String phoneNumber;
    private String address;
    private String portfolioUrl;
    private List<String> skills;
}
