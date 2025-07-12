package com.example.devjobs.jobposting.dto;

import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.user.entity.CompanyUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobPostingResponse {

    @Getter
    @Builder
    public static class Detail {
        private Long id;
        private String title;
        private String content;
        private Long salary;
        private LocalDate deadline;
        private String workLocation;
        private CompanyInfo companyInfo;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;

        public static Detail from(JobPosting jobPosting) {
            return Detail.builder()
                    .id(jobPosting.getId())
                    .title(jobPosting.getTitle())
                    .content(jobPosting.getContent())
                    .salary(jobPosting.getSalary())
                    .deadline(jobPosting.getDeadline())
                    .workLocation(jobPosting.getWorkLocation())
                    .companyInfo(CompanyInfo.from(jobPosting.getCompanyUser()))
                    .createDate(jobPosting.getCreateDate())
                    .updateDate(jobPosting.getUpdateDate())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Simple {
        private Long id;
        private String title;
        private LocalDate deadline;
        private String companyName;

        public static Simple from(JobPosting jobPosting) {
            return Simple.builder()
                    .id(jobPosting.getId())
                    .title(jobPosting.getTitle())
                    .deadline(jobPosting.getDeadline())
                    .companyName(jobPosting.getCompanyUser().getCompanyName())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CompanyInfo {
        private Long companyId;
        private String companyName;
        private String industry;
        private String location;

        public static CompanyInfo from(CompanyUser companyUser) {
            return new CompanyInfo(
                    companyUser.getId(),
                    companyUser.getCompanyName(),
                    companyUser.getIndustry(),
                    companyUser.getCompanyAddress()
            );
        }
    }
}
