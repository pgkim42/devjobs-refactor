package com.example.devjobs.message.controller;

import com.example.devjobs.common.ApiResponse;
import com.example.devjobs.message.dto.MessageResponse;
import com.example.devjobs.message.dto.SendMessageRequest;
import com.example.devjobs.message.service.MessageService;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "쪽지", description = "사용자 간 쪽지 기능 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @Operation(summary = "쪽지 보내기", description = "다른 사용자에게 쪽지를 보냅니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쪽지 전송 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "수신자를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping
    public ApiResponse<MessageResponse> sendMessage(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody SendMessageRequest request) {
        User user = userDetails.getUser();
        MessageResponse response = messageService.sendMessage(user, request);
        return ApiResponse.success(response);
    }
    
    @Operation(summary = "받은 쪽지함 조회", description = "로그인한 사용자가 받은 쪽지 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/received")
    public ApiResponse<Page<MessageResponse>> getReceivedMessages(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userDetails.getUser();
        Page<MessageResponse> messages = messageService.getReceivedMessages(user, pageable);
        return ApiResponse.success(messages);
    }
    
    @Operation(summary = "보낸 쪽지함 조회", description = "로그인한 사용자가 보낸 쪽지 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/sent")
    public ApiResponse<Page<MessageResponse>> getSentMessages(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userDetails.getUser();
        Page<MessageResponse> messages = messageService.getSentMessages(user, pageable);
        return ApiResponse.success(messages);
    }
    
    @Operation(summary = "읽지 않은 쪽지 개수 조회", description = "읽지 않은 쪽지의 개수를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Long count = messageService.getUnreadCount(user);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ApiResponse.success(response);
    }
    
    @Operation(summary = "쪽지 상세 조회", description = "특정 쪽지의 상세 내용을 조회합니다. 자동으로 읽음 처리됩니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "쪽지를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{messageId}")
    public ApiResponse<MessageResponse> getMessage(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "쪽지 ID") @PathVariable Long messageId) {
        User user = userDetails.getUser();
        MessageResponse message = messageService.getMessage(user, messageId);
        return ApiResponse.success(message);
    }
    
    @Operation(summary = "쪽지 읽음 처리", description = "특정 쪽지를 읽음으로 처리합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "읽음 처리 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "쪽지를 찾을 수 없음")
    })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{messageId}/read")
    public ApiResponse<Void> markAsRead(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "쪽지 ID") @PathVariable Long messageId) {
        User user = userDetails.getUser();
        messageService.markAsRead(user, messageId);
        return ApiResponse.success();
    }
}