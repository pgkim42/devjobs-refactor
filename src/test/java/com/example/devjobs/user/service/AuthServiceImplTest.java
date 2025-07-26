package com.example.devjobs.user.service;

import com.example.devjobs.common.exception.DuplicateResourceException;
import com.example.devjobs.user.dto.auth.IndividualUserSignUpRequest;
import com.example.devjobs.user.dto.auth.SignInRequest;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.provider.JwtProvider;
import com.example.devjobs.user.repository.SkillRepository;
import com.example.devjobs.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SkillRepository skillRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtProvider jwtProvider;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    private IndividualUserSignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    
    @BeforeEach
    void setUp() {
        signUpRequest = IndividualUserSignUpRequest.builder()
                .loginId("testuser")
                .password("password123")
                .name("테스트유저")
                .email("test@example.com")
                .phoneNumber("010-1234-5678")
                .build();
                
        signInRequest = SignInRequest.builder()
                .loginId("testuser")
                .password("password123")
                .build();
    }
    
    @Test
    @DisplayName("개인회원 가입 성공")
    void signUp_Individual_Success() {
        // given
        when(userRepository.existsByLoginId(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // when
        assertDoesNotThrow(() -> authService.signUp(signUpRequest));
        
        // then
        verify(userRepository, times(1)).save(any(IndividualUser.class));
    }
    
    @Test
    @DisplayName("중복된 아이디로 회원가입 시 예외 발생")
    void signUp_DuplicateLoginId_ThrowsException() {
        // given
        when(userRepository.existsByLoginId(anyString())).thenReturn(true);
        
        // when & then
        assertThrows(DuplicateResourceException.class, 
            () -> authService.signUp(signUpRequest));
        
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("중복된 이메일로 회원가입 시 예외 발생")
    void signUp_DuplicateEmail_ThrowsException() {
        // given
        when(userRepository.existsByLoginId(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // when & then
        assertThrows(DuplicateResourceException.class, 
            () -> authService.signUp(signUpRequest));
        
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("로그인 성공")
    void signIn_Success() {
        // given
        User user = IndividualUser.builder()
                .id(1L)
                .loginId("testuser")
                .password("encodedPassword")
                .role("ROLE_INDIVIDUAL")
                .build();
                
        when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtProvider.create(anyString(), anyString(), anyLong())).thenReturn("jwt-token");
        
        // when
        String token = authService.signIn(signInRequest);
        
        // then
        assertNotNull(token);
        assertEquals("jwt-token", token);
        verify(jwtProvider, times(1)).create("testuser", "ROLE_INDIVIDUAL", 1L);
    }
    
    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 시 예외 발생")
    void signIn_UserNotFound_ThrowsException() {
        // given
        when(userRepository.findByLoginId(anyString())).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(BadCredentialsException.class, 
            () -> authService.signIn(signInRequest));
    }
    
    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외 발생")
    void signIn_WrongPassword_ThrowsException() {
        // given
        User user = IndividualUser.builder()
                .loginId("testuser")
                .password("encodedPassword")
                .build();
                
        when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // when & then
        assertThrows(BadCredentialsException.class, 
            () -> authService.signIn(signInRequest));
    }
}