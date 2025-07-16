package com.example.devjobs.jobposting.repository;

import com.example.devjobs.jobposting.dto.JobPostingResponse;
import com.example.devjobs.jobposting.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostingRepositoryCustom {
    Page<JobPosting> search(
            String keyword,
            String location,
            Integer minSalary,
            Integer maxSalary,
            Integer minExperience,
            Integer maxExperience,
            Long jobCategoryId,
            Pageable pageable
    );
}
