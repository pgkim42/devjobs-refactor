package com.example.devjobs.jobposting.service;

import com.example.devjobs.jobposting.dto.JobPostingDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface JobPostingService {

    int register(JobPostingDTO dto, MultipartFile uploadFile);

    List<JobPostingDTO> getList();

    JobPostingDTO read(Integer jobCode);

    void remove(Integer jobCode);

    List<String> getCompanyNamesFromJobPostings();

    long countAllJobPostings();

    long countActiveJobPostings();

    void modify(Integer jobCode, JobPostingDTO dto, MultipartFile uploadFile);
}
