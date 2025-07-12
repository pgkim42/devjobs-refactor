package com.example.devjobs.user.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
}
