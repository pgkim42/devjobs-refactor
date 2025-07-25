package com.example.devjobs.message.controller;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.message.dto.MessageResponse;
import com.example.devjobs.message.dto.SendMessageRequest;
import com.example.devjobs.message.service.MessageService;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    // 메시지 보내기
    @PostMapping
    public ApiResponse<MessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody SendMessageRequest request) {
        User user = userDetails.getUser();
        MessageResponse response = messageService.sendMessage(user, request);
        return ApiResponse.success(response);
    }
    
    // 받은 쪽지함
    @GetMapping("/received")
    public ApiResponse<Page<MessageResponse>> getReceivedMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userDetails.getUser();
        Page<MessageResponse> messages = messageService.getReceivedMessages(user, pageable);
        return ApiResponse.success(messages);
    }
    
    // 보낸 쪽지함
    @GetMapping("/sent")
    public ApiResponse<Page<MessageResponse>> getSentMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userDetails.getUser();
        Page<MessageResponse> messages = messageService.getSentMessages(user, pageable);
        return ApiResponse.success(messages);
    }
    
    // 읽지 않은 메시지 개수
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Long count = messageService.getUnreadCount(user);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ApiResponse.success(response);
    }
    
    // 메시지 상세 조회 (자동 읽음 처리)
    @GetMapping("/{messageId}")
    public ApiResponse<MessageResponse> getMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long messageId) {
        User user = userDetails.getUser();
        MessageResponse message = messageService.getMessage(user, messageId);
        return ApiResponse.success(message);
    }
    
    // 읽음 처리
    @PatchMapping("/{messageId}/read")
    public ApiResponse<Void> markAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long messageId) {
        User user = userDetails.getUser();
        messageService.markAsRead(user, messageId);
        return ApiResponse.success();
    }
}