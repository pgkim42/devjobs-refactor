package com.example.devjobs.application.service;

import com.example.devjobs.application.dto.*;
import com.example.devjobs.application.entity.Application;
import com.example.devjobs.application.entity.ApplicationStatus;
import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.repository.IndividualUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
    private IndividualUserRepository individualUserRepository;
    
    @InjectMocks
    private ApplicationServiceImpl applicationService;
    
    private IndividualUser individualUser;
    private CompanyUser companyUser;
    private JobPosting jobPosting;
    private Application application;
    
    @BeforeEach
    void setUp() {
        individualUser = IndividualUser.builder()
                .id(1L)
                .loginId("testuser")
                .name("테스트유저")
                .email("test@test.com")
                .build();
                
        companyUser = CompanyUser.builder()
                .id(2L)
                .loginId("company")
                .companyName("테스트회사")
                .build();
                
        jobPosting = JobPosting.builder()
                .id(1L)
                .title("백엔드 개발자 채용")
                .companyUser(companyUser)
                .deadline(LocalDate.now().plusDays(30))
                .build();
                
        application = Application.builder()
                .id(1L)
                .individualUser(individualUser)
                .jobPosting(jobPosting)
                .status(ApplicationStatus.APPLIED)
                .build();
    }
    
    @Nested
    @DisplayName("지원서 생성")
    class CreateApplicationTest {
        
        @Test
        @DisplayName("지원 성공")
        void createApplication_Success() {
            // given
            ApplicationRequestDTO request = new ApplicationRequestDTO(1L);
            when(individualUserRepository.findById(1L)).thenReturn(Optional.of(individualUser));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(applicationRepository.findByJobPostingAndIndividualUser(jobPosting, individualUser))
                    .thenReturn(Optional.empty());
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            
            // when
            ApplicationResponseDTO response = applicationService.createApplication(request, 1L);
            
            // then
            assertNotNull(response);
            assertEquals(1L, response.getApplicationId());
            assertEquals(ApplicationStatus.APPLIED, response.getStatus());
            verify(applicationRepository, times(1)).save(any(Application.class));
        }
        
        @Test
        @DisplayName("지원 실패 - 사용자 없음")
        void createApplication_UserNotFound() {
            // given
            ApplicationRequestDTO request = new ApplicationRequestDTO(1L);
            when(individualUserRepository.findById(1L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> applicationService.createApplication(request, 1L));
            verify(applicationRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("지원 실패 - 채용공고 없음")
        void createApplication_JobPostingNotFound() {
            // given
            ApplicationRequestDTO request = new ApplicationRequestDTO(999L);
            when(individualUserRepository.findById(1L)).thenReturn(Optional.of(individualUser));
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> applicationService.createApplication(request, 1L));
            verify(applicationRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("지원 실패 - 중복 지원")
        void createApplication_DuplicateApplication() {
            // given
            ApplicationRequestDTO request = new ApplicationRequestDTO(1L);
            when(individualUserRepository.findById(1L)).thenReturn(Optional.of(individualUser));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(applicationRepository.findByJobPostingAndIndividualUser(jobPosting, individualUser))
                    .thenReturn(Optional.of(application));
            
            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> applicationService.createApplication(request, 1L));
            verify(applicationRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("지원서 삭제")
    class DeleteApplicationTest {
        
        @Test
        @DisplayName("삭제 성공")
        void deleteApplication_Success() {
            // given
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            
            // when
            assertDoesNotThrow(() -> applicationService.deleteApplication(1L, 1L));
            
            // then
            verify(applicationRepository, times(1)).delete(application);
        }
        
        @Test
        @DisplayName("삭제 실패 - 지원서 없음")
        void deleteApplication_NotFound() {
            // given
            when(applicationRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> applicationService.deleteApplication(999L, 1L));
            verify(applicationRepository, never()).delete(any());
        }
    }
    
    @Nested
    @DisplayName("지원 목록 조회")
    class GetApplicationsTest {
        
        @Test
        @DisplayName("개인 지원 목록 조회 성공")
        void getMyApplications_Success() {
            // given
            List<Application> applications = Arrays.asList(application);
            when(individualUserRepository.findById(1L)).thenReturn(Optional.of(individualUser));
            when(applicationRepository.findByIndividualUser(individualUser)).thenReturn(applications);
            
            // when
            List<ApplicationForIndividualResponse> responses = applicationService.getMyApplications(1L);
            
            // then
            assertEquals(1, responses.size());
            assertEquals("백엔드 개발자 채용", responses.get(0).getJobPostingTitle());
            assertEquals("테스트회사", responses.get(0).getCompanyName());
        }
        
        @Test
        @DisplayName("개인 지원 목록 조회 실패 - 사용자 없음")
        void getMyApplications_UserNotFound() {
            // given
            when(individualUserRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> applicationService.getMyApplications(999L));
        }
        
        @Test
        @DisplayName("기업 지원자 목록 조회 성공")
        void getJobApplicants_Success() {
            // given
            List<Application> applications = Arrays.asList(application);
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(applicationRepository.findByJobPosting(jobPosting)).thenReturn(applications);
            
            // when
            List<ApplicationForCompanyResponse> responses = applicationService.getJobApplicants(1L, 2L);
            
            // then
            assertEquals(1, responses.size());
            assertEquals("테스트유저", responses.get(0).getApplicantName());
            assertEquals("test@test.com", responses.get(0).getApplicantEmail());
        }
        
        @Test
        @DisplayName("기업 지원자 목록 조회 실패 - 채용공고 없음")
        void getJobApplicants_JobPostingNotFound() {
            // given
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> applicationService.getJobApplicants(999L, 2L));
        }
    }
    
    @Nested
    @DisplayName("지원 상태 업데이트")
    class UpdateStatusTest {
        
        @Test
        @DisplayName("상태 업데이트 성공")
        void updateApplicationStatus_Success() {
            // given
            UpdateStatusRequestDTO request = new UpdateStatusRequestDTO(ApplicationStatus.PASSED);
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            
            // when
            applicationService.updateApplicationStatus(1L, request, 2L);
            
            // then
            assertEquals(ApplicationStatus.PASSED, application.getStatus());
            verify(applicationRepository, times(1)).save(application);
        }
        
        @Test
        @DisplayName("상태 업데이트 실패 - 지원서 없음")
        void updateApplicationStatus_NotFound() {
            // given
            UpdateStatusRequestDTO request = new UpdateStatusRequestDTO(ApplicationStatus.PASSED);
            when(applicationRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> applicationService.updateApplicationStatus(999L, request, 2L));
            verify(applicationRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("권한 검증")
    class AuthorizationTest {
        
        @Test
        @DisplayName("지원서 소유자 확인 - true")
        void isApplicationOwner_True() {
            // given
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            
            // when
            boolean result = applicationService.isApplicationOwner(1L, 1L);
            
            // then
            assertTrue(result);
        }
        
        @Test
        @DisplayName("지원서 소유자 확인 - false")
        void isApplicationOwner_False() {
            // given
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            
            // when
            boolean result = applicationService.isApplicationOwner(1L, 999L);
            
            // then
            assertFalse(result);
        }
        
        @Test
        @DisplayName("지원서 소유자 확인 - 지원서 없음")
        void isApplicationOwner_NotFound() {
            // given
            when(applicationRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when
            boolean result = applicationService.isApplicationOwner(999L, 1L);
            
            // then
            assertFalse(result);
        }
        
        @Test
        @DisplayName("채용공고 소유자 확인 - true")
        void isJobPostingOwner_True() {
            // given
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            
            // when
            boolean result = applicationService.isJobPostingOwner(1L, 2L);
            
            // then
            assertTrue(result);
        }
        
        @Test
        @DisplayName("채용공고 소유자 확인 - false")
        void isJobPostingOwner_False() {
            // given
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            
            // when
            boolean result = applicationService.isJobPostingOwner(1L, 999L);
            
            // then
            assertFalse(result);
        }
        
        @Test
        @DisplayName("지원서를 통한 채용공고 소유자 확인 - true")
        void isJobPostingOwnerByApplication_True() {
            // given
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            
            // when
            boolean result = applicationService.isJobPostingOwnerByApplication(1L, 2L);
            
            // then
            assertTrue(result);
        }
        
        @Test
        @DisplayName("지원서를 통한 채용공고 소유자 확인 - false")
        void isJobPostingOwnerByApplication_False() {
            // given
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            
            // when
            boolean result = applicationService.isJobPostingOwnerByApplication(1L, 999L);
            
            // then
            assertFalse(result);
        }
    }
}