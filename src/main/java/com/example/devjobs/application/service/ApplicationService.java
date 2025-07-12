package com.example.devjobs.application.service;

import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.application.dto.ApplicationResponseDTO;
import com.example.devjobs.application.dto.UpdateStatusRequestDTO;

import java.util.List;

public interface ApplicationService {
    ApplicationResponseDTO createApplication(ApplicationRequestDTO requestDTO, Long userId);

    void deleteApplication(Long applicationId, Long userId);

    List<ApplicationResponseDTO> getMyApplications(Long userId);

    List<ApplicationResponseDTO> getJobApplicants(Long jobPostingId, Long companyId);

    void updateApplicationStatus(Long applicationId, UpdateStatusRequestDTO requestDTO, Long companyId);
}

