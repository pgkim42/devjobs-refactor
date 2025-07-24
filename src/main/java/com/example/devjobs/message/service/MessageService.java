package com.example.devjobs.message.service;

import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.message.dto.MessageResponse;
import com.example.devjobs.message.dto.SendMessageRequest;
import com.example.devjobs.message.entity.Message;
import com.example.devjobs.message.repository.MessageRepository;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    
    @Transactional
    public MessageResponse sendMessage(User sender, SendMessageRequest request) {
        // 수신자 확인
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 자기 자신에게는 메시지를 보낼 수 없음
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("자기 자신에게는 메시지를 보낼 수 없습니다.");
        }
        
        // 채용공고 확인 (선택사항)
        JobPosting jobPosting = null;
        if (request.getJobPostingId() != null) {
            jobPosting = jobPostingRepository.findById(request.getJobPostingId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채용공고입니다."));
        }
        
        // 메시지 생성
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .jobPosting(jobPosting)
                .isRead(false)
                .build();
        
        Message savedMessage = messageRepository.save(message);
        return MessageResponse.from(savedMessage);
    }
    
    // 받은 쪽지함
    public Page<MessageResponse> getReceivedMessages(User user, Pageable pageable) {
        Page<Message> messages = messageRepository.findByReceiverOrderByCreatedAtDesc(user, pageable);
        return messages.map(MessageResponse::from);
    }
    
    // 보낸 쪽지함
    public Page<MessageResponse> getSentMessages(User user, Pageable pageable) {
        Page<Message> messages = messageRepository.findBySenderOrderByCreatedAtDesc(user, pageable);
        return messages.map(MessageResponse::from);
    }
    
    // 읽지 않은 메시지 개수
    public Long getUnreadCount(User user) {
        return messageRepository.countByReceiverAndIsReadFalse(user);
    }
    
    // 메시지 읽음 처리
    @Transactional
    public void markAsRead(User user, Long messageId) {
        Message message = messageRepository.findByIdAndUser(messageId, user)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
        
        // 수신자만 읽음 처리 가능
        if (!message.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        
        message.markAsRead();
    }
    
    // 메시지 상세 조회
    public MessageResponse getMessage(User user, Long messageId) {
        Message message = messageRepository.findByIdAndUser(messageId, user)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
        
        // 수신자가 조회하는 경우 읽음 처리
        if (message.getReceiver().getId().equals(user.getId()) && !message.getIsRead()) {
            message.markAsRead();
            messageRepository.save(message);
        }
        
        return MessageResponse.from(message);
    }
}