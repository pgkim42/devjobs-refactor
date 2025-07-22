package com.example.devjobs.jobcategory.dto;

import com.example.devjobs.jobcategory.entity.JobCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class JobCategoryDto {

    @Getter
    public static class Request {
        @NotBlank(message = "Category name is required")
        private String categoryName;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String categoryName;

        public static Response from(JobCategory entity) {
            return Response.builder()
                    .id(entity.getId())
                    .categoryName(entity.getCategoryName())
                    .build();
        }
    }
}
