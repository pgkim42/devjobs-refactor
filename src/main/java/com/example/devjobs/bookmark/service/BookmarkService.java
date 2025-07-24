package com.example.devjobs.bookmark.service;

import com.example.devjobs.bookmark.dto.BookmarkResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkService {
    
    boolean toggleBookmark(Long userId, Long jobPostingId);
    
    boolean isBookmarked(Long userId, Long jobPostingId);
    
    Page<BookmarkResponse> getMyBookmarks(Long userId, Pageable pageable);
    
    List<Long> getBookmarkedJobPostingIds(Long userId);
    
    Long getBookmarkCount(Long userId);
}