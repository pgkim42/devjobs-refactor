package com.example.devjobs.common;

import com.example.devjobs.jobcategory.entity.JobCategory;
import com.example.devjobs.jobcategory.repository.JobCategoryRepository;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.entity.AdminUser;
import com.example.devjobs.user.entity.enums.WorkStatus;
import com.example.devjobs.user.repository.UserRepository;
import com.example.devjobs.application.repository.ApplicationRepository;
import com.example.devjobs.bookmark.repository.BookmarkRepository;
import com.example.devjobs.message.repository.MessageRepository;
import com.example.devjobs.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@Slf4j
@Component
@Profile("local") // local 프로파일에서만 실행
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobPostingRepository jobPostingRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationRepository applicationRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MessageRepository messageRepository;
    private final SkillRepository skillRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final LanguageSkillRepository languageSkillRepository;
    private final CertificationRepository certificationRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 기존 데이터 모두 삭제 (외래키 제약조건 고려한 순서)
        log.warn("=== 기존 데이터를 모두 삭제하고 새로 생성합니다! ===");
        
        // 1. 가장 먼저 연관 테이블들 삭제
        log.info("Message 삭제 중...");
        messageRepository.deleteAll();
        
        log.info("Bookmark 삭제 중...");
        bookmarkRepository.deleteAll();
        
        log.info("Application 삭제 중...");
        applicationRepository.deleteAll();
        
        // 2. 채용공고 삭제
        log.info("JobPosting 삭제 중...");
        jobPostingRepository.deleteAll();
        
        // 3. 사용자 관련 상세 정보 삭제
        log.info("사용자 관련 정보 삭제 중...");
        skillRepository.deleteAll();
        workExperienceRepository.deleteAll();
        educationRepository.deleteAll();
        languageSkillRepository.deleteAll();
        certificationRepository.deleteAll();
        
        // 4. 사용자 삭제
        log.info("User 삭제 중...");
        userRepository.deleteAll();
        
        // 5. 카테고리 삭제
        log.info("JobCategory 삭제 중...");
        jobCategoryRepository.deleteAll();

        log.info("=== 샘플 데이터 초기화 시작 ===");

        // 1. 직무 카테고리 생성
        createJobCategories();

        // 2. 관리자 계정 생성
        createAdminUser();

        // 3. 개인 회원 생성
        createIndividualUsers();

        // 4. 기업 회원 생성
        createCompanyUsers();

        // 5. 채용공고 생성
        createJobPostings();

        log.info("=== 샘플 데이터 초기화 완료 ===");
        
        // 생성된 계정 정보 출력
        log.info("========================================");
        log.info("생성된 샘플 계정 정보:");
        log.info("========================================");
        log.info("【관리자】");
        log.info("- ID: admin");
        log.info("- PW: admin!@!@qq");
        log.info("========================================");
        log.info("【개인 회원】");
        log.info("- ID: user1 ~ user10");
        log.info("- PW: password123");
        log.info("========================================");
        log.info("【기업 회원】");
        log.info("- ID: company1 ~ company10");
        log.info("- PW: password123");
        log.info("========================================");
    }

    private void createJobCategories() {
        // 직무 카테고리가 이미 있으면 스킵
        if (jobCategoryRepository.count() > 0) {
            log.info("직무 카테고리가 이미 존재합니다.");
            return;
        }
        
        log.info("직무 카테고리 생성 중...");
        
        String[] categories = {
            "백엔드 개발", "프론트엔드 개발", "풀스택 개발",
            "모바일 개발", "데이터 엔지니어", "DevOps",
            "AI/ML", "QA", "보안", "게임 개발"
        };

        for (String categoryName : categories) {
            JobCategory category = JobCategory.builder()
                    .categoryName(categoryName)  // name -> categoryName
                    .build();
            jobCategoryRepository.save(category);
        }
        
        log.info("직무 카테고리 {} 개 생성 완료", categories.length);
    }
    
    private void createAdminUser() {
        log.info("관리자 계정 생성 중...");
        
        AdminUser admin = AdminUser.builder()
                .loginId("admin")
                .password(passwordEncoder.encode("admin!@!@qq"))
                .name("시스템관리자")
                .email("admin@devjobs.com")
                .role("ROLE_ADMIN")
                .department("시스템관리팀")
                .adminLevel(1)
                .build();
                
        userRepository.save(admin);
        log.info("관리자 계정 생성 완료: admin / admin!@!@qq");
    }

    private void createIndividualUsers() {
        log.info("개인 회원 생성 중...");

        String[] names = {"김개발", "이코딩", "박프로", "최디자인", "정데이터", "강모바일", "송풀스택", "한보안", "오데브옵스", "임에이아이"};
        String[] cities = {"서울시 강남구", "서울시 서초구", "경기도 성남시", "서울시 마포구", "경기도 판교", "서울시 송파구", "인천시 연수구", "경기도 수원시", "서울시 종로구", "부산시 해운대구"};
        String[] headlines = {
            "5년차 백엔드 개발자입니다",
            "프론트엔드 개발 전문가",
            "풀스택 개발자를 꿈꾸는 주니어",
            "UI/UX에 진심인 디자이너",
            "데이터로 세상을 바꾸는 엔지니어",
            "모바일 앱 개발 3년차",
            "스타트업 경험 많은 풀스택 개발자",
            "보안이 최우선인 개발자",
            "인프라 자동화 전문가",
            "AI/ML 연구개발자"
        };
        WorkStatus[] statuses = {WorkStatus.LOOKING_FOR_JOB, WorkStatus.OPEN_TO_OFFERS, WorkStatus.NOT_LOOKING};

        for (int i = 1; i <= 10; i++) {
            IndividualUser user = IndividualUser.builder()
                    .loginId("user" + i)
                    .password(passwordEncoder.encode("password123"))
                    .email("user" + i + "@example.com")
                    .role("ROLE_INDIVIDUAL")
                    .name(names[i-1])
                    .phoneNumber("010-" + String.format("%04d", 1000 + i) + "-" + String.format("%04d", 5000 + i))
                    .address(cities[i-1])
                    .headline(headlines[i-1])
                    .workStatus(statuses[i % 3])
                    .portfolioUrl(i % 2 == 0 ? "https://portfolio" + i + ".com" : null)
                    .build();
            userRepository.save(user);
        }

        log.info("개인 회원 10명 생성 완료");
        log.info("생성된 개인 회원 계정:");
        for (int i = 1; i <= 10; i++) {
            log.info("- user{} / password123 ({}) - {}", i, names[i-1], headlines[i-1]);
        }
    }

    private void createCompanyUsers() {
        log.info("기업 회원 생성 중...");

        String[] companyNames = {
            "테크코프", "스타트업랩", "게임데브", "클라우드테크", "모바일솔루션",
            "데이터시스템즈", "에이아이컴퍼니", "핀테크뱅크", "이커머스월드", "블록체인랩"
        };
        String[] ceoNames = {
            "최대표", "김창업", "박게임", "이클라우드", "정모바일",
            "강데이터", "송에이아이", "한핀테크", "오커머스", "임블록"
        };
        String[] industries = {
            "IT/소프트웨어", "스타트업", "게임", "클라우드", "모바일",
            "빅데이터", "인공지능", "핀테크", "이커머스", "블록체인"
        };
        String[] addresses = {
            "서울시 강남구 테헤란로 123", "서울시 서초구 강남대로 456", "경기도 성남시 분당구 판교로 789",
            "서울시 마포구 월드컵북로 100", "서울시 강남구 삼성로 200", "경기도 판교 테크노밸리 300",
            "서울시 종로구 종로 400", "서울시 영등포구 여의도동 500", "부산시 해운대구 센텀시티 600",
            "대전시 유성구 테크노파크 700"
        };

        for (int i = 1; i <= 10; i++) {
            CompanyUser company = CompanyUser.builder()
                    .loginId("company" + i)
                    .password(passwordEncoder.encode("password123"))
                    .email("hr@" + companyNames[i-1].toLowerCase().replaceAll(" ", "") + ".com")
                    .name(companyNames[i-1])
                    .role("ROLE_COMPANY")
                    .companyName(companyNames[i-1])
                    .companyCode(String.format("%03d-%02d-%05d", 100+i, 10+i, 10000+i))
                    .ceoName(ceoNames[i-1])
                    .companyAddress(addresses[i-1])
                    .industry(industries[i-1])
                    .companyWebsite("https://" + companyNames[i-1].toLowerCase().replaceAll(" ", "") + ".com")
                    .build();
            userRepository.save(company);
        }

        log.info("기업 회원 10개 생성 완료");
        log.info("생성된 기업 회원 계정:");
        for (int i = 1; i <= 10; i++) {
            log.info("- company{} / password123 ({} - {})", i, companyNames[i-1], industries[i-1]);
        }
    }

    private void createJobPostings() {
        log.info("채용공고 생성 중...");

        // 모든 기업과 카테고리 조회
        var companies = userRepository.findAll().stream()
                .filter(u -> u instanceof CompanyUser)
                .map(u -> (CompanyUser) u)
                .toList();
        
        var categories = jobCategoryRepository.findAll();
        
        if (companies.isEmpty() || categories.isEmpty()) {
            log.warn("기업 또는 카테고리가 없어 채용공고를 생성할 수 없습니다.");
            return;
        }

        String[] jobTitles = {
            "시니어 백엔드 개발자", "주니어 프론트엔드 개발자", "풀스택 개발자 모집",
            "DevOps 엔지니어", "데이터 엔지니어링 전문가", "모바일 앱 개발자",
            "AI/ML 연구원", "보안 전문가", "QA 엔지니어", "게임 클라이언트 개발자",
            "React Native 개발자", "Node.js 백엔드 개발자", "Python Django 개발자",
            "iOS 앱 개발자", "안드로이드 개발자", "빅데이터 분석가",
            "클라우드 아키텍트", "블록체인 개발자", "UI/UX 디자이너 겸 개발자",
            "임베디드 시스템 개발자", "AR/VR 개발자", "게임 서버 프로그래머",
            "프로덕트 매니저 (개발 경험 필수)", "테크 리드", "CTO 모집"
        };

        String[] contents = {
            "우리 회사에서 함께 성장할 개발자를 찾습니다.\n\n주요 업무:\n- 서비스 개발 및 운영\n- 기술 스택 개선\n- 코드 리뷰 및 품질 관리\n\n자격 요건:\n- 관련 경험 보유\n- 협업 능력\n- 성장 마인드",
            "혁신적인 서비스를 만들어갈 인재를 모집합니다.\n\n주요 업무:\n- 신규 기능 개발\n- 성능 최적화\n- 아키텍처 설계\n\n우대 사항:\n- 오픈소스 기여 경험\n- 대규모 트래픽 처리 경험",
            "최고의 팀과 함께할 기회!\n\n우리가 제공하는 것:\n- 자율적인 근무 환경\n- 최신 장비 지원\n- 교육비 지원\n\n함께 하실 분은:\n- 열정적인 개발자\n- 문제 해결을 즐기는 분"
        };

        String[] locations = {
            "서울시 강남구", "서울시 서초구", "경기도 성남시 분당구",
            "서울시 마포구", "서울시 영등포구", "경기도 판교",
            "서울시 종로구", "서울시 송파구", "인천시 연수구", "부산시 해운대구"
        };

        Random random = new Random();
        
        int expiredCount = 0;
        for (int i = 0; i < 25; i++) {
            CompanyUser company = companies.get(random.nextInt(companies.size()));
            JobCategory category = categories.get(random.nextInt(categories.size()));
            
            // 일부 공고는 마감일이 지난 것으로 생성 (테스트용)
            LocalDate deadline;
            if (i < 5) { // 처음 5개는 마감된 공고로 생성
                deadline = LocalDate.now().minusDays(1 + random.nextInt(30));
                expiredCount++;
            } else {
                deadline = LocalDate.now().plusDays(30 + random.nextInt(60));
            }
            
            JobPosting job = JobPosting.builder()
                    .companyUser(company)
                    .jobCategory(category)
                    .title(jobTitles[i % jobTitles.length])
                    .content(contents[i % contents.length])
                    .salary((long) (3000 + random.nextInt(5000)))
                    .deadline(deadline)
                    .workLocation(locations[random.nextInt(locations.length)])
                    .requiredExperienceYears(random.nextInt(8))
                    .build();
            
            jobPostingRepository.save(job);
        }

        log.info("채용공고 25개 생성 완료 (마감된 공고 {}개 포함)", expiredCount);
    }
}