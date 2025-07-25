package com.example.devjobs.batch.job;

import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.entity.enums.JobPostingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobPostingBatchConfig {
    
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    private static final int CHUNK_SIZE = 100;
    
    @Bean
    public Job closeExpiredJobPostingsJob() {
        return new JobBuilder("closeExpiredJobPostingsJob", jobRepository)
                .start(closeExpiredJobPostingsStep())
                .build();
    }
    
    @Bean
    @JobScope
    public Step closeExpiredJobPostingsStep() {
        return new StepBuilder("closeExpiredJobPostingsStep", jobRepository)
                .<JobPosting, JobPosting>chunk(CHUNK_SIZE, transactionManager)
                .reader(expiredJobPostingReader())
                .processor(jobPostingStatusProcessor())
                .writer(jobPostingWriter())
                .build();
    }
    
    @Bean
    @StepScope
    public JpaPagingItemReader<JobPosting> expiredJobPostingReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("today", LocalDate.now());
        parameters.put("activeStatus", JobPostingStatus.ACTIVE);
        
        return new JpaPagingItemReaderBuilder<JobPosting>()
                .name("expiredJobPostingReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT j FROM JobPosting j " +
                           "WHERE j.deadline < :today " +
                           "AND j.status = :activeStatus")
                .parameterValues(parameters)
                .pageSize(CHUNK_SIZE)
                .build();
    }
    
    @Bean
    @StepScope
    public ItemProcessor<JobPosting, JobPosting> jobPostingStatusProcessor() {
        return jobPosting -> {
            log.info("마감 처리 중: {} (ID: {})", jobPosting.getTitle(), jobPosting.getId());
            jobPosting.close();
            return jobPosting;
        };
    }
    
    @Bean
    @StepScope
    public ItemWriter<JobPosting> jobPostingWriter() {
        return items -> {
            log.info("총 {}개의 채용공고를 마감 처리했습니다.", items.size());
        };
    }
}