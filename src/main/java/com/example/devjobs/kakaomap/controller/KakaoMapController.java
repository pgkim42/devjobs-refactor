package com.example.devjobs.kakaomap.controller;

import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.kakaomap.service.KakaoMapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/kakao-map")
@RequiredArgsConstructor
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;
    private final JobPostingRepository jobPostingRepository;

    @GetMapping("/coordinates/{jobId}")
    public ResponseEntity<?> getCoordinatesForJobPosting(@PathVariable Long jobId) {
        Optional<JobPosting> jobPostingOptional = jobPostingRepository.findById(jobId);
        if (jobPostingOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("구인공고를 찾을 수 없습니다.");
        }

        JobPosting jobPosting = jobPostingOptional.get();
        if (jobPosting.getWorkLocation() == null || jobPosting.getWorkLocation().isEmpty()) {
            return ResponseEntity.badRequest().body("구인공고에 등록된 주소를 찾을 수 없습니다");
        }

        try {
            String coordinatesJson = kakaoMapService.getCoordinates(jobPosting.getWorkLocation());
            ObjectMapper objectMapper = new ObjectMapper();
            Object json = objectMapper.readValue(coordinatesJson, Object.class);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch coordinates: " + e.getMessage());
        }
    }
}