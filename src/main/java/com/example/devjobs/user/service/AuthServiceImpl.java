package com.example.devjobs.user.service;

import com.example.devjobs.common.exception.DuplicateResourceException;
import com.example.devjobs.common.exception.ResourceNotFoundException;
import com.example.devjobs.user.dto.auth.CompanyUserSignUpRequest;
import com.example.devjobs.user.dto.auth.CurrentUserResponse;
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
import org.springframework.security.authentication.BadCredentialsException;
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
    public void signUp(IndividualUserSignUpRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new DuplicateResourceException("이미 사용중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("이미 사용중인 이메일입니다.");
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
                .role("ROLE_INDIVIDUAL")
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .portfolioUrl(request.getPortfolioUrl())
                .skills(skills)
                .build();

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void signUp(CompanyUserSignUpRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new DuplicateResourceException("이미 사용중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("이미 사용중인 이메일입니다.");
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
    }

    @Override
    public String signIn(SignInRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.create(user.getLoginId(), user.getRole(), user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        CurrentUserResponse.CurrentUserResponseBuilder builder = CurrentUserResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole());

        if (user instanceof IndividualUser) {
            builder.userType("individual");
        } else if (user instanceof CompanyUser) {
            CompanyUser companyUser = (CompanyUser) user;
            builder.userType("company")
                    .companyCode(companyUser.getCompanyCode())
                    .companyName(companyUser.getCompanyName())
                    .industry(companyUser.getIndustry())
                    .ceoName(companyUser.getCeoName())
                    .companyAddress(companyUser.getCompanyAddress())
                    .companyWebsite(companyUser.getCompanyWebsite());
        }

        return builder.build();
    }
}
