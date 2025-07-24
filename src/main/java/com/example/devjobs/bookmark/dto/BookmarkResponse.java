package com.example.devjobs.bookmark.dto;

import com.example.devjobs.bookmark.entity.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkResponse {
    
    private Long bookmarkId;
    private Long jobPostingId;
    private String title;
    private String companyName;
    private String workLocation;
    private Long salary;
    private Integer requiredExperienceYears;
    private LocalDate deadline;
    private LocalDateTime bookmarkedAt;
    
    public static BookmarkResponse from(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .bookmarkId(bookmark.getId())
                .jobPostingId(bookmark.getJobPosting().getId())
                .title(bookmark.getJobPosting().getTitle())
                .companyName(bookmark.getJobPosting().getCompanyUser().getCompanyName())
                .workLocation(bookmark.getJobPosting().getWorkLocation())
                .salary(bookmark.getJobPosting().getSalary())
                .requiredExperienceYears(bookmark.getJobPosting().getRequiredExperienceYears())
                .deadline(bookmark.getJobPosting().getDeadline())
                .bookmarkedAt(bookmark.getCreateDate())
                .build();
    }
}