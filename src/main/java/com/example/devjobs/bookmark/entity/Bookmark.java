package com.example.devjobs.bookmark.entity;

import com.example.devjobs.common.BaseEntity;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookmarks", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "job_posting_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bookmark extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;
    
    public static Bookmark create(User user, JobPosting jobPosting) {
        return Bookmark.builder()
                .user(user)
                .jobPosting(jobPosting)
                .build();
    }
}