# DevJobs - 개발자 구인구직 플랫폼

## 1. 프로젝트 소개

**DevJobs**는 개발자들을 위한 맞춤형 구인구직 플랫폼입니다. 복잡했던 레거시 코드를 클린 아키텍처와 객체지향 원칙에 따라 재설계하여, 유지보수성과 확장성이 뛰어난 백엔드 시스템을 구축하는 것을 목표로 합니다.

본 프로젝트를 통해 사용자는 자신의 전문성을 드러내는 이력서를 손쉽게 작성하고, 기업은 원하는 인재를 효율적으로 찾을 수 있는 환경을 제공하고자 합니다.

## 2. 핵심 기능

### 👤 사용자 관리 (인증/인가)
- **회원가입**: 개인(구직자)과 기업 회원으로 역할을 분리하여 가입
- **로그인**: JWT 토큰 기반의 상태 비저장(Stateless) 인증 시스템
- **권한 제어**: `@PreAuthorize`를 통해 각 API에 대한 역할 기반 접근 제어 (개인, 기업, 관리자)

### 📄 개인 프로필(이력서) 관리
- **프로필 조회**: 자신의 전체 이력서 정보(기본 정보, 경력, 학력, 스킬 등)를 한 번에 조회
- **프로필 수정**: 연락처, 주소, 포트폴리오 등 기본 정보 수정
- **경력 관리**: 경력 사항 추가, 수정, 삭제
- **학력 관리**: 학력 정보 추가, 수정, 삭제
- **보유 기술 관리**: 보유 기술 추가 및 삭제
- **어학 능력 및 자격증 관리**: 어학 점수 및 자격증 정보 추가, 수정, 삭제

### 🏢 채용 공고 관리
- **공고 등록/수정/삭제**: 기업 회원이 채용 공고를 관리
- **공고 전체 조회**: 페이징 처리된 전체 공고 목록 조회
- **공고 상세 조회**: 특정 채용 공고의 상세 내용 조회

### 📝 지원 관리
- **공고 지원**: 개인 회원이 원하는 공고에 지원
- **지원 현황 조회**: 개인 회원이 자신의 지원 목록을 확인
- **지원 취소**: 지원했던 공고를 취소
- **지원자 상태 변경**: 기업 회원이 지원자의 상태(서류 통과, 면접 등)를 변경

### ⚙️ 기타 관리 기능
- **직무 카테고리**: 관리자가 직무 카테고리를 생성, 수정, 삭제
- **표준 API 응답**: 모든 API 응답을 `ApiResponse` 객체로 감싸 일관된 형식 제공
- **전역 예외 처리**: `@RestControllerAdvice`를 통해 예외 상황에 대한 응답 형식 표준화

## 3. 시스템 아키텍처
- **JPA 상속 전략**: 공통 `User` 엔티티를 `IndividualUser`와 `CompanyUser`가 상속받는 `JOINED` 전략을 채택하여 객체지향적이고 확장성 있는 구조 설계
- **RESTful API**: 리소스(명사)��� 행위(동사)를 명확히 분리하여 API 엔드포인트를 설계하고, HTTP 메서드로 기능을 표현
- **Service/Repository 패턴**: 비즈니스 로직과 데이터 접근 로직을 명확히 분리

## 4. 기술 스택
- **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA (Hibernate)
- **Database**: MariaDB (Docker)
- **Build Tool**: Gradle
- **API Test**: Postman

## 5. 주요 API Endpoints
- `POST /api/auth/signup/{type}`: 회원가입 (individual/company)
- `POST /api/auth/signin`: 로그인
- `GET, PUT /api/profiles/individual`: 개인 프로필 조회/수정
- `POST, PUT, DELETE /api/profiles/individual/**`: 경력, 학력 등 상세 프로필 관리
- `POST, GET, PATCH, DELETE /api/jobpostings/**`: 채용 공고 관리
- `POST, GET, DELETE /api/applications/**`: 지원 관리
- `PATCH /api/applications/{id}/status`: 지원 상태 변경
- `POST, GET, PUT, DELETE /api/job-categories/**`: 직무 카테고리 관리 (관리자)
