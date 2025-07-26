package com.example.devjobs.bookmark.service;

import com.example.devjobs.bookmark.dto.BookmarkResponse;
import com.example.devjobs.bookmark.entity.Bookmark;
import com.example.devjobs.bookmark.repository.BookmarkRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceImplTest {

    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JobPostingRepository jobPostingRepository;
    
    @InjectMocks
    private BookmarkServiceImpl bookmarkService;
    
    private User user;
    private CompanyUser companyUser;
    private JobPosting jobPosting;
    private Bookmark bookmark;
    
    @BeforeEach
    void setUp() {
        user = IndividualUser.builder()
                .id(1L)
                .loginId("testuser")
                .name("테스트유저")
                .email("test@test.com")
                .build();
                
        companyUser = CompanyUser.builder()
                .id(2L)
                .companyName("테스트회사")
                .build();
                
        jobPosting = JobPosting.builder()
                .id(1L)
                .title("백엔드 개발자 채용")
                .companyUser(companyUser)
                .workLocation("서울시 강남구")
                .salary(50000000L)
                .requiredExperienceYears(3)
                .deadline(LocalDate.now().plusDays(30))
                .build();
                
        bookmark = Bookmark.builder()
                .id(1L)
                .user(user)
                .jobPosting(jobPosting)
                .build();
    }
    
    @Nested
    @DisplayName("북마크 토글")
    class ToggleBookmarkTest {
        
        @Test
        @DisplayName("북마크 추가 성공")
        void toggleBookmark_AddBookmark_Success() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(bookmarkRepository.findByUserAndJobPosting(user, jobPosting)).thenReturn(Optional.empty());
            when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);
            
            // when
            boolean result = bookmarkService.toggleBookmark(1L, 1L);
            
            // then
            assertTrue(result);
            verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
            verify(bookmarkRepository, never()).delete(any());
        }
        
        @Test
        @DisplayName("북마크 제거 성공")
        void toggleBookmark_RemoveBookmark_Success() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(bookmarkRepository.findByUserAndJobPosting(user, jobPosting)).thenReturn(Optional.of(bookmark));
            
            // when
            boolean result = bookmarkService.toggleBookmark(1L, 1L);
            
            // then
            assertFalse(result);
            verify(bookmarkRepository, times(1)).delete(bookmark);
            verify(bookmarkRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("북마크 토글 실패 - 사용자 없음")
        void toggleBookmark_UserNotFound() {
            // given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> bookmarkService.toggleBookmark(999L, 1L));
            verify(bookmarkRepository, never()).save(any());
            verify(bookmarkRepository, never()).delete(any());
        }
        
        @Test
        @DisplayName("북마크 토글 실패 - 채용공고 없음")
        void toggleBookmark_JobPostingNotFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> bookmarkService.toggleBookmark(1L, 999L));
            verify(bookmarkRepository, never()).save(any());
            verify(bookmarkRepository, never()).delete(any());
        }
    }
    
    @Nested
    @DisplayName("북마크 확인")
    class IsBookmarkedTest {
        
        @Test
        @DisplayName("북마크 되어있음")
        void isBookmarked_True() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(bookmarkRepository.existsByUserAndJobPosting(user, jobPosting)).thenReturn(true);
            
            // when
            boolean result = bookmarkService.isBookmarked(1L, 1L);
            
            // then
            assertTrue(result);
        }
        
        @Test
        @DisplayName("북마크 되어있지 않음")
        void isBookmarked_False() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(bookmarkRepository.existsByUserAndJobPosting(user, jobPosting)).thenReturn(false);
            
            // when
            boolean result = bookmarkService.isBookmarked(1L, 1L);
            
            // then
            assertFalse(result);
        }
        
        @Test
        @DisplayName("북마크 확인 실패 - 사용자 없음")
        void isBookmarked_UserNotFound() {
            // given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> bookmarkService.isBookmarked(999L, 1L));
        }
    }
    
    @Nested
    @DisplayName("북마크 목록 조회")
    class GetBookmarksTest {
        
        @Test
        @DisplayName("내 북마크 목록 조회 성공")
        void getMyBookmarks_Success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Bookmark> bookmarkPage = new PageImpl<>(Arrays.asList(bookmark), pageable, 1);
            
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(bookmarkRepository.findByUserWithJobPosting(user, pageable)).thenReturn(bookmarkPage);
            
            // when
            Page<BookmarkResponse> response = bookmarkService.getMyBookmarks(1L, pageable);
            
            // then
            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            assertEquals("백엔드 개발자 채용", response.getContent().get(0).getTitle());
            assertEquals("테스트회사", response.getContent().get(0).getCompanyName());
        }
        
        @Test
        @DisplayName("북마크된 채용공고 ID 목록 조회 성공")
        void getBookmarkedJobPostingIds_Success() {
            // given
            List<Long> jobPostingIds = Arrays.asList(1L, 2L, 3L);
            when(bookmarkRepository.findBookmarkedJobPostingIdsByUserId(1L)).thenReturn(jobPostingIds);
            
            // when
            List<Long> result = bookmarkService.getBookmarkedJobPostingIds(1L);
            
            // then
            assertEquals(3, result.size());
            assertTrue(result.contains(1L));
            assertTrue(result.contains(2L));
            assertTrue(result.contains(3L));
        }
    }
    
    @Nested
    @DisplayName("북마크 개수 조회")
    class BookmarkCountTest {
        
        @Test
        @DisplayName("북마크 개수 조회 성공")
        void getBookmarkCount_Success() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(bookmarkRepository.countByUser(user)).thenReturn(5L);
            
            // when
            Long count = bookmarkService.getBookmarkCount(1L);
            
            // then
            assertEquals(5L, count);
        }
        
        @Test
        @DisplayName("북마크 개수 조회 실패 - 사용자 없음")
        void getBookmarkCount_UserNotFound() {
            // given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(EntityNotFoundException.class,
                () -> bookmarkService.getBookmarkCount(999L));
        }
    }
}