package com.example.devjobs.application.controller;

import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
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

    @BeforeEach
    void setUp() {
        // 테스트용 유저 저장
        IndividualUser user = IndividualUser.builder()
                .loginId("testUser")
                .password(passwordEncoder.encode("password"))
                .name("testUser")
                .email("test@test.com")
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        // 테스트용 채용공고 저장
        testJobPosting = JobPosting.builder()
                .title("Test Job")
                .content("Test Content")
                .recruitJob("Backend")
                .recruitField(1)
                .postingDeadline(java.time.LocalDateTime.now().plusDays(10))
                .postingStatus(true)
                .workExperience(0)
                .jobCategory("IT")
                .skill("Java,Spring")
                .address("Test Address")
                .build();
        jobPostingRepository.save(testJobPosting);
    }

    @Test
    @DisplayName("지원 등록 API 성공")
    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createApplication_success() throws Exception {
        // given
        ApplicationRequestDTO requestDTO = new ApplicationRequestDTO();
        requestDTO.setJobPostingId((long) testJobPosting.getJobCode());

        // when & then
        mockMvc.perform(post("/api/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobPostingId").value(testJobPosting.getJobCode()));
    }
}


