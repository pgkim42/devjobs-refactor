package com.example.devjobs.bookmark.service;

import com.example.devjobs.bookmark.dto.BookmarkResponse;
import com.example.devjobs.bookmark.entity.Bookmark;
import com.example.devjobs.bookmark.repository.BookmarkRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService {
    
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    
    @Override
    public boolean toggleBookmark(Long userId, Long jobPostingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
                
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + jobPostingId));
        
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserAndJobPosting(user, jobPosting);
        
        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return false; // 북마크 제거됨
        } else {
            Bookmark bookmark = Bookmark.create(user, jobPosting);
            bookmarkRepository.save(bookmark);
            return true; // 북마크 추가됨
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long userId, Long jobPostingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
                
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + jobPostingId));
                
        return bookmarkRepository.existsByUserAndJobPosting(user, jobPosting);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getMyBookmarks(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
                
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserWithJobPosting(user, pageable);
        return bookmarks.map(BookmarkResponse::from);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Long> getBookmarkedJobPostingIds(Long userId) {
        return bookmarkRepository.findBookmarkedJobPostingIdsByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getBookmarkCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
                
        return bookmarkRepository.countByUser(user);
    }
}