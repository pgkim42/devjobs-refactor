# DevJobs Backend

Spring Boot 기반 채용 플랫폼 REST API

## 기술 스택

- Java 17
- Spring Boot 3.4.0
- Spring Security + JWT
- Spring Data JPA
- Spring Batch
- QueryDSL
- MariaDB
- SpringDoc OpenAPI 2.7.0

## 실행

```bash
# 데이터베이스 실행 (docker-compose)
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun
```

## API 문서

http://localhost:8080/swagger-ui.html

## 주요 기능

### 인증/인가
- JWT 기반 인증
- 역할별 권한 관리 (INDIVIDUAL, COMPANY)

### 채용공고
- CRUD 및 검색/필터링
- 자동 마감 처리 (Spring Batch)
- 상태 관리 (ACTIVE, CLOSED, CANCELLED, FILLED)

### 지원 관리
- 지원/취소
- 상태 변경 (APPLIED → PASSED → INTERVIEW → ACCEPTED/REJECTED)

### 프로필
- 개인: 경력, 학력, 스킬, 어학, 자격증
- 기업: 회사 정보, 로고

### 부가 기능
- 북마크
- 쪽지
- 파일 업로드 (이력서, 회사 로고)

## 환경 설정

`src/main/resources/application.yml` 참고