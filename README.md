# DevJobs Backend API

개발자 채용 플랫폼 백엔드 API 서버

## 기술 스택

- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- JPA/Hibernate
- MariaDB
- Gradle

## 실행 방법

```bash
# 데이터베이스 설정 (application.yml)
# MariaDB 실행 후 devjobs 데이터베이스 생성 필요

# 프로젝트 실행
./gradlew bootRun

# 빌드
./gradlew build
```

## API 엔드포인트

### 인증 (`/api/auth`)
- `POST /api/auth/signin` - 로그인
- `POST /api/auth/signup/individual` - 개인 회원가입
- `POST /api/auth/signup/company` - 기업 회원가입
- `GET /api/auth/me` - 현재 사용자 정보

### 채용공고 (`/api/jobpostings`)
- `GET /api/jobpostings` - 채용공고 목록 (페이징)
- `GET /api/jobpostings/{id}` - 채용공고 상세
- `POST /api/jobpostings` - 채용공고 등록 (기업)
- `PATCH /api/jobpostings/{id}` - 채용공고 수정 (기업)
- `DELETE /api/jobpostings/{id}` - 채용공고 삭제 (기업)
- `GET /api/jobpostings/my` - 내 회사 채용공고 목록 (기업)

### 지원 (`/api/applications`)
- `POST /api/applications` - 지원하기 (개인)
- `GET /api/applications/my` - 내 지원 목록 (개인)
- `DELETE /api/applications/{id}` - 지원 취소 (개인)
- `GET /api/applications/job/{jobPostingId}` - 채용공고별 지원자 목록 (기업)
- `PATCH /api/applications/{id}/status` - 지원 상태 변경 (기업)

### 프로필 (`/api/profiles`)
- `GET /api/profiles/individual` - 개인 프로필 조회
- `PUT /api/profiles/individual` - 개인 프로필 수정
- `POST /api/profiles/individual/resume` - 이력서 등록
- `GET /api/profiles/company` - 기업 프로필 조회
- `PUT /api/profiles/company` - 기업 프로필 수정

### 카테고리 (`/api/job-categories`)
- `GET /api/job-categories` - 직무 카테고리 목록

### 홈화면 (`/api/home`)
- `GET /api/home/dashboard` - 홈 대시보드 데이터

## 응답 형식

모든 API 응답은 다음 형식으로 래핑됩니다:

```json
{
  "message": "Success",
  "data": { ... },
  "statusCode": 200
}
```

## 인증

JWT 토큰 기반 인증을 사용합니다.

```
Authorization: Bearer {token}
```

## 지원 상태값

- `APPLIED` - 지원 완료
- `PASSED` - 서류 통과
- `INTERVIEW` - 면접
- `ACCEPTED` - 최종 합격
- `REJECTED` - 불합격