package com.example.devjobs.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {
    
    @NotNull(message = "수신자 ID는 필수입니다.")
    private Long receiverId;
    
    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(min = 1, max = 1000, message = "메시지는 1자 이상 1000자 이하여야 합니다.")
    private String content;
    
    private Long jobPostingId;  // 채용공고 관련 문의인 경우
}