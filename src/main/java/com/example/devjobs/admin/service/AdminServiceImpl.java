package com.example.devjobs.admin.service;

import com.example.devjobs.admin.dto.CompanyUserListResponse;
import com.example.devjobs.admin.dto.IndividualUserListResponse;
import com.example.devjobs.admin.dto.AdminJobPostingListResponse;
import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.entity.enums.JobPostingStatus;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;

    @Override
    public Page<IndividualUserListResponse> getIndividualUsers(String search, Pageable pageable) {
        Page<IndividualUser> users;
        
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findIndividualUsersBySearch(search, pageable);
        } else {
            users = userRepository.findAllIndividualUsers(pageable);
        }
        
        List<IndividualUserListResponse> responses = users.getContent().stream()
                .map(this::mapToIndividualResponse)
                .collect(Collectors.toList());
                
        return new PageImpl<>(responses, pageable, users.getTotalElements());
    }

    @Override
    public Page<CompanyUserListResponse> getCompanyUsers(String search, Pageable pageable) {
        Page<CompanyUser> users;
        
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findCompanyUsersBySearch(search, pageable);
        } else {
            users = userRepository.findAllCompanyUsers(pageable);
        }
        
        List<CompanyUserListResponse> responses = users.getContent().stream()
                .map(this::mapToCompanyResponse)
                .collect(Collectors.toList());
                
        return new PageImpl<>(responses, pageable, users.getTotalElements());
    }

    @Override
    public IndividualUserListResponse getIndividualUser(Long userId) {
        IndividualUser user = userRepository.findIndividualUserById(userId)
                .orElseThrow(() -> new RuntimeException("Individual user not found"));
        return mapToIndividualResponse(user);
    }

    @Override
    public CompanyUserListResponse getCompanyUser(Long userId) {
        CompanyUser user = userRepository.findCompanyUserById(userId)
                .orElseThrow(() -> new RuntimeException("Company user not found"));
        return mapToCompanyResponse(user);
    }

    private IndividualUserListResponse mapToIndividualResponse(IndividualUser user) {
        int applicationCount = applicationRepository.countByIndividualUser(user);
        
        return IndividualUserListResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhoneNumber())
                .address(user.getAddress())
                .applicationCount(applicationCount)
                .createdAt(user.getCreateDate())
                .isActive(true) // 추후 상태 필드 추가 시 수정
                .build();
    }

    private CompanyUserListResponse mapToCompanyResponse(CompanyUser user) {
        int jobPostingCount = jobPostingRepository.countByCompanyUser(user);
        
        return CompanyUserListResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .companyName(user.getCompanyName())
                .businessNumber(user.getCompanyCode())
                .ceoName(user.getCeoName())
                .contactPersonName(user.getName())
                .address(user.getCompanyAddress())
                .jobPostingCount(jobPostingCount)
                .createdAt(user.getCreateDate())
                .isActive(true) // 추후 상태 필드 추가 시 수정
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AdminJobPostingListResponse> getJobPostings(String search, String status, Pageable pageable) {
        Page<JobPosting> jobPostings;
        
        if (search != null && !search.trim().isEmpty() && status != null && !status.trim().isEmpty()) {
            JobPostingStatus postingStatus = JobPostingStatus.valueOf(status);
            jobPostings = jobPostingRepository.findBySearchAndStatus(search, postingStatus, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            jobPostings = jobPostingRepository.findBySearch(search, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            JobPostingStatus postingStatus = JobPostingStatus.valueOf(status);
            jobPostings = jobPostingRepository.findByStatus(postingStatus, pageable);
        } else {
            jobPostings = jobPostingRepository.findAllWithCompanyUser(pageable);
        }
        
        List<AdminJobPostingListResponse> responses = jobPostings.getContent().stream()
                .map(this::mapToJobPostingResponse)
                .collect(Collectors.toList());
                
        return new PageImpl<>(responses, pageable, jobPostings.getTotalElements());
    }
    
    @Override
    @Transactional
    public void deleteJobPosting(Long jobId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));
        jobPostingRepository.delete(jobPosting);
    }
    
    private AdminJobPostingListResponse mapToJobPostingResponse(JobPosting jobPosting) {
        int applicationCount = applicationRepository.countByJobPosting(jobPosting);
        
        return AdminJobPostingListResponse.builder()
                .id(jobPosting.getId())
                .title(jobPosting.getTitle())
                .companyName(jobPosting.getCompanyUser().getCompanyName())
                .companyUserId(jobPosting.getCompanyUser().getId())
                .categoryName(jobPosting.getJobCategory() != null ? jobPosting.getJobCategory().getCategoryName() : null)
                .workLocation(jobPosting.getWorkLocation())
                .requiredExperienceYears(jobPosting.getRequiredExperienceYears())
                .salary(jobPosting.getSalary())
                .deadline(jobPosting.getDeadline())
                .status(jobPosting.getStatus())
                .viewCount(0) // 조회수 필드가 없다면 0으로
                .applicationCount(applicationCount)
                .createdAt(jobPosting.getCreateDate())
                .updatedAt(jobPosting.getUpdateDate())
                .build();
    }
}