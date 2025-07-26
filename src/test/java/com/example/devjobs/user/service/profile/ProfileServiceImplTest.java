package com.example.devjobs.user.service.profile;

import com.example.devjobs.common.file.FileService;
import com.example.devjobs.user.dto.profile.*;
import com.example.devjobs.user.entity.*;
import com.example.devjobs.user.entity.enums.WorkStatus;
import com.example.devjobs.user.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private IndividualUserRepository individualUserRepository;
    @Mock
    private CompanyUserRepository companyUserRepository;
    @Mock
    private WorkExperienceRepository workExperienceRepository;
    @Mock
    private EducationRepository educationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private LanguageSkillRepository languageSkillRepository;
    @Mock
    private CertificationRepository certificationRepository;
    @Mock
    private FileService fileService;
    
    @InjectMocks
    private ProfileServiceImpl profileService;
    
    private IndividualUser individualUser;
    private CompanyUser companyUser;
    private final Long userId = 1L;
    private final Long otherId = 999L;
    
    @BeforeEach
    void setUp() {
        individualUser = IndividualUser.builder()
                .id(userId)
                .loginId("testuser")
                .name("테스트유저")
                .email("test@test.com")
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .skills(new HashSet<>())
                .build();
                
        companyUser = CompanyUser.builder()
                .id(userId)
                .loginId("company")
                .name("담당자")
                .email("company@test.com")
                .companyName("테스트회사")
                .companyCode("123-45-67890")
                .build();
    }
    
    @Nested
    @DisplayName("개인 프로필 관리")
    class IndividualProfileTest {
        
        @Test
        @DisplayName("개인 프로필 조회 성공")
        void getIndividualProfile_Success() {
            // given
            when(individualUserRepository.findById(userId)).thenReturn(Optional.of(individualUser));
            
            // when
            IndividualProfileResponse response = profileService.getIndividualProfile(userId);
            
            // then
            assertNotNull(response);
            assertEquals("테스트유저", response.getName());
            assertEquals("test@test.com", response.getEmail());
        }
        
        @Test
        @DisplayName("개인 프로필 조회 실패 - 사용자 없음")
        void getIndividualProfile_NotFound() {
            // given
            when(individualUserRepository.findById(userId)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class, 
                () -> profileService.getIndividualProfile(userId));
        }
        
        @Test
        @DisplayName("개인 프로필 수정 성공")
        void updateIndividualProfile_Success() {
            // given
            UpdateIndividualProfileRequest request = UpdateIndividualProfileRequest.builder()
                    .name("수정된이름")
                    .phoneNumber("010-9999-8888")
                    .address("서울시 송파구")
                    .portfolioUrl("https://portfolio.com")
                    .headline("백엔드 개발자")
                    .workStatus(WorkStatus.LOOKING_FOR_JOB)
                    .build();
                    
            when(individualUserRepository.findById(userId)).thenReturn(Optional.of(individualUser));
            
            // when
            IndividualProfileResponse response = profileService.updateIndividualProfile(userId, request);
            
            // then
            assertEquals("수정된이름", individualUser.getName());
            assertEquals("010-9999-8888", individualUser.getPhoneNumber());
            verify(individualUserRepository, times(1)).findById(userId);
        }
        
        @Test
        @DisplayName("이력서 업로드 성공")
        void uploadResume_Success() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "resume.pdf", "application/pdf", "test".getBytes());
            when(individualUserRepository.findById(userId)).thenReturn(Optional.of(individualUser));
            when(fileService.storeFile(any(MultipartFile.class))).thenReturn("stored-file-name");
            when(fileService.getFileUrl(anyString())).thenReturn("https://files.com/resume.pdf");
            
            // when
            IndividualProfileResponse response = profileService.uploadResume(userId, file);
            
            // then
            assertEquals("https://files.com/resume.pdf", individualUser.getResumeUrl());
            verify(fileService, times(1)).storeFile(file);
            verify(individualUserRepository, times(1)).save(individualUser);
        }
    }
    
    @Nested
    @DisplayName("경력 관리")
    class WorkExperienceTest {
        
        private WorkExperience workExperience;
        private WorkExperienceRequest request;
        
        @BeforeEach
        void setUp() {
            workExperience = WorkExperience.builder()
                    .id(1L)
                    .companyName("이전회사")
                    .position("백엔드 개발자")
                    .individualUser(individualUser)
                    .build();
                    
            request = WorkExperienceRequest.builder()
                    .companyName("새회사")
                    .department("개발팀")
                    .position("시니어 개발자")
                    .startDate(LocalDate.of(2020, 1, 1))
                    .endDate(LocalDate.of(2023, 12, 31))
                    .description("백엔드 개발")
                    .build();
        }
        
        @Test
        @DisplayName("경력 추가 성공")
        void addWorkExperience_Success() {
            // given
            when(individualUserRepository.findById(userId)).thenReturn(Optional.of(individualUser));
            when(workExperienceRepository.save(any(WorkExperience.class))).thenReturn(workExperience);
            
            // when
            WorkExperienceDto response = profileService.addWorkExperience(userId, request);
            
            // then
            assertNotNull(response);
            verify(workExperienceRepository, times(1)).save(any(WorkExperience.class));
        }
        
        @Test
        @DisplayName("경력 수정 성공")
        void updateWorkExperience_Success() {
            // given
            when(workExperienceRepository.findById(1L)).thenReturn(Optional.of(workExperience));
            
            // when
            WorkExperienceDto response = profileService.updateWorkExperience(userId, 1L, request);
            
            // then
            assertEquals("새회사", workExperience.getCompanyName());
            assertEquals("시니어 개발자", workExperience.getPosition());
        }
        
        @Test
        @DisplayName("경력 수정 실패 - 권한 없음")
        void updateWorkExperience_AccessDenied() {
            // given
            when(workExperienceRepository.findById(1L)).thenReturn(Optional.of(workExperience));
            
            // when & then
            assertThrows(AccessDeniedException.class, 
                () -> profileService.updateWorkExperience(otherId, 1L, request));
        }
        
        @Test
        @DisplayName("경력 삭제 성공")
        void deleteWorkExperience_Success() {
            // given
            when(workExperienceRepository.findById(1L)).thenReturn(Optional.of(workExperience));
            
            // when
            assertDoesNotThrow(() -> profileService.deleteWorkExperience(userId, 1L));
            
            // then
            verify(workExperienceRepository, times(1)).delete(workExperience);
        }
        
        @Test
        @DisplayName("경력 삭제 실패 - 권한 없음")
        void deleteWorkExperience_AccessDenied() {
            // given
            when(workExperienceRepository.findById(1L)).thenReturn(Optional.of(workExperience));
            
            // when & then
            assertThrows(AccessDeniedException.class, 
                () -> profileService.deleteWorkExperience(otherId, 1L));
        }
    }
    
    @Nested
    @DisplayName("스킬 관리")
    class SkillTest {
        
        @Test
        @DisplayName("스킬 추가 성공 - 기존 스킬")
        void addSkill_ExistingSkill_Success() {
            // given
            Skill existingSkill = Skill.builder().id(1L).name("Java").build();
            SkillRequest request = new SkillRequest();
            setField(request, "name", "Java");
            
            when(individualUserRepository.findById(userId)).thenReturn(Optional.of(individualUser));
            when(skillRepository.findByName("Java")).thenReturn(Optional.of(existingSkill));
            
            // when
            Set<SkillDto> response = profileService.addSkill(userId, request);
            
            // then
            assertTrue(individualUser.getSkills().contains(existingSkill));
            verify(skillRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("스킬 추가 성공 - 새로운 스킬")
        void addSkill_NewSkill_Success() {
            // given
            Skill newSkill = Skill.builder().id(1L).name("Python").build();
            SkillRequest request = new SkillRequest();
            setField(request, "name", "Python");
            
            when(individualUserRepository.findById(userId)).thenReturn(Optional.of(individualUser));
            when(skillRepository.findByName("Python")).thenReturn(Optional.empty());
            when(skillRepository.save(any(Skill.class))).thenReturn(newSkill);
            
            // when
            Set<SkillDto> response = profileService.addSkill(userId, request);
            
            // then
            assertTrue(individualUser.getSkills().contains(newSkill));
            verify(skillRepository, times(1)).save(any(Skill.class));
        }
        
        @Test
        @DisplayName("스킬 삭제 성공")
        void deleteSkill_Success() {
            // given
            Skill skill = Skill.builder().id(1L).name("Java").build();
            individualUser.getSkills().add(skill);
            
            when(individualUserRepository.findById(userId)).thenReturn(Optional.of(individualUser));
            when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
            
            // when
            profileService.deleteSkill(userId, 1L);
            
            // then
            assertFalse(individualUser.getSkills().contains(skill));
        }
    }
    
    @Nested
    @DisplayName("기업 프로필 관리")
    class CompanyProfileTest {
        
        @Test
        @DisplayName("기업 프로필 조회 성공")
        void getCompanyProfile_Success() {
            // given
            when(companyUserRepository.findById(userId)).thenReturn(Optional.of(companyUser));
            
            // when
            CompanyProfileResponse response = profileService.getCompanyProfile(userId);
            
            // then
            assertNotNull(response);
            assertEquals("테스트회사", response.getCompanyName());
            assertEquals("123-45-67890", response.getCompanyCode());
        }
        
        @Test
        @DisplayName("기업 프로필 수정 성공")
        void updateCompanyProfile_Success() {
            // given
            UpdateCompanyProfileRequest request = new UpdateCompanyProfileRequest(
                    "서울시 강남구 테헤란로",
                    "IT",
                    "https://testcompany.com",
                    "https://logo.com/test.png"
            );
                    
            when(companyUserRepository.findById(userId)).thenReturn(Optional.of(companyUser));
            
            // when
            CompanyProfileResponse response = profileService.updateCompanyProfile(userId, request);
            
            // then
            assertEquals("서울시 강남구 테헤란로", companyUser.getCompanyAddress());
            assertEquals("IT", companyUser.getIndustry());
        }
        
        @Test
        @DisplayName("기업 로고 업로드 성공")
        void uploadCompanyLogo_Success() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "logo.png", "image/png", "test".getBytes());
            when(companyUserRepository.findById(userId)).thenReturn(Optional.of(companyUser));
            when(fileService.storeFile(any(MultipartFile.class))).thenReturn("stored-logo");
            when(fileService.getFileUrl(anyString())).thenReturn("https://files.com/logo.png");
            
            // when
            CompanyProfileResponse response = profileService.uploadCompanyLogo(userId, file);
            
            // then
            assertEquals("https://files.com/logo.png", companyUser.getLogoUrl());
            verify(companyUserRepository, times(1)).save(companyUser);
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