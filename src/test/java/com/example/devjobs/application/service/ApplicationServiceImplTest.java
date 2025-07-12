package com.example.devjobs.application.service;

import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.application.dto.ApplicationResponseDTO;
import com.example.devjobs.application.entity.Application;
import com.example.devjobs.application.entity.ApplicationStatus;
import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private IndividualUser testUser;
    private CompanyUser testCompany;
    private JobPosting testJobPosting;
    private Application testApplication;
    private String testUserLoginId = "testUser";
    private String anotherUserLoginId = "anotherUser";

    @BeforeEach
    void setUp() {
        testUser = IndividualUser.builder().id(1L).loginId(testUserLoginId).name("testUser").build();
        testCompany = CompanyUser.builder().id(2L).companyName("testCompany").build();
        testJobPosting = JobPosting.builder().id(101L).companyUser(testCompany).title("Backend Developer").build();
        testApplication = Application.builder()
                .id(1L)
                .individualUser(testUser)
                .jobPosting(testJobPosting)
                .status(ApplicationStatus.APPLIED)
                .build();
    }

    @Test
    @DisplayName("지원 등록 성공")
    void createApplication_success() {
        // given
        ApplicationRequestDTO requestDTO = new ApplicationRequestDTO();
        requestDTO.setJobPostingId(101L);

        when(userRepository.findByLoginId(testUserLoginId)).thenReturn(Optional.of(testUser));
        when(jobPostingRepository.findById(101L)).thenReturn(Optional.of(testJobPosting));
        when(applicationRepository.findByJobPostingAndIndividualUser(any(), any())).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // when
        ApplicationResponseDTO responseDTO = applicationService.createApplication(requestDTO, testUserLoginId);

        // then
        assertNotNull(responseDTO);
        assertEquals(testApplication.getId(), responseDTO.getApplicationId());
        assertEquals(ApplicationStatus.APPLIED, responseDTO.getStatus());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    @DisplayName("지원 등록 실패 - 이미 지원한 공고")
    void createApplication_fail_alreadyApplied() {
        // given
        ApplicationRequestDTO requestDTO = new ApplicationRequestDTO();
        requestDTO.setJobPostingId(101L);

        when(userRepository.findByLoginId(testUserLoginId)).thenReturn(Optional.of(testUser));
        when(jobPostingRepository.findById(101L)).thenReturn(Optional.of(testJobPosting));
        when(applicationRepository.findByJobPostingAndIndividualUser(testJobPosting, testUser)).thenReturn(Optional.of(testApplication));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.createApplication(requestDTO, testUserLoginId);
        });
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("지원 취소 성공")
    void deleteApplication_success() {
        // given
        when(userRepository.findByLoginId(testUserLoginId)).thenReturn(Optional.of(testUser));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // when
        applicationService.deleteApplication(1L, testUserLoginId);

        // then
        verify(applicationRepository, times(1)).delete(testApplication);
    }

    @Test
    @DisplayName("지원 취소 실패 - 다른 사용자의 지원서")
    void deleteApplication_fail_notOwner() {
        // given
        IndividualUser anotherUser = IndividualUser.builder().id(99L).loginId(anotherUserLoginId).build();
        when(userRepository.findByLoginId(anotherUserLoginId)).thenReturn(Optional.of(anotherUser));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // when & then
        assertThrows(SecurityException.class, () -> {
            applicationService.deleteApplication(1L, anotherUserLoginId);
        });
        verify(applicationRepository, never()).delete(any());
    }
}
