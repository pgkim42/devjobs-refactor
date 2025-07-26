package com.example.devjobs.admin.service;

import com.example.devjobs.admin.dto.CompanyUserListResponse;
import com.example.devjobs.admin.dto.IndividualUserListResponse;
import com.example.devjobs.admin.dto.AdminJobPostingListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<IndividualUserListResponse> getIndividualUsers(String search, Pageable pageable);
    Page<CompanyUserListResponse> getCompanyUsers(String search, Pageable pageable);
    IndividualUserListResponse getIndividualUser(Long userId);
    CompanyUserListResponse getCompanyUser(Long userId);
    Page<AdminJobPostingListResponse> getJobPostings(String search, String status, Pageable pageable);
    void deleteJobPosting(Long jobId);
}