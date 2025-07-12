package com.example.devjobs.jobcategory.service;

import com.example.devjobs.jobcategory.dto.JobCategoryDto;
import com.example.devjobs.jobcategory.entity.JobCategory;
import com.example.devjobs.jobcategory.repository.JobCategoryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JobCategoryServiceImpl implements JobCategoryService {

    private final JobCategoryRepository jobCategoryRepository;

    @PostConstruct
    public void initJobCategories() {
        if (jobCategoryRepository.count() == 0) {
            List<JobCategory> categories = Arrays.asList(
                    JobCategory.builder().categoryName("백엔드 개발자").build(),
                    JobCategory.builder().categoryName("프론트엔드 개발자").build(),
                    JobCategory.builder().categoryName("풀스택 개발자").build(),
                    JobCategory.builder().categoryName("데이터 과학자").build(),
                    JobCategory.builder().categoryName("게임 개발자").build(),
                    JobCategory.builder().categoryName("모바일앱 개발자").build(),
                    JobCategory.builder().categoryName("데브옵스 엔지니어").build(),
                    JobCategory.builder().categoryName("임베디드 엔지니어").build(),
                    JobCategory.builder().categoryName("클라우드 엔지니어").build(),
                    JobCategory.builder().categoryName("시큐리티 엔지니어").build()
            );
            jobCategoryRepository.saveAll(categories);
            log.info("Initialized {} job categories.", categories.size());
        }
    }

    @Override
    public JobCategoryDto.Response createCategory(JobCategoryDto.Request request) {
        JobCategory jobCategory = JobCategory.builder()
                .categoryName(request.getCategoryName())
                .build();
        JobCategory savedCategory = jobCategoryRepository.save(jobCategory);
        return JobCategoryDto.Response.from(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobCategoryDto.Response> getAllCategories() {
        return jobCategoryRepository.findAll().stream()
                .map(JobCategoryDto.Response::from)
                .collect(Collectors.toList());
    }

    @Override
    public JobCategoryDto.Response updateCategory(Long categoryId, JobCategoryDto.Request request) {
        JobCategory jobCategory = jobCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
        jobCategory.updateCategoryName(request.getCategoryName());
        JobCategory updatedCategory = jobCategoryRepository.save(jobCategory);
        return JobCategoryDto.Response.from(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (!jobCategoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found with id: " + categoryId);
        }
        jobCategoryRepository.deleteById(categoryId);
    }
}