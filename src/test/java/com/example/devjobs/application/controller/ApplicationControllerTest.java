package com.example.devjobs.application.controller;

import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    private JobPosting testJobPosting;
    private CompanyUser testCompany;

    @BeforeEach
    void setUp() {
        // 테스트용 개인 유저 저장
        IndividualUser user = IndividualUser.builder()
                .loginId("testUser")
                .password(passwordEncoder.encode("password"))
                .name("testUser")
                .email("test@test.com")
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        // 테스트용 기업 유저 저장
        testCompany = CompanyUser.builder()
                .loginId("testCompany")
                .password(passwordEncoder.encode("password"))
                .name("testCompany")
                .email("company@test.com")
                .role("ROLE_COMPANY")
                .companyName("Test Company")
                .companyAddress("Test Address")
                .companyCode("123-45-67890")
                .ceoName("Test CEO")
                .build();
        userRepository.save(testCompany);

        // 테스트용 채용공고 저장
        testJobPosting = JobPosting.builder()
                .companyUser(testCompany)
                .title("Test Job")
                .content("Test Content")
                .workLocation("Test Location")
                .deadline(java.time.LocalDate.now().plusDays(10))
                .build();
        jobPostingRepository.save(testJobPosting);
    }

    @Test
    @DisplayName("지원 등록 API 성공")
    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createApplication_success() throws Exception {
        // given
        ApplicationRequestDTO requestDTO = new ApplicationRequestDTO();
        requestDTO.setJobPostingId(testJobPosting.getId());

        // when & then
        mockMvc.perform(post("/api/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobPostingId").value(testJobPosting.getId()));
    }
}


