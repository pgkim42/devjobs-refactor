package com.example.devjobs.user.controller;

import com.example.devjobs.user.dto.auth.CompanyUserSignUpRequest;
import com.example.devjobs.user.dto.auth.IndividualUserSignUpRequest;
import com.example.devjobs.user.dto.auth.SignInRequest;
import com.example.devjobs.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/individual")
    public ResponseEntity<Void> signUp(@RequestBody @Valid IndividualUserSignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/signup/company")
    public ResponseEntity<Void> signUp(@RequestBody @Valid CompanyUserSignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody @Valid SignInRequest request) {
        return authService.signIn(request);
    }
}
