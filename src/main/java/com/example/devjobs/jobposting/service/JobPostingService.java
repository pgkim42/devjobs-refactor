package com.example.devjobs.jobposting.service;

import com.example.devjobs.jobposting.dto.JobPostingRequest;
import com.example.devjobs.jobposting.dto.JobPostingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface JobPostingService {

    JobPostingResponse.Detail createJobPosting(JobPostingRequest.Create request, Long companyUserId);

    JobPostingResponse.Detail getJobPosting(Long postId);

    JobPostingResponse.Detail updateJobPosting(Long postId, JobPostingRequest.Update request, Long companyUserId);

    void deleteJobPosting(Long postId, Long companyUserId);

    Page<JobPostingResponse.Simple> searchJobPostings(
            String keyword,
            String location,
            Integer minSalary,
            Integer maxSalary,
            Integer minExperience,
            Integer maxExperience,
            Long jobCategoryId,
            Pageable pageable
    );
    
    List<JobPostingResponse.Simple> getCompanyJobPostings(Long companyUserId);
}
