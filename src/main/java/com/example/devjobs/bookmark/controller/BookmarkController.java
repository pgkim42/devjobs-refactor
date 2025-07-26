package com.example.devjobs.bookmark.controller;

import com.example.devjobs.bookmark.dto.BookmarkResponse;
import com.example.devjobs.bookmark.service.BookmarkService;
import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.user.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "북마크", description = "채용공고 북마크 관리 API")
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    
    @Operation(summary = "북마크 토글", description = "채용공고를 북마크에 추가하거나 제거합니다. 개인회원만 사용 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토글 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "채용공고를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/toggle/{jobPostingId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleBookmark(
            @Parameter(description = "채용공고 ID") @PathVariable Long jobPostingId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        boolean isBookmarked = bookmarkService.toggleBookmark(userDetails.getUserId(), jobPostingId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);
        response.put("message", isBookmarked ? "북마크에 추가되었습니다." : "북마크가 제거되었습니다.");
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "북마크 여부 확인", description = "특정 채용공고의 북마크 여부를 확인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/check/{jobPostingId}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkBookmark(
            @Parameter(description = "채용공고 ID") @PathVariable Long jobPostingId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        boolean isBookmarked = bookmarkService.isBookmarked(userDetails.getUserId(), jobPostingId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "내 북마크 목록 조회", description = "로그인한 사용자의 북마크 목록을 페이지 단위로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/my")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Page<BookmarkResponse>>> getMyBookmarks(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Page<BookmarkResponse> bookmarks = bookmarkService.getMyBookmarks(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(bookmarks));
    }
    
    @Operation(summary = "북마크한 채용공고 ID 목록", description = "북마크한 모든 채용공고의 ID 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/ids")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<List<Long>>> getBookmarkedJobPostingIds(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<Long> bookmarkedIds = bookmarkService.getBookmarkedJobPostingIds(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(bookmarkedIds));
    }
    
    @Operation(summary = "북마크 개수 조회", description = "사용자의 총 북마크 개수를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/count")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBookmarkCount(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Long count = bookmarkService.getBookmarkCount(userDetails.getUserId());
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}