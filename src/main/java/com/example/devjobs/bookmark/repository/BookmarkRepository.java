package com.example.devjobs.bookmark.repository;

import com.example.devjobs.bookmark.entity.Bookmark;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    Optional<Bookmark> findByUserAndJobPosting(User user, JobPosting jobPosting);
    
    boolean existsByUserAndJobPosting(User user, JobPosting jobPosting);
    
    @Query("SELECT b FROM Bookmark b JOIN FETCH b.jobPosting jp JOIN FETCH jp.companyUser WHERE b.user = :user ORDER BY b.createDate DESC")
    Page<Bookmark> findByUserWithJobPosting(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT b.jobPosting.id FROM Bookmark b WHERE b.user.id = :userId")
    List<Long> findBookmarkedJobPostingIdsByUserId(@Param("userId") Long userId);
    
    void deleteByUserAndJobPosting(User user, JobPosting jobPosting);
    
    Long countByUser(User user);
}