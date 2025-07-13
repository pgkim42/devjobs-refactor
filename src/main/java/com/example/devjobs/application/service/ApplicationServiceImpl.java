package com.example.devjobs.application.service;

import com.example.devjobs.application.dto.ApplicationForCompanyResponse;
import com.example.devjobs.application.dto.ApplicationForIndividualResponse;
import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.application.dto.ApplicationResponseDTO;
import com.example.devjobs.application.dto.UpdateStatusRequestDTO;
import com.example.devjobs.application.entity.Application;
import com.example.devjobs.application.entity.ApplicationStatus;
import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.repository.IndividualUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("applicationServiceImpl") // Add a bean name to be used in @PreAuthorize
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final IndividualUserRepository individualUserRepository;

    @Override
    @Transactional
    public ApplicationResponseDTO createApplication(ApplicationRequestDTO requestDTO, Long userId) {
        IndividualUser individualUser = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        JobPosting jobPosting = jobPostingRepository.findById(requestDTO.getJobPostingId())
                .orElseThrow(() -> new EntityNotFoundException("채용 공고를 찾을 수 없습니다."));

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
    public void deleteApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("지원 내역을 찾을 수 없습니다."));
        // Authorization check is now handled by @PreAuthorize in the controller
        applicationRepository.delete(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationForIndividualResponse> getMyApplications(Long userId) {
        IndividualUser individualUser = individualUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return applicationRepository.findByIndividualUser(individualUser).stream()
                .map(ApplicationForIndividualResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationForCompanyResponse> getJobApplicants(Long jobPostingId, Long companyId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new EntityNotFoundException("채용 공고를 찾을 수 없습니다."));
        // Authorization check is now handled by @PreAuthorize in the controller
        return applicationRepository.findByJobPosting(jobPosting).stream()
                .map(ApplicationForCompanyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateApplicationStatus(Long applicationId, UpdateStatusRequestDTO requestDTO, Long companyId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("지원 내역을 찾을 수 없습니다."));
        // Authorization check is now handled by @PreAuthorize in the controller
        application.setStatus(requestDTO.getStatus());
        applicationRepository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isApplicationOwner(Long applicationId, Long userId) {
        return applicationRepository.findById(applicationId)
                .map(application -> application.getIndividualUser().getId().equals(userId))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isJobPostingOwner(Long jobPostingId, Long companyId) {
        return jobPostingRepository.findById(jobPostingId)
                .map(jobPosting -> jobPosting.getCompanyUser().getId().equals(companyId))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isJobPostingOwnerByApplication(Long applicationId, Long companyId) {
        return applicationRepository.findById(applicationId)
                .map(application -> application.getJobPosting().getCompanyUser().getId().equals(companyId))
                .orElse(false);
    }
}
