package com.example.devjobs.application.service;

import com.example.devjobs.application.dto.*;

import java.util.List;

public interface ApplicationService {
    ApplicationResponseDTO createApplication(ApplicationRequestDTO requestDTO, Long userId);

    void deleteApplication(Long applicationId, Long userId);

    List<ApplicationForIndividualResponse> getMyApplications(Long userId);

    List<ApplicationForCompanyResponse> getJobApplicants(Long jobPostingId, Long companyId);

    void updateApplicationStatus(Long applicationId, UpdateStatusRequestDTO requestDTO, Long companyId);

    // Methods for @PreAuthorize
    boolean isApplicationOwner(Long applicationId, Long userId);
    boolean isJobPostingOwner(Long jobPostingId, Long companyId);
    boolean isJobPostingOwnerByApplication(Long applicationId, Long companyId);
}

