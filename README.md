# DevJobs - 개발자 구인구직 플랫폼

## 1. 프로젝트 소개

**DevJobs**는 개발자들을 위한 맞춤형 구인구직 플랫폼입니다. 복잡했던 레거시 코드를 클린 아키텍처와 객체지향 원칙에 따라 재설계하여, 유지보수성과 확장성이 뛰어난 백엔드 시스템을 구축하는 것을 목표로 합니다.

본 프로젝트를 통해 사용자는 자신의 전문성을 드러내는 이력서를 손쉽게 작성하고, 기업은 원하는 인재를 효율적으로 찾을 수 있는 환경을 제공하고자 합니다.

## 2. 핵심 기능

### 👤 사용자 및 인증
- **역할 기반 회원가입**: 개인(구직자)과 기업 회원으로 역할을 분리하여 가입합니다.
- **JWT 기반 로그인**: 상태 비저장(Stateless) 인증 시스템을 통해 로그인하고 토큰을 발급받습니다.
- **권한 제어**: `@PreAuthorize`를 통해 각 API에 대한 역할(`INDIVIDUAL`, `COMPANY`, `ADMIN`) 기반 접근을 제어합니다.

### 📄 프로필 관리 (이력서 및 기업 정보)
- **개인 프로필 전체 관리**: 자신의 이력서 정보(기본 정보, 경력, 학력, 스킬 등)를 한 번에 조회하고 관리하는 전체 CRUD API를 제공합니다.
- **기업 프로필 관리**: 기업의 기본 정보(주소, 산업, 웹사이트 등)를 조회하고 수정합니다.
- **파일 업로드**:
    - 개인 회원은 이력서 파일(PDF, DOCX 등)을 업로드할 수 있습니다.
    - 기업 회원은 회사 로고 이미지를 업로드할 수 있습니다.

### 🏢 채용 공고 및 지원
- **채용 공고 관리**: 기업 회원이 채용 공고를 생성, 수정, 삭제합니다.
- **다중 조건 상세 검색**: **QueryDSL**을 사용하여 키워드, 근무지, 연봉, 요구 경력, 직무 카테고리 등 여러 조건을 조합하여 채용 공고를 정교하게 검색합니다.
- **지원 관리**:
    - 개인 회원이 공고에 지원하고 지원 현황을 조회하거나 취소합니다.
    - 기업 회원이 공고별 지원자 목록을 확인하고 지원 상태(서류 통과, 면접 등)를 변경합니다.

### ⚙️ 기타
- **직무 카테고리 관리**: 관리자(ADMIN)가 직무 카테고리를 생성, 수정, 삭제합니다.
- **표준 API 응답**: 모든 응답을 `ApiResponse` 객체로 감싸 일관된 형식의 응답을 제공합니다.
- **전역 예외 처리**: `@RestControllerAdvice`를 통해 예외 상황에 대한 응답을 표준화합니다.

## 3. 시스템 아키텍처
- **JPA 상속 전략**: 공통 `User` 엔티티를 `IndividualUser`와 `CompanyUser`가 상속받는 `JOINED` 전략을 채택하여 객체지향적이고 확장성 있는 구조를 설계했습니다.
- **QueryDSL 기반 동적 쿼리**: 복잡한 검색 조건을 타입-세이프(Type-safe)하고 가독성 높게 처리하기 위해 QueryDSL을 도입하여 데이터 조회 로직을 구현했습니다.
- **RESTful API**: 리소스(명사)와 행위(HTTP 메서드)를 명확히 분리하여 API 엔드포인트를 설계했습니다.
- **역할 분리 설계**: `Service/Repository` 패턴을 준수하고, 파일 처리 로직을 `FileService`로 분리하는 등 역할과 책임을 명확히 나누었습니다.

## 4. 기술 스택
- **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA (Hibernate)
- **Database**: MariaDB (Docker)
- **Data Access**: **QueryDSL**
- **Build Tool**: Gradle
- **API Test**: Postman

## 5. 주요 API Endpoints
- `POST /api/auth/signup/{type}`: 회원가입 (individual/company)
- `POST /api/auth/signin`: 로그인
- `GET, PUT /api/profiles/individual`: 개인 프로필 조회/수정
- `POST, PUT, DELETE /api/profiles/individual/**`: 경력, 학력 등 상세 프로필 관리
- `POST /api/profiles/individual/resume`: 개인 이력서 파일 업로드
- `GET, PUT /api/profiles/company`: 기업 프로필 조회/수정
- `POST /api/profiles/company/logo`: 기업 로고 이미지 업로드
- `GET /api/jobpostings`: **채용 공고 상세 검색** (`keyword`, `location`, `minSalary`, `maxSalary`, `minExperience`, `maxExperience`, `jobCategoryId`)
- `POST, GET, PATCH, DELETE /api/jobpostings/**`: 채용 공고 CRUD
- `POST, GET, DELETE /api/applications/**`: 지원 관리
- `PATCH /api/applications/{id}/status`: 지원 상태 변경
- `POST, GET, PUT, DELETE /api/job-categories/**`: 직무 카테고리 관리 (관리자)