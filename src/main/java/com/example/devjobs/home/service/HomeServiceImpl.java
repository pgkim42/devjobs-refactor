package com.example.devjobs.home.service;

import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.home.dto.CategoryWithCountDto;
import com.example.devjobs.home.dto.HomeResponse;
import com.example.devjobs.home.dto.HomeStatisticsDto;
import com.example.devjobs.home.dto.SimpleJobPostingDto;
import com.example.devjobs.jobcategory.entity.JobCategory;
import com.example.devjobs.jobcategory.repository.JobCategoryRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.repository.CompanyUserRepository;
import com.example.devjobs.user.repository.IndividualUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeServiceImpl implements HomeService {
    
    private final JobPostingRepository jobPostingRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final CompanyUserRepository companyUserRepository;
    private final IndividualUserRepository individualUserRepository;
    private final ApplicationRepository applicationRepository;
    
    @Override
    public HomeResponse getHomeData() {
        // 통계 데이터 조회
        HomeStatisticsDto statistics = getStatistics();
        
        // 최신 채용공고 6개 조회
        List<SimpleJobPostingDto> recentJobs = getRecentJobs();
        
        // 인기 카테고리 조회 (채용공고가 많은 순서대로)
        List<CategoryWithCountDto> popularCategories = getPopularCategories();
        
        return HomeResponse.builder()
                .statistics(statistics)
                .recentJobs(recentJobs)
                .popularCategories(popularCategories)
                .build();
    }
    
    private HomeStatisticsDto getStatistics() {
        long totalJobs = jobPostingRepository.count();
        long activeJobs = jobPostingRepository.countByDeadlineAfter(LocalDate.now());
        long totalCompanies = companyUserRepository.count();
        long totalIndividuals = individualUserRepository.count();
        
        return HomeStatisticsDto.builder()
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .totalCompanies(totalCompanies)
                .totalUsers(totalIndividuals + totalCompanies)
                .build();
    }
    
    private List<SimpleJobPostingDto> getRecentJobs() {
        PageRequest pageRequest = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createDate"));
        List<JobPosting> recentPostings = jobPostingRepository.findAll(pageRequest).getContent();
        
        return recentPostings.stream()
                .map(posting -> SimpleJobPostingDto.builder()
                        .id(posting.getId())
                        .title(posting.getTitle())
                        .content(posting.getContent())
                        .salary(posting.getSalary())
                        .deadline(posting.getDeadline())
                        .workLocation(posting.getWorkLocation())
                        .requiredExperienceYears(posting.getRequiredExperienceYears())
                        .jobCategoryId(posting.getJobCategory().getId())
                        .jobCategoryName(posting.getJobCategory().getCategoryName())
                        .companyName(posting.getCompanyUser().getCompanyName())
                        .companyCode(posting.getCompanyUser().getCompanyCode())
                        .createdAt(posting.getCreateDate())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<CategoryWithCountDto> getPopularCategories() {
        List<JobCategory> allCategories = jobCategoryRepository.findAll();
        
        return allCategories.stream()
                .map(category -> {
                    long jobCount = jobPostingRepository.countByJobCategoryAndDeadlineAfter(
                            category, LocalDate.now());
                    return CategoryWithCountDto.builder()
                            .id(category.getId())
                            .categoryName(category.getCategoryName())
                            .jobCount(jobCount)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getJobCount(), a.getJobCount()))
                .limit(8)
                .collect(Collectors.toList());
    }
}