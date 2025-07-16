package com.example.devjobs.jobposting.service;

import com.example.devjobs.jobcategory.entity.JobCategory;
import com.example.devjobs.jobcategory.repository.JobCategoryRepository;
import com.example.devjobs.jobposting.dto.JobPostingRequest;
import com.example.devjobs.jobposting.dto.JobPostingResponse;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final JobCategoryRepository jobCategoryRepository; // 의존성 추가

    @Override
    public JobPostingResponse.Detail createJobPosting(JobPostingRequest.Create request, Long companyUserId) {
        CompanyUser companyUser = (CompanyUser) userRepository.findById(companyUserId)
                .orElseThrow(() -> new EntityNotFoundException("Company user not found with id: " + companyUserId));

        JobCategory jobCategory = jobCategoryRepository.findById(request.getJobCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("JobCategory not found with id: " + request.getJobCategoryId()));

        JobPosting jobPosting = JobPosting.builder()
                .companyUser(companyUser)
                .jobCategory(jobCategory)
                .title(request.getTitle())
                .content(request.getContent())
                .salary(request.getSalary())
                .deadline(request.getDeadline())
                .workLocation(request.getWorkLocation())
                .requiredExperienceYears(request.getRequiredExperienceYears())
                .build();

        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return JobPostingResponse.Detail.from(savedJobPosting);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingResponse.Detail getJobPosting(Long postId) {
        JobPosting jobPosting = findJobPostingById(postId);
        return JobPostingResponse.Detail.from(jobPosting);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostingResponse.Simple> searchJobPostings(
            String keyword, String location, Integer minSalary, Integer maxSalary,
            Integer minExperience, Integer maxExperience, Long jobCategoryId, Pageable pageable) {

        return jobPostingRepository.search(
                        keyword, location, minSalary, maxSalary,
                        minExperience, maxExperience, jobCategoryId, pageable)
                .map(JobPostingResponse.Simple::from);
    }

    @Override
    public JobPostingResponse.Detail updateJobPosting(Long postId, JobPostingRequest.Update request, Long companyUserId) {
        JobPosting jobPosting = findJobPostingById(postId);
        validateOwner(jobPosting, companyUserId);

        updateFields(jobPosting, request);

        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
        return JobPostingResponse.Detail.from(updatedJobPosting);
    }

    @Override
    public void deleteJobPosting(Long postId, Long companyUserId) {
        JobPosting jobPosting = findJobPostingById(postId);
        validateOwner(jobPosting, companyUserId);
        jobPostingRepository.delete(jobPosting);
    }

    private JobPosting findJobPostingById(Long postId) {
        return jobPostingRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + postId));
    }

    private void validateOwner(JobPosting jobPosting, Long companyUserId) {
        if (!Objects.equals(jobPosting.getCompanyUser().getId(), companyUserId)) {
            throw new AccessDeniedException("User does not have permission to modify this job posting");
        }
    }

    private void updateFields(JobPosting jobPosting, JobPostingRequest.Update request) {
        JobCategory jobCategory = null;
        if (request.getJobCategoryId() != null) {
            jobCategory = jobCategoryRepository.findById(request.getJobCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("JobCategory not found with id: " + request.getJobCategoryId()));
        }

        jobPosting.update(
                request.getTitle(),
                request.getContent(),
                request.getSalary(),
                request.getDeadline(),
                request.getWorkLocation(),
                request.getRequiredExperienceYears(),
                jobCategory
        );
    }
}

