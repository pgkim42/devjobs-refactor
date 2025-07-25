package com.example.devjobs.jobposting.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class JobPostingRequest {

    @Getter
    @Setter
    public static class Create {
        @NotBlank(message = "제목을 입력해주세요")
        @Size(min = 5, max = 100, message = "제목은 5자 이상 100자 이하로 입력해주세요")
        private String title;

        @NotBlank(message = "내용을 입력해주세요")
        @Size(min = 50, max = 5000, message = "내용은 50자 이상 5000자 이하로 입력해주세요")
        private String content;

        @Min(value = 0, message = "연봉은 0 이상이어야 합니다")
        @Max(value = 999999, message = "연봉은 999,999만원 이하로 입력해주세요")
        private Long salary;

        @NotNull(message = "마감일을 선택해주세요")
        @Future(message = "마감일은 오늘 이후 날짜여야 합니다")
        private LocalDate deadline;

        @NotBlank(message = "근무지를 입력해주세요")
        @Size(min = 2, max = 100, message = "근무지는 2자 이상 100자 이하로 입력해주세요")
        private String workLocation;

        @NotNull(message = "경력 요구사항을 입력해주세요")
        @Min(value = 0, message = "경력은 0년 이상이어야 합니다")
        @Max(value = 50, message = "경력은 50년 이하로 입력해주세요")
        private Integer requiredExperienceYears;

        @NotNull(message = "직무 카테고리를 선택해주세요")
        @Positive(message = "올바른 카테고리를 선택해주세요")
        private Long jobCategoryId;
    }

    @Getter
    @Setter
    public static class Update {
        private String title;
        private String content;
        private Long salary;
        @Future(message = "Deadline must be in the future")
        private LocalDate deadline;
        private String workLocation;
        private Integer requiredExperienceYears;
        private Long jobCategoryId;
    }
}
