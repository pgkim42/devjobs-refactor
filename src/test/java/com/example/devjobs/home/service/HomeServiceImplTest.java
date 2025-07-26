package com.example.devjobs.home.service;

import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.home.dto.CategoryWithCountDto;
import com.example.devjobs.home.dto.HomeResponse;
import com.example.devjobs.home.dto.HomeStatisticsDto;
import com.example.devjobs.home.dto.SimpleJobPostingDto;
import com.example.devjobs.jobcategory.entity.JobCategory;
import com.example.devjobs.jobcategory.repository.JobCategoryRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.entity.enums.JobPostingStatus;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.repository.CompanyUserRepository;
import com.example.devjobs.user.repository.IndividualUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeServiceImplTest {

    @Mock
    private JobPostingRepository jobPostingRepository;
    @Mock
    private JobCategoryRepository jobCategoryRepository;
    @Mock
    private CompanyUserRepository companyUserRepository;
    @Mock
    private IndividualUserRepository individualUserRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    
    @InjectMocks
    private HomeServiceImpl homeService;
    
    private CompanyUser companyUser1;
    private CompanyUser companyUser2;
    private JobCategory backendCategory;
    private JobCategory frontendCategory;
    private JobPosting jobPosting1;
    private JobPosting jobPosting2;
    
    @BeforeEach
    void setUp() {
        companyUser1 = CompanyUser.builder()
                .id(1L)
                .companyName("테크회사")
                .companyCode("123-45-67890")
                .build();
                
        companyUser2 = CompanyUser.builder()
                .id(2L)
                .companyName("IT기업")
                .companyCode("987-65-43210")
                .build();
                
        backendCategory = JobCategory.builder()
                .id(1L)
                .categoryName("백엔드 개발자")
                .build();
                
        frontendCategory = JobCategory.builder()
                .id(2L)
                .categoryName("프론트엔드 개발자")
                .build();
                
        jobPosting1 = JobPosting.builder()
                .id(1L)
                .title("백엔드 개발자 채용")
                .content("백엔드 개발자를 채용합니다.")
                .companyUser(companyUser1)
                .jobCategory(backendCategory)
                .salary(50000000L)
                .deadline(LocalDate.now().plusDays(30))
                .workLocation("서울시 강남구")
                .requiredExperienceYears(3)
                .status(JobPostingStatus.ACTIVE)
                .build();
        setCreateDate(jobPosting1, LocalDateTime.now());
                
        jobPosting2 = JobPosting.builder()
                .id(2L)
                .title("프론트엔드 개발자 채용")
                .content("프론트엔드 개발자를 채용합니다.")
                .companyUser(companyUser2)
                .jobCategory(frontendCategory)
                .salary(45000000L)
                .deadline(LocalDate.now().plusDays(20))
                .workLocation("서울시 송파구")
                .requiredExperienceYears(2)
                .status(JobPostingStatus.ACTIVE)
                .build();
        setCreateDate(jobPosting2, LocalDateTime.now().minusDays(1));
    }
    
    @Test
    @DisplayName("홈 데이터 조회 - 통계, 최신 채용공고, 인기 카테고리")
    void getHomeData_Success() {
        // given
        // 통계 데이터
        when(jobPostingRepository.count()).thenReturn(100L);
        when(jobPostingRepository.countByDeadlineAfter(any(LocalDate.class))).thenReturn(80L);
        when(companyUserRepository.count()).thenReturn(50L);
        when(individualUserRepository.count()).thenReturn(1000L);
        
        // 최신 채용공고
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<JobPosting> jobPostingPage = new PageImpl<>(Arrays.asList(jobPosting1, jobPosting2));
        when(jobPostingRepository.findAll(any(Pageable.class))).thenReturn(jobPostingPage);
        
        // 인기 카테고리
        List<JobCategory> categories = Arrays.asList(backendCategory, frontendCategory);
        when(jobCategoryRepository.findAll()).thenReturn(categories);
        when(jobPostingRepository.countByJobCategoryAndDeadlineAfter(eq(backendCategory), any(LocalDate.class)))
                .thenReturn(25L);
        when(jobPostingRepository.countByJobCategoryAndDeadlineAfter(eq(frontendCategory), any(LocalDate.class)))
                .thenReturn(20L);
        
        // when
        HomeResponse response = homeService.getHomeData();
        
        // then
        assertNotNull(response);
        
        // 통계 검증
        HomeStatisticsDto statistics = response.statistics();
        assertEquals(100L, statistics.totalJobs());
        assertEquals(80L, statistics.activeJobs());
        assertEquals(50L, statistics.totalCompanies());
        assertEquals(1050L, statistics.totalUsers()); // 개인(1000) + 기업(50)
        
        // 최신 채용공고 검증
        List<SimpleJobPostingDto> recentJobs = response.recentJobs();
        assertEquals(2, recentJobs.size());
        assertEquals("백엔드 개발자 채용", recentJobs.get(0).getTitle());
        assertEquals("테크회사", recentJobs.get(0).getCompanyName());
        assertEquals(50000000L, recentJobs.get(0).getSalary());
        
        // 인기 카테고리 검증
        List<CategoryWithCountDto> popularCategories = response.popularCategories();
        assertEquals(2, popularCategories.size());
        assertEquals("백엔드 개발자", popularCategories.get(0).categoryName());
        assertEquals(25L, popularCategories.get(0).jobCount());
        assertEquals("프론트엔드 개발자", popularCategories.get(1).categoryName());
        assertEquals(20L, popularCategories.get(1).jobCount());
        
        // 정렬 검증 (채용공고 수가 많은 순)
        assertTrue(popularCategories.get(0).jobCount() >= popularCategories.get(1).jobCount());
    }
    
    @Test
    @DisplayName("홈 데이터 조회 - 데이터가 없는 경우")
    void getHomeData_NoData() {
        // given
        when(jobPostingRepository.count()).thenReturn(0L);
        when(jobPostingRepository.countByDeadlineAfter(any(LocalDate.class))).thenReturn(0L);
        when(companyUserRepository.count()).thenReturn(0L);
        when(individualUserRepository.count()).thenReturn(0L);
        
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<JobPosting> emptyPage = new PageImpl<>(Arrays.asList());
        when(jobPostingRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);
        
        when(jobCategoryRepository.findAll()).thenReturn(Arrays.asList());
        
        // when
        HomeResponse response = homeService.getHomeData();
        
        // then
        assertNotNull(response);
        
        // 통계 검증
        HomeStatisticsDto statistics = response.statistics();
        assertEquals(0L, statistics.totalJobs());
        assertEquals(0L, statistics.activeJobs());
        assertEquals(0L, statistics.totalCompanies());
        assertEquals(0L, statistics.totalUsers());
        
        // 빈 리스트 검증
        assertTrue(response.recentJobs().isEmpty());
        assertTrue(response.popularCategories().isEmpty());
    }
    
    @Test
    @DisplayName("홈 데이터 조회 - 인기 카테고리 8개 제한")
    void getHomeData_LimitPopularCategories() {
        // given
        // 통계 데이터 (이전과 동일)
        when(jobPostingRepository.count()).thenReturn(100L);
        when(jobPostingRepository.countByDeadlineAfter(any(LocalDate.class))).thenReturn(80L);
        when(companyUserRepository.count()).thenReturn(50L);
        when(individualUserRepository.count()).thenReturn(1000L);
        
        // 최신 채용공고 (이전과 동일)
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<JobPosting> jobPostingPage = new PageImpl<>(Arrays.asList(jobPosting1));
        when(jobPostingRepository.findAll(any(Pageable.class))).thenReturn(jobPostingPage);
        
        // 10개의 카테고리 생성
        List<JobCategory> categories = Arrays.asList();
        for (int i = 1; i <= 10; i++) {
            JobCategory category = JobCategory.builder()
                    .id((long) i)
                    .categoryName("카테고리" + i)
                    .build();
            categories = appendToList(categories, category);
            when(jobPostingRepository.countByJobCategoryAndDeadlineAfter(eq(category), any(LocalDate.class)))
                    .thenReturn((long) (10 - i + 1) * 5); // 역순으로 카운트 설정
        }
        when(jobCategoryRepository.findAll()).thenReturn(categories);
        
        // when
        HomeResponse response = homeService.getHomeData();
        
        // then
        List<CategoryWithCountDto> popularCategories = response.popularCategories();
        assertEquals(8, popularCategories.size()); // 8개로 제한됨
        
        // 첫 번째 카테고리가 가장 많은 공고 수를 가져야 함
        assertTrue(popularCategories.get(0).jobCount() >= popularCategories.get(1).jobCount());
    }
    
    // Reflection helper to set createDate
    private void setCreateDate(JobPosting jobPosting, LocalDateTime dateTime) {
        try {
            java.lang.reflect.Field field = jobPosting.getClass().getSuperclass().getDeclaredField("createDate");
            field.setAccessible(true);
            field.set(jobPosting, dateTime);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // Helper method to append to immutable list
    private <T> List<T> appendToList(List<T> list, T element) {
        List<T> newList = new java.util.ArrayList<>(list);
        newList.add(element);
        return newList;
    }
}