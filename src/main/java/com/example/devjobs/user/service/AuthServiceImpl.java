package com.example.devjobs.user.service;

import com.example.devjobs.user.dto.auth.CompanyUserSignUpRequest;
import com.example.devjobs.user.dto.auth.IndividualUserSignUpRequest;
import com.example.devjobs.user.dto.auth.SignInRequest;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.entity.Skill;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.provider.JwtProvider;
import com.example.devjobs.user.repository.SkillRepository;
import com.example.devjobs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    @Override
    public ResponseEntity<Void> signUp(IndividualUserSignUpRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        Set<Skill> skills = new HashSet<>();
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            skills = skillRepository.findByNameIn(request.getSkills());
            // 새로운 스킬이 있다면 저장
            for (String skillName : request.getSkills()) {
                if (skills.stream().noneMatch(s -> s.getName().equalsIgnoreCase(skillName))) {
                    Skill newSkill = skillRepository.save(Skill.builder().name(skillName).build());
                    skills.add(newSkill);
                }
            }
        }

        IndividualUser user = IndividualUser.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .role("ROLE_USER")
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .portfolioUrl(request.getPortfolioUrl())
                .skills(skills)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @Override
    public ResponseEntity<Void> signUp(CompanyUserSignUpRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        CompanyUser user = CompanyUser.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .role("ROLE_COMPANY")
                .companyName(request.getCompanyName())
                .companyAddress(request.getCompanyAddress())
                .companyCode(request.getCompanyCode())
                .ceoName(request.getCeoName())
                .companyWebsite(request.getCompanyWebsite())
                .logoUrl(request.getLogoUrl())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> signIn(SignInRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = jwtProvider.create(user.getLoginId(), user.getRole(), user.getId());
        return ResponseEntity.ok(token);
    }
}
