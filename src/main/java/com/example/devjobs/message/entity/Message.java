package com.example.devjobs.message.entity;

import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @Column(nullable = false, length = 1000)
    private String content;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id")
    private JobPosting jobPosting;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public void markAsRead() {
        this.isRead = true;
    }
}