package com.example.devjobs.home.controller;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.home.dto.HomeResponse;
import com.example.devjobs.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeApiController {
    
    private final HomeService homeService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<HomeResponse>> getHomeData() {
        HomeResponse homeData = homeService.getHomeData();
        return ResponseEntity.ok(ApiResponse.success(homeData));
    }
}