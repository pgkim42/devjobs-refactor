package com.example.devjobs.user.config;

import com.example.devjobs.user.provider.JwtProvider;
import com.example.devjobs.user.service.UserDetailsImpl;
import com.example.devjobs.user.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // /api/auth/me는 인증이 필요하므로 필터를 적용
        if (path.equals("/api/auth/me")) {
            // 인증 필터 적용
        } else if (path.startsWith("/api/auth/") || path.startsWith("/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = parseBearerToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            var claims = jwtProvider.validate(token);
            if (claims == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String loginId = claims.getSubject();
            String role = (String) claims.get("role");
            Long userId = claims.get("userId", Long.class);

            // UserDetailsImpl 객체 생성
            UserDetailsImpl userDetails = new UserDetailsImpl(userId, loginId, null, role);
            
            // UserDetailsImpl을 Principal로 설정
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }
}
