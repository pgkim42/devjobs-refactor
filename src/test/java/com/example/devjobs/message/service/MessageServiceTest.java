package com.example.devjobs.message.service;

import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.message.dto.MessageResponse;
import com.example.devjobs.message.dto.SendMessageRequest;
import com.example.devjobs.message.entity.Message;
import com.example.devjobs.message.repository.MessageRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JobPostingRepository jobPostingRepository;
    
    @InjectMocks
    private MessageService messageService;
    
    private User sender;
    private User receiver;
    private CompanyUser companyUser;
    private JobPosting jobPosting;
    private Message message;
    private SendMessageRequest sendRequest;
    
    @BeforeEach
    void setUp() {
        sender = IndividualUser.builder()
                .id(1L)
                .loginId("sender")
                .name("보내는사람")
                .email("sender@test.com")
                .build();
                
        receiver = IndividualUser.builder()
                .id(2L)
                .loginId("receiver")
                .name("받는사람")
                .email("receiver@test.com")
                .build();
                
        companyUser = CompanyUser.builder()
                .id(3L)
                .companyName("테스트회사")
                .build();
                
        jobPosting = JobPosting.builder()
                .id(1L)
                .title("백엔드 개발자 채용")
                .companyUser(companyUser)
                .deadline(LocalDate.now().plusDays(30))
                .build();
                
        message = Message.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .content("안녕하세요, 문의드립니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
                
        sendRequest = new SendMessageRequest();
        sendRequest.setReceiverId(2L);
        sendRequest.setContent("안녕하세요, 문의드립니다.");
    }
    
    @Nested
    @DisplayName("메시지 전송")
    class SendMessageTest {
        
        @Test
        @DisplayName("메시지 전송 성공 - 채용공고 없음")
        void sendMessage_WithoutJobPosting_Success() {
            // given
            when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
            when(messageRepository.save(any(Message.class))).thenReturn(message);
            
            // when
            MessageResponse response = messageService.sendMessage(sender, sendRequest);
            
            // then
            assertNotNull(response);
            assertEquals("보내는사람", response.getSenderName());
            assertEquals("받는사람", response.getReceiverName());
            assertEquals("안녕하세요, 문의드립니다.", response.getContent());
            assertFalse(response.getIsRead());
            verify(messageRepository, times(1)).save(any(Message.class));
        }
        
        @Test
        @DisplayName("메시지 전송 성공 - 채용공고 포함")
        void sendMessage_WithJobPosting_Success() {
            // given
            sendRequest.setJobPostingId(1L);
            message = Message.builder()
                    .id(1L)
                    .sender(sender)
                    .receiver(receiver)
                    .content("안녕하세요, 문의드립니다.")
                    .jobPosting(jobPosting)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
                    
            when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(jobPosting));
            when(messageRepository.save(any(Message.class))).thenReturn(message);
            
            // when
            MessageResponse response = messageService.sendMessage(sender, sendRequest);
            
            // then
            assertNotNull(response);
            assertEquals(1L, response.getJobPostingId());
            assertEquals("백엔드 개발자 채용", response.getJobPostingTitle());
            verify(jobPostingRepository, times(1)).findById(1L);
        }
        
        @Test
        @DisplayName("메시지 전송 실패 - 수신자 없음")
        void sendMessage_ReceiverNotFound() {
            // given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            sendRequest.setReceiverId(999L);
            
            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> messageService.sendMessage(sender, sendRequest),
                "존재하지 않는 사용자입니다.");
            verify(messageRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("메시지 전송 실패 - 자기 자신에게 전송")
        void sendMessage_ToSelf() {
            // given
            sendRequest.setReceiverId(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            
            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> messageService.sendMessage(sender, sendRequest),
                "자기 자신에게는 메시지를 보낼 수 없습니다.");
            verify(messageRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("메시지 전송 실패 - 채용공고 없음")
        void sendMessage_JobPostingNotFound() {
            // given
            sendRequest.setJobPostingId(999L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> messageService.sendMessage(sender, sendRequest),
                "존재하지 않는 채용공고입니다.");
            verify(messageRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("메시지 조회")
    class GetMessagesTest {
        
        @Test
        @DisplayName("받은 메시지 목록 조회 성공")
        void getReceivedMessages_Success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Message> messagePage = new PageImpl<>(Arrays.asList(message), pageable, 1);
            when(messageRepository.findByReceiverOrderByCreatedAtDesc(receiver, pageable)).thenReturn(messagePage);
            
            // when
            Page<MessageResponse> response = messageService.getReceivedMessages(receiver, pageable);
            
            // then
            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            assertEquals("보내는사람", response.getContent().get(0).getSenderName());
        }
        
        @Test
        @DisplayName("보낸 메시지 목록 조회 성공")
        void getSentMessages_Success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Message> messagePage = new PageImpl<>(Arrays.asList(message), pageable, 1);
            when(messageRepository.findBySenderOrderByCreatedAtDesc(sender, pageable)).thenReturn(messagePage);
            
            // when
            Page<MessageResponse> response = messageService.getSentMessages(sender, pageable);
            
            // then
            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            assertEquals("받는사람", response.getContent().get(0).getReceiverName());
        }
        
        @Test
        @DisplayName("읽지 않은 메시지 개수 조회")
        void getUnreadCount_Success() {
            // given
            when(messageRepository.countByReceiverAndIsReadFalse(receiver)).thenReturn(5L);
            
            // when
            Long count = messageService.getUnreadCount(receiver);
            
            // then
            assertEquals(5L, count);
        }
    }
    
    @Nested
    @DisplayName("메시지 읽음 처리")
    class MarkAsReadTest {
        
        @Test
        @DisplayName("읽음 처리 성공")
        void markAsRead_Success() {
            // given
            when(messageRepository.findByIdAndUser(1L, receiver)).thenReturn(Optional.of(message));
            
            // when
            messageService.markAsRead(receiver, 1L);
            
            // then
            assertTrue(message.getIsRead());
            verify(messageRepository, times(1)).findByIdAndUser(1L, receiver);
        }
        
        @Test
        @DisplayName("읽음 처리 실패 - 메시지 없음")
        void markAsRead_MessageNotFound() {
            // given
            when(messageRepository.findByIdAndUser(999L, receiver)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> messageService.markAsRead(receiver, 999L),
                "메시지를 찾을 수 없습니다.");
        }
        
        @Test
        @DisplayName("읽음 처리 실패 - 권한 없음")
        void markAsRead_NoPermission() {
            // given
            when(messageRepository.findByIdAndUser(1L, sender)).thenReturn(Optional.of(message));
            
            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> messageService.markAsRead(sender, 1L),
                "권한이 없습니다.");
        }
    }
    
    @Nested
    @DisplayName("메시지 상세 조회")
    class GetMessageTest {
        
        @Test
        @DisplayName("메시지 조회 성공 - 수신자가 조회 (읽음 처리)")
        void getMessage_AsReceiver_Success() {
            // given
            when(messageRepository.findByIdAndUser(1L, receiver)).thenReturn(Optional.of(message));
            when(messageRepository.save(any(Message.class))).thenReturn(message);
            
            // when
            MessageResponse response = messageService.getMessage(receiver, 1L);
            
            // then
            assertNotNull(response);
            assertTrue(message.getIsRead());
            verify(messageRepository, times(1)).save(message);
        }
        
        @Test
        @DisplayName("메시지 조회 성공 - 발신자가 조회 (읽음 처리 안함)")
        void getMessage_AsSender_Success() {
            // given
            when(messageRepository.findByIdAndUser(1L, sender)).thenReturn(Optional.of(message));
            
            // when
            MessageResponse response = messageService.getMessage(sender, 1L);
            
            // then
            assertNotNull(response);
            assertFalse(message.getIsRead());
            verify(messageRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("메시지 조회 성공 - 이미 읽은 메시지")
        void getMessage_AlreadyRead_Success() {
            // given
            message.markAsRead();
            when(messageRepository.findByIdAndUser(1L, receiver)).thenReturn(Optional.of(message));
            
            // when
            MessageResponse response = messageService.getMessage(receiver, 1L);
            
            // then
            assertNotNull(response);
            assertTrue(message.getIsRead());
            verify(messageRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("메시지 조회 실패 - 메시지 없음")
        void getMessage_NotFound() {
            // given
            when(messageRepository.findByIdAndUser(999L, receiver)).thenReturn(Optional.empty());
            
            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> messageService.getMessage(receiver, 999L),
                "메시지를 찾을 수 없습니다.");
        }
    }
}