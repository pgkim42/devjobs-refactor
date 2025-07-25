package com.example.devjobs.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobPostingBatchScheduler {
    
    private final JobLauncher jobLauncher;
    private final Job closeExpiredJobPostingsJob;
    
    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runCloseExpiredJobPostings() {
        try {
            log.info("마감된 채용공고 처리 배치 시작: {}", LocalDateTime.now());
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("runTime", LocalDateTime.now())
                    .toJobParameters();
            
            jobLauncher.run(closeExpiredJobPostingsJob, jobParameters);
            
            log.info("마감된 채용공고 처리 배치 완료");
        } catch (Exception e) {
            log.error("배치 실행 중 오류 발생", e);
        }
    }
    
    // 테스트용: 5분마다 실행 (개발 환경에서만)
    // @Scheduled(fixedDelay = 300000) // 5분
    // public void runTestBatch() {
    //     runCloseExpiredJobPostings();
    // }
}