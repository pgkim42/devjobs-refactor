package com.example.devjobs.user.service;

import com.example.devjobs.user.dto.auth.CompanyUserSignUpRequest;
import com.example.devjobs.user.dto.auth.CurrentUserResponse;
import com.example.devjobs.user.dto.auth.IndividualUserSignUpRequest;
import com.example.devjobs.user.dto.auth.SignInRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    void signUp(IndividualUserSignUpRequest request);

    void signUp(CompanyUserSignUpRequest request);

    String signIn(SignInRequest request);

    CurrentUserResponse getCurrentUser(Long userId);
}
