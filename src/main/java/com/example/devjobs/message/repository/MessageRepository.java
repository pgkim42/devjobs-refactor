package com.example.devjobs.message.repository;

import com.example.devjobs.message.entity.Message;
import com.example.devjobs.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // 받은 쪽지함
    Page<Message> findByReceiverOrderByCreatedAtDesc(User receiver, Pageable pageable);
    
    // 보낸 쪽지함
    Page<Message> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);
    
    // 읽지 않은 쪽지 개수
    Long countByReceiverAndIsReadFalse(User receiver);
    
    // 특정 쪽지 조회 (권한 확인용)
    @Query("SELECT m FROM Message m WHERE m.id = :messageId AND (m.sender = :user OR m.receiver = :user)")
    Optional<Message> findByIdAndUser(@Param("messageId") Long messageId, @Param("user") User user);
    
    // 두 사용자 간의 대화 조회 (특정 채용공고 관련)
    @Query("SELECT m FROM Message m WHERE m.jobPosting.id = :jobPostingId " +
           "AND ((m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1)) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(@Param("user1") User user1, 
                                   @Param("user2") User user2, 
                                   @Param("jobPostingId") Long jobPostingId,
                                   Pageable pageable);
}