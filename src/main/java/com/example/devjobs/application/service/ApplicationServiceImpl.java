package com.example.devjobs.application.service;

import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.application.dto.ApplicationResponseDTO;
import com.example.devjobs.application.dto.UpdateStatusRequestDTO;
import com.example.devjobs.application.entity.Application;
import com.example.devjobs.application.entity.ApplicationStatus;
import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApplicationResponseDTO createApplication(ApplicationRequestDTO requestDTO, String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        IndividualUser individualUser = (IndividualUser) user;

        JobPosting jobPosting = jobPostingRepository.findById(requestDTO.getJobPostingId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("채용 공고를 찾을 수 없습니다."));

        if (applicationRepository.findByJobPostingAndIndividualUser(jobPosting, individualUser).isPresent()) {
            throw new IllegalArgumentException("이미 지원한 공고입니다.");
        }

        Application application = Application.builder()
                .jobPosting(jobPosting)
                .individualUser(individualUser)
                .status(ApplicationStatus.APPLIED)
                .build();

        Application savedApplication = applicationRepository.save(application);
        return ApplicationResponseDTO.fromEntity(savedApplication);
    }

    @Override
    @Transactional
    public void deleteApplication(Long applicationId, String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원 내역을 찾을 수 없습니다."));

        if (!application.getIndividualUser().getId().equals(user.getId())) {
            throw new SecurityException("지원자 본인만 지원을 취소할 수 있습니다.");
        }

        applicationRepository.delete(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getMyApplications(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        IndividualUser individualUser = (IndividualUser) user;
        return applicationRepository.findByIndividualUser(individualUser).stream()
                .map(ApplicationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getJobApplicants(Long jobPostingId, String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("채용 공고를 찾을 수 없습니다."));

        if (!jobPosting.getCompanyUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 공고의 작성자만 지원자 목록을 조회할 수 있습니다.");
        }

        return jobPosting.getApplications().stream()
                .map(ApplicationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateApplicationStatus(Long applicationId, UpdateStatusRequestDTO requestDTO, String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원 내역을 찾을 수 없습니다."));

        JobPosting jobPosting = application.getJobPosting();
        if (!jobPosting.getCompanyUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 공고의 작성자만 지원 상태를 변경할 수 있습니다.");
        }

        application.setStatus(requestDTO.getStatus());
        applicationRepository.save(application);
    }
}
