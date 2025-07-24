package com.example.devjobs.message.dto;

import com.example.devjobs.message.entity.Message;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private String senderLoginId;
    private Long receiverId;
    private String receiverName;
    private String receiverLoginId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Long jobPostingId;
    private String jobPostingTitle;
    
    public static MessageResponse from(Message message) {
        return MessageResponse.builder()
                .messageId(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .senderLoginId(message.getSender().getLoginId())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getName())
                .receiverLoginId(message.getReceiver().getLoginId())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .jobPostingId(message.getJobPosting() != null ? message.getJobPosting().getId() : null)
                .jobPostingTitle(message.getJobPosting() != null ? message.getJobPosting().getTitle() : null)
                .build();
    }
}