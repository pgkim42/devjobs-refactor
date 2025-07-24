package com.example.devjobs.bookmark.controller;

import com.example.devjobs.bookmark.dto.BookmarkResponse;
import com.example.devjobs.bookmark.service.BookmarkService;
import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.user.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    
    @PostMapping("/toggle/{jobPostingId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleBookmark(
            @PathVariable Long jobPostingId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        boolean isBookmarked = bookmarkService.toggleBookmark(userDetails.getUserId(), jobPostingId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);
        response.put("message", isBookmarked ? "북마크에 추가되었습니다." : "북마크가 제거되었습니다.");
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/check/{jobPostingId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkBookmark(
            @PathVariable Long jobPostingId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        boolean isBookmarked = bookmarkService.isBookmarked(userDetails.getUserId(), jobPostingId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Page<BookmarkResponse>>> getMyBookmarks(
            @PageableDefault(size = 12) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Page<BookmarkResponse> bookmarks = bookmarkService.getMyBookmarks(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(bookmarks));
    }
    
    @GetMapping("/ids")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<List<Long>>> getBookmarkedJobPostingIds(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<Long> bookmarkedIds = bookmarkService.getBookmarkedJobPostingIds(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(bookmarkedIds));
    }
    
    @GetMapping("/count")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBookmarkCount(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Long count = bookmarkService.getBookmarkCount(userDetails.getUserId());
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}