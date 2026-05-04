# 헷갈림 퀴즈

헷갈릴 만한 상식 문제를 가볍게 풀고, 점수를 랭킹으로 비교하는 웹 퀴즈 게임입니다.

이 프로젝트는 소셜 로그인, 데이터베이스 저장, Docker 기반 개발 환경을 직접 경험하기 위해 기획했습니다. 단순 CRUD 예제보다 사용자가 실제로 플레이할 수 있는 작은 서비스를 만들면서 웹 서비스의 기본 흐름을 이해하는 것을 목표로 합니다.

## 주요 기능

- Google 소셜 로그인
- 퀴즈 카테고리 선택
- 카테고리별 10문제 풀이
- 문제별 정답 여부 확인
- 퀴즈 결과 저장
- 카테고리별 랭킹 조회
- Docker Compose 기반 PostgreSQL 실행

## 기술 스택

| 영역 | 기술 | 사용 목적 |
|---|---|---|
| Frontend | React | 화면 구성과 사용자 인터랙션 구현 |
| Frontend | TypeScript | API 응답과 화면 데이터 타입 관리 |
| Frontend | Vite | 프론트엔드 개발 서버와 빌드 |
| Backend | Spring Boot | REST API 서버 구현 |
| Backend | Spring Security OAuth2 | Google 소셜 로그인 연동 |
| Database | PostgreSQL | 사용자, 문제, 결과, 랭킹 데이터 저장 |
| Infra | Docker Compose | 로컬 개발용 DB 실행 |
| Version Control | Git, GitHub | 버전 관리와 포트폴리오 공개 |

## 프로젝트 구조

현재 프로젝트의 기본 구조입니다. 실제 기능 개발을 진행하면서 세부 구조는 변경될 수 있습니다.

```text
tricky-quiz
├─ frontend
│  └─ React + TypeScript + Vite
├─ backend
│  └─ Spring Boot
├─ docs
│  ├─ 01-project-plan.md
│  ├─ 02-requirements.md
│  ├─ 03-screen-design.md
│  ├─ 04-menu-tree.md
│  ├─ 05-tech-stack.md
│  ├─ 06-database-design.md
│  ├─ 06-database-erd.drawio
│  ├─ 07-api-spec.md
│  └─ 08-roadmap.md
└─ README.md
```

## 실행 방법

로컬 개발 기준 실행 방법입니다.

### 1. DB 실행

```bash
docker compose up -d
```

### 2. 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

Windows PowerShell에서는 다음처럼 실행할 수 있습니다.

```powershell
cd backend
.\gradlew bootRun
```

### 3. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

## 환경 변수

민감한 설정값은 코드에 직접 작성하지 않고 환경 변수로 관리합니다.

| 이름 | 설명 |
|---|---|
| `DB_URL` | PostgreSQL 접속 URL |
| `DB_USERNAME` | 데이터베이스 사용자명 |
| `DB_PASSWORD` | 데이터베이스 비밀번호 |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Client Secret |

환경 변수 예시는 `.env.example` 파일에서 확인할 수 있습니다.

## 핵심 개발 흐름

1. 프론트엔드 정적 화면 구현
2. 백엔드 API 기본 구조 구현
3. PostgreSQL 연결 및 엔티티 구현
4. 초기 문제 데이터 준비
5. Google 로그인 연동
6. 퀴즈 문제 조회 및 풀이 구현
7. 결과 저장 및 랭킹 구현
8. README와 실행 문서 정리

## 제외한 기능

초기 버전에서는 기능 범위를 줄이고 핵심 흐름을 완성하기 위해 다음 기능을 제외합니다.

- 관리자 페이지
- 결제 기능
- 실시간 대전
- 모바일 앱
- 여러 소셜 로그인 동시 지원
- OpenAI API를 활용한 문제 생성
- OpenAI 생성 문제 자동 출제
- 복잡한 통계 분석 기능

## 문서

| 문서 | 설명 |
|---|---|
| [프로젝트 기획서](./docs/01-project-plan.md) | 프로젝트 목적, 주요 사용자, 핵심 기능 정리 |
| [요구사항 정의서](./docs/02-requirements.md) | 기능 요구사항과 비기능 요구사항 정리 |
| [화면 설계서](./docs/03-screen-design.md) | 주요 화면 구성과 화면별 동작 정리 |
| [메뉴 트리](./docs/04-menu-tree.md) | 화면 이동 구조와 메뉴 구성 정리 |
| [기술 스택 선정 문서](./docs/05-tech-stack.md) | 기술 선택 이유와 제외 기술 정리 |
| [DB 설계서](./docs/06-database-design.md) | 테이블 구조, 관계, 저장 흐름 정리 |
| [API 명세서](./docs/07-api-spec.md) | API 목록, 요청, 응답, 인증 기준 정리 |
| [개발 로드맵](./docs/08-roadmap.md) | 단계별 개발 순서와 완료 기준 정리 |
| [개발 기록](./docs/09-dev-log.md) | 작업 내용, 문제 원인, 해결 과정 정리 |

## 외부 설계 링크

- [요구사항 정의서](https://docs.google.com/spreadsheets/d/1KPJ_9YYDqnzsyehJ1-3U9esVwqNnbyYsiAjQ9bHbCH4/edit?usp=sharing)
- [화면 설계](https://www.figma.com/design/JzKnADCUhdFHORv541y0DP/tricky-quiz-%ED%99%94%EB%A9%B4%EC%84%A4%EA%B3%84?node-id=0-1&t=LDMm76jVqBf9Zmsc-1)
- [메뉴 트리](https://www.figma.com/board/LLE8nVUt3aERqy6TJOrVHw/%EB%A9%94%EB%89%B4-%ED%8A%B8%EB%A6%AC?node-id=0-1&t=tFt6oiVYzyM4z7YK-1)
- [사용자 유저 플로우](https://www.figma.com/board/WdNGANEuOwlQHiOKIhcYk6/%EC%A0%9C%EB%AA%A9-%EC%97%86%EC%9D%8C?node-id=0-1&t=TrKYB9GXMxTmnX0B-1)
- [ERD](https://drive.google.com/file/d/1Skwmrr6G4MO5LNRt_-wZ00GbTrfBXWcg/view?usp=sharing)
- [테이블 명세서](https://docs.google.com/spreadsheets/d/1LcSbtoZYwTN2SffkXg9lTdyw1kV6wSQjgHjYmQSTzRI/edit?usp=sharing)

## 포트폴리오 설명 포인트

- 단순 CRUD가 아니라 로그인, 문제 조회, 결과 저장, 랭킹 조회까지 이어지는 서비스 흐름을 구현합니다.
- 프론트엔드와 백엔드를 분리하고 REST API로 통신하는 구조를 사용합니다.
- Docker Compose로 로컬 DB 실행 환경을 구성해 개발 환경 재현성을 높입니다.
- OAuth, DB 설계, API 명세, 화면 설계 문서를 함께 정리해 구현 전 기획 과정을 보여줍니다.
