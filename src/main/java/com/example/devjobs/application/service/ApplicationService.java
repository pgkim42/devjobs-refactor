package com.example.devjobs.application.service;

import com.example.devjobs.application.dto.ApplicationRequestDTO;
import com.example.devjobs.application.dto.ApplicationResponseDTO;
import com.example.devjobs.application.dto.UpdateStatusRequestDTO;

import java.util.List;

public interface ApplicationService {

    ApplicationResponseDTO createApplication(ApplicationRequestDTO requestDTO, String loginId);

    void deleteApplication(Long applicationId, String loginId);

    List<ApplicationResponseDTO> getMyApplications(String loginId);

    List<ApplicationResponseDTO> getJobApplicants(Long jobPostingId, String loginId);

    void updateApplicationStatus(Long applicationId, UpdateStatusRequestDTO requestDTO, String loginId);
}
