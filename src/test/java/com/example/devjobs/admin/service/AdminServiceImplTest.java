package com.example.devjobs.admin.service;

import com.example.devjobs.admin.dto.AdminJobPostingListResponse;
import com.example.devjobs.admin.dto.CompanyUserListResponse;
import com.example.devjobs.admin.dto.IndividualUserListResponse;
import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.entity.enums.JobPostingStatus;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private IndividualUser individualUser;
    private CompanyUser companyUser;
    private JobPosting jobPosting;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);
        
        individualUser = IndividualUser.builder()
                .id(1L)
                .loginId("user1")
                .email("user1@test.com")
                .name("김개발")
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .role("ROLE_INDIVIDUAL")
                .build();
        individualUser.setCreateDate(LocalDateTime.now());
        
        companyUser = CompanyUser.builder()
                .id(2L)
                .loginId("company1")
                .email("company1@test.com")
                .name("이담당")
                .companyName("테크기업")
                .companyCode("123-45-67890")
                .ceoName("김대표")
                .companyAddress("서울시 강남구")
                .role("ROLE_COMPANY")
                .build();
        companyUser.setCreateDate(LocalDateTime.now());
        
        jobPosting = JobPosting.builder()
                .id(1L)
                .title("백엔드 개발자")
                .companyUser(companyUser)
                .workLocation("서울")
                .requiredExperienceYears(3)
                .salary(5000L)
                .deadline(LocalDate.now().plusDays(30))
                .status(JobPostingStatus.ACTIVE)
                .build();
        jobPosting.setCreateDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("개인회원 목록 조회 - 검색어 없음")
    void getIndividualUsers_NoSearch() {
        // Given
        Page<IndividualUser> userPage = new PageImpl<>(Arrays.asList(individualUser));
        when(userRepository.findAllIndividualUsers(pageable)).thenReturn(userPage);
        when(applicationRepository.countByIndividualUser(individualUser)).thenReturn(3);
        
        // When
        Page<IndividualUserListResponse> result = adminService.getIndividualUsers(null, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getName()).isEqualTo("김개발");
        assertThat(result.getContent().get(0).getApplicationCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("개인회원 목록 조회 - 검색어 있음")
    void getIndividualUsers_WithSearch() {
        // Given
        String search = "김개발";
        Page<IndividualUser> userPage = new PageImpl<>(Arrays.asList(individualUser));
        when(userRepository.findIndividualUsersBySearch(search, pageable)).thenReturn(userPage);
        when(applicationRepository.countByIndividualUser(individualUser)).thenReturn(3);
        
        // When
        Page<IndividualUserListResponse> result = adminService.getIndividualUsers(search, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(userRepository).findIndividualUsersBySearch(search, pageable);
    }

    @Test
    @DisplayName("기업회원 목록 조회 - 검색어 없음")
    void getCompanyUsers_NoSearch() {
        // Given
        Page<CompanyUser> userPage = new PageImpl<>(Arrays.asList(companyUser));
        when(userRepository.findAllCompanyUsers(pageable)).thenReturn(userPage);
        when(jobPostingRepository.countByCompanyUser(companyUser)).thenReturn(5);
        
        // When
        Page<CompanyUserListResponse> result = adminService.getCompanyUsers(null, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCompanyName()).isEqualTo("테크기업");
        assertThat(result.getContent().get(0).getJobPostingCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("개인회원 상세 조회 성공")
    void getIndividualUser_Success() {
        // Given
        when(userRepository.findIndividualUserById(1L)).thenReturn(Optional.of(individualUser));
        when(applicationRepository.countByIndividualUser(individualUser)).thenReturn(3);
        
        // When
        IndividualUserListResponse result = adminService.getIndividualUser(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("김개발");
    }

    @Test
    @DisplayName("개인회원 상세 조회 실패 - 사용자 없음")
    void getIndividualUser_NotFound() {
        // Given
        when(userRepository.findIndividualUserById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> adminService.getIndividualUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Individual user not found");
    }

    @Test
    @DisplayName("채용공고 목록 조회 - 전체")
    void getJobPostings_All() {
        // Given
        Page<JobPosting> jobPage = new PageImpl<>(Arrays.asList(jobPosting));
        when(jobPostingRepository.findAllWithCompanyUser(pageable)).thenReturn(jobPage);
        when(applicationRepository.countByJobPosting(jobPosting)).thenReturn(10);
        
        // When
        Page<AdminJobPostingListResponse> result = adminService.getJobPostings(null, null, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("백엔드 개발자");
        assertThat(result.getContent().get(0).getCompanyName()).isEqualTo("테크기업");
        assertThat(result.getContent().get(0).getApplicationCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("채용공고 목록 조회 - 검색어와 상태 필터")
    void getJobPostings_WithSearchAndStatus() {
        // Given
        String search = "백엔드";
        String status = "ACTIVE";
        Page<JobPosting> jobPage = new PageImpl<>(Arrays.asList(jobPosting));
        when(jobPostingRepository.findBySearchAndStatus(search, JobPostingStatus.ACTIVE, pageable))
                .thenReturn(jobPage);
        when(applicationRepository.countByJobPosting(jobPosting)).thenReturn(10);
        
        // When
        Page<AdminJobPostingListResponse> result = adminService.getJobPostings(search, status, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(jobPostingRepository).findBySearchAndStatus(search, JobPostingStatus.ACTIVE, pageable);
    }

    @Test
    @DisplayName("채용공고 삭제 성공")
    void deleteJobPosting_Success() {
        // Given
        when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
        
        // When
        adminService.deleteJobPosting(1L);
        
        // Then
        verify(jobPostingRepository).delete(jobPosting);
    }

    @Test
    @DisplayName("채용공고 삭제 실패 - 공고 없음")
    void deleteJobPosting_NotFound() {
        // Given
        when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> adminService.deleteJobPosting(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Job posting not found");
    }
}