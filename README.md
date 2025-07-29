# DevJobs - 개발자 채용 플랫폼

Spring Boot + React 기반 채용 플랫폼

## 기술 스택

### Backend

- Java 17
- Spring Boot 3.4.0
- Spring Security + JWT
- Spring Data JPA + QueryDSL
- Spring Batch
- MariaDB
- Docker

### Frontend

- React 18
- Vite
- Styled-components
- Axios

## 주요 기능

### 회원 관리

- 개인/기업 회원가입 및 로그인
- JWT 기반 인증
- 역할별 권한 관리 (INDIVIDUAL, COMPANY)

### 채용공고

- 채용공고 CRUD
- 키워드 검색 (제목, 내용, 회사명)
- 실시간 검색 자동완성
- 카테고리별 필터링
- 자동 마감 처리 (Spring Batch)

### 지원 관리

- 이력서 업로드 및 지원
- 지원 상태 관리 (지원완료 → 서류통과 → 면접 → 최종합격/불합격)
- 지원 취소

### 프로필

- 개인: 이력서 업로드 (PDF/DOC/DOCX)
- 기업: 회사 정보 및 로고 관리

### 부가 기능

- 채용공고 북마크
- 사용자 간 메시징
- 통계 대시보드

## 실행 방법

### Backend

```bash
# 데이터베이스 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun
```

### Frontend

```bash
cd devjobs-front-refactor
npm install
npm run dev
```

## API 문서

- Swagger UI: http://localhost:8080/swagger-ui.html

## 프로젝트 구조

```
/devJobs                 # Spring Boot 백엔드
/devjobs-front-refactor  # React 프론트엔드
```

## 배포

- **Backend**: Naver Cloud Platform (Spring Boot + MariaDB)
- **Frontend**: Netlify
