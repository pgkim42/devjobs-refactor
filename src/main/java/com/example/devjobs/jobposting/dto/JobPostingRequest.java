package com.example.devjobs.jobposting.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class JobPostingRequest {

    @Getter
    @Setter
    public static class Create {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Content is required")
        private String content;

        private Long salary;

        @NotNull(message = "Deadline is required")
        @Future(message = "Deadline must be in the future")
        private LocalDate deadline;

        @NotBlank(message = "Work location is required")
        private String workLocation;

        @NotNull(message = "Required experience years is required")
        private Integer requiredExperienceYears;

        @NotNull(message = "Job category ID is required")
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
