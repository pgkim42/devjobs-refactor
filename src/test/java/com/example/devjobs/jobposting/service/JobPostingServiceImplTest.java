package com.example.devjobs.jobposting.service;

import com.example.devjobs.jobcategory.entity.JobCategory;
import com.example.devjobs.jobcategory.repository.JobCategoryRepository;
import com.example.devjobs.jobposting.dto.JobPostingRequest;
import com.example.devjobs.jobposting.dto.JobPostingResponse;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.entity.enums.JobPostingStatus;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.repository.CompanyUserRepository;
import com.example.devjobs.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobPostingServiceImplTest {

    @Mock
    private JobPostingRepository jobPostingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyUserRepository companyUserRepository;
    @Mock
    private JobCategoryRepository jobCategoryRepository;
    
    @InjectMocks
    private JobPostingServiceImpl jobPostingService;
    
    private CompanyUser companyUser;
    private JobCategory jobCategory;
    private JobPosting jobPosting;
    private JobPostingRequest.Create createRequest;
    private JobPostingRequest.Update updateRequest;
    
    @BeforeEach
    void setUp() {
        companyUser = CompanyUser.builder()
                .id(1L)
                .companyName("테스트회사")
                .companyAddress("서울시 강남구")
                .industry("IT")
                .build();
                
        jobCategory = JobCategory.builder()
                .id(1L)
                .categoryName("백엔드 개발자")
                .build();
                
        jobPosting = JobPosting.builder()
                .id(1L)
                .companyUser(companyUser)
                .jobCategory(jobCategory)
                .title("백엔드 개발자 채용")
                .content("저희 회사에서 백엔드 개발자를 채용합니다.")
                .salary(50000000L)
                .deadline(LocalDate.now().plusDays(30))
                .workLocation("서울시 강남구")
                .requiredExperienceYears(3)
                .viewCount(0L)
                .status(JobPostingStatus.ACTIVE)
                .build();
                
        createRequest = new JobPostingRequest.Create();
        setField(createRequest, "title", "백엔드 개발자 채용");
        setField(createRequest, "content", "저희 회사에서 백엔드 개발자를 채용합니다.");
        setField(createRequest, "salary", 50000000L);
        setField(createRequest, "deadline", LocalDate.now().plusDays(30));
        setField(createRequest, "workLocation", "서울시 강남구");
        setField(createRequest, "requiredExperienceYears", 3);
        setField(createRequest, "jobCategoryId", 1L);
        
        updateRequest = new JobPostingRequest.Update();
        setField(updateRequest, "title", "수정된 제목");
        setField(updateRequest, "content", "수정된 내용입니다.");
        setField(updateRequest, "salary", 60000000L);
    }
    
    @Nested
    @DisplayName("채용공고 생성")
    class CreateJobPostingTest {
        
        @Test
        @DisplayName("생성 성공")
        void createJobPosting_Success() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(companyUser));
            when(jobCategoryRepository.findById(1L)).thenReturn(Optional.of(jobCategory));
            when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);
            
            // when
            JobPostingResponse.Detail response = jobPostingService.createJobPosting(createRequest, 1L);
            
            // then
            assertNotNull(response);
            assertEquals("백엔드 개발자 채용", response.getTitle());
            assertEquals(50000000L, response.getSalary());
            verify(jobPostingRepository, times(1)).save(any(JobPosting.class));
        }
        
        @Test
        @DisplayName("생성 실패 - 회사 없음")
        void createJobPosting_CompanyNotFound() {
            // given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> jobPostingService.createJobPosting(createRequest, 999L));
            verify(jobPostingRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("생성 실패 - 카테고리 없음")
        void createJobPosting_CategoryNotFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(companyUser));
            when(jobCategoryRepository.findById(999L)).thenReturn(Optional.empty());
            setField(createRequest, "jobCategoryId", 999L);
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> jobPostingService.createJobPosting(createRequest, 1L));
            verify(jobPostingRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("채용공고 조회")
    class GetJobPostingTest {
        
        @Test
        @DisplayName("상세 조회 성공 - 조회수 증가")
        void getJobPosting_Success() {
            // given
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);
            
            // when
            JobPostingResponse.Detail response = jobPostingService.getJobPosting(1L);
            
            // then
            assertNotNull(response);
            assertEquals("백엔드 개발자 채용", response.getTitle());
            assertEquals(1L, jobPosting.getViewCount()); // 조회수 증가 확인
            verify(jobPostingRepository, times(1)).save(jobPosting);
        }
        
        @Test
        @DisplayName("상세 조회 실패 - 공고 없음")
        void getJobPosting_NotFound() {
            // given
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> jobPostingService.getJobPosting(999L));
        }
        
        @Test
        @DisplayName("회사별 공고 목록 조회 성공")
        void getCompanyJobPostings_Success() {
            // given
            List<JobPosting> jobPostings = Arrays.asList(jobPosting);
            when(companyUserRepository.findById(1L)).thenReturn(Optional.of(companyUser));
            when(jobPostingRepository.findByCompanyUser(companyUser)).thenReturn(jobPostings);
            
            // when
            List<JobPostingResponse.Simple> responses = jobPostingService.getCompanyJobPostings(1L);
            
            // then
            assertEquals(1, responses.size());
            assertEquals("백엔드 개발자 채용", responses.get(0).getTitle());
            assertEquals("테스트회사", responses.get(0).getCompanyName());
        }
    }
    
    @Nested
    @DisplayName("채용공고 검색")
    class SearchJobPostingTest {
        
        @Test
        @DisplayName("검색 성공")
        void searchJobPostings_Success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<JobPosting> jobPostingPage = new PageImpl<>(Arrays.asList(jobPosting), pageable, 1);
            when(jobPostingRepository.search(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(jobPostingPage);
            
            // when
            Page<JobPostingResponse.Simple> response = jobPostingService.searchJobPostings(
                    "백엔드", "서울", 40000000, 60000000, 1, 5, 1L, pageable);
            
            // then
            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            assertEquals("백엔드 개발자 채용", response.getContent().get(0).getTitle());
        }
    }
    
    @Nested
    @DisplayName("채용공고 수정")
    class UpdateJobPostingTest {
        
        @Test
        @DisplayName("수정 성공")
        void updateJobPosting_Success() {
            // given
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);
            
            // when
            JobPostingResponse.Detail response = jobPostingService.updateJobPosting(1L, updateRequest, 1L);
            
            // then
            assertNotNull(response);
            verify(jobPostingRepository, times(1)).save(jobPosting);
        }
        
        @Test
        @DisplayName("수정 실패 - 권한 없음")
        void updateJobPosting_AccessDenied() {
            // given
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            
            // when & then
            assertThrows(AccessDeniedException.class,
                () -> jobPostingService.updateJobPosting(1L, updateRequest, 999L));
            verify(jobPostingRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("수정 실패 - 공고 없음")
        void updateJobPosting_NotFound() {
            // given
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> jobPostingService.updateJobPosting(999L, updateRequest, 1L));
        }
        
        @Test
        @DisplayName("카테고리 변경 성공")
        void updateJobPosting_WithCategoryChange_Success() {
            // given
            JobCategory newCategory = JobCategory.builder()
                    .id(2L)
                    .categoryName("프론트엔드 개발자")
                    .build();
            setField(updateRequest, "jobCategoryId", 2L);
            
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(jobCategoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
            when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);
            
            // when
            JobPostingResponse.Detail response = jobPostingService.updateJobPosting(1L, updateRequest, 1L);
            
            // then
            assertNotNull(response);
            verify(jobCategoryRepository, times(1)).findById(2L);
        }
    }
    
    @Nested
    @DisplayName("채용공고 삭제")
    class DeleteJobPostingTest {
        
        @Test
        @DisplayName("삭제 성공")
        void deleteJobPosting_Success() {
            // given
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            
            // when
            assertDoesNotThrow(() -> jobPostingService.deleteJobPosting(1L, 1L));
            
            // then
            verify(jobPostingRepository, times(1)).delete(jobPosting);
        }
        
        @Test
        @DisplayName("삭제 실패 - 권한 없음")
        void deleteJobPosting_AccessDenied() {
            // given
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            
            // when & then
            assertThrows(AccessDeniedException.class,
                () -> jobPostingService.deleteJobPosting(1L, 999L));
            verify(jobPostingRepository, never()).delete(any());
        }
        
        @Test
        @DisplayName("삭제 실패 - 공고 없음")
        void deleteJobPosting_NotFound() {
            // given
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> jobPostingService.deleteJobPosting(999L, 1L));
        }
    }
    
    // Reflection helper
    private void setField(Object object, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}