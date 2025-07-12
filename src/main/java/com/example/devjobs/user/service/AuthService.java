package com.example.devjobs.user.service;

import com.example.devjobs.user.dto.auth.CompanyUserSignUpRequest;
import com.example.devjobs.user.dto.auth.IndividualUserSignUpRequest;
import com.example.devjobs.user.dto.auth.SignInRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<Void> signUp(IndividualUserSignUpRequest request);

    ResponseEntity<Void> signUp(CompanyUserSignUpRequest request);

    ResponseEntity<String> signIn(SignInRequest request);
}
