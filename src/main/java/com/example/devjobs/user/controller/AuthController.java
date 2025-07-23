package com.example.devjobs.user.controller;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.user.dto.auth.CompanyUserSignUpRequest;
import com.example.devjobs.user.dto.auth.CurrentUserResponse;
import com.example.devjobs.user.dto.auth.IndividualUserSignUpRequest;
import com.example.devjobs.user.dto.auth.SignInRequest;
import com.example.devjobs.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid IndividualUserSignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(null));
    }

    @PostMapping("/signup/company")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid CompanyUserSignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(null));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<String>> signIn(@RequestBody @Valid SignInRequest request) {
        String token = authService.signIn(request);
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser(@AuthenticationPrincipal String userId) {
        CurrentUserResponse userInfo = authService.getCurrentUser(Long.parseLong(userId));
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}
