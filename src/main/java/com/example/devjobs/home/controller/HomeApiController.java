package com.example.devjobs.home.controller;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.home.dto.HomeResponse;
import com.example.devjobs.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "홈화면", description = "홈화면 관련 API")
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeApiController {
    
    private final HomeService homeService;
    
    @Operation(summary = "홈화면 데이터 조회", description = "홈화면에 표시할 통계, 최신 채용공고, 카테고리별 현황 등을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<HomeResponse>> getHomeData() {
        HomeResponse homeData = homeService.getHomeData();
        return ResponseEntity.ok(ApiResponse.success(homeData));
    }
}