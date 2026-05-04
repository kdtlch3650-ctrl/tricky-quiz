# 개발 기록

## 2026-05-04

### 퀴즈 결과 제출 API 구현

작업 내용:

- `POST /api/quiz/results` 결과 제출 API를 추가했습니다.
- 제출 요청용 `QuizSubmitRequest`, `QuizSubmitAnswerRequest` DTO를 추가했습니다.
- 제출 응답용 `QuizSubmitResponse`, `QuizSubmitAnswerResponse` DTO를 추가했습니다.
- `QuizService`에서 사용자 식별, 정답 검증, 결과 저장, 답안 저장을 처리하도록 확장했습니다.
- `ResponseStatusException`을 전역 예외 처리기에 추가해 404 응답이 JSON으로 내려가도록 맞췄습니다.
- 컨트롤러 테스트와 서비스 테스트를 추가했습니다.

확인한 내용:

- 답안 개수가 10개가 아니면 400으로 처리합니다.
- 존재하지 않는 문제 또는 선택지는 404로 처리합니다.
- 제출 결과에는 문제별 정답 선택지, 사용자가 고른 선택지, 해설이 포함됩니다.
- `QuizServiceTest`, `QuizControllerTest`가 통과했습니다.

현재 상태:

- 퀴즈 시작 API 다음 흐름인 결과 저장 API가 준비되었습니다.
- 로컬 PostgreSQL이 실행되지 않아 전체 `mvn test`는 아직 끝까지 돌리지 못했습니다.
- 선택한 테스트만 돌리면 코드와 컨트롤러는 정상 동작합니다.

### 퀴즈 시작 API 구현

작업 내용:

- 퀴즈 시작용 `GET /api/quiz/questions?category=GENERAL` API를 추가했습니다.
- `QuizController`를 추가해 요청을 받아 `QuizService`로 위임하도록 구성했습니다.
- 문제 응답용 `QuizQuestionsResponse`, `QuizQuestionResponse`, `QuizChoiceResponse` DTO를 추가했습니다.
- `QuizService`에서 카테고리 검증, 활성 문제 10개 선택, 선택지 조회를 처리하도록 구현했습니다.
- 비로그인 요청이 API 환경에서 `401`으로 떨어지도록 `SecurityConfig`를 보완했습니다.
- 컨트롤러 테스트와 서비스 테스트를 추가했습니다.

확인한 내용:

- 선택지 응답에는 정답 여부를 포함하지 않도록 했습니다.
- 카테고리 값이 잘못되면 `IllegalArgumentException`을 던지도록 했습니다.
- 출제 가능한 문제가 10개 미만이면 예외를 반환하도록 했습니다.
- `mvn test`가 성공했습니다.

현재 상태:

- 퀴즈 시작 화면에 필요한 문제와 선택지 응답 구조가 준비되었습니다.
- 다음 단계에서는 제출 API 또는 결과 저장 API로 확장할 수 있습니다.

### DB 연결 및 엔티티 구현

작업 내용:

- Flyway 의존성을 추가했습니다.
- `V1__create_quiz_tables.sql` 마이그레이션 파일을 추가했습니다.
- `users`, `quiz_questions`, `quiz_choices`, `quiz_results`, `quiz_answers` 테이블 생성 SQL을 작성했습니다.
- 사용자, 문제, 선택지, 결과, 답안 엔티티를 추가했습니다.
- 카테고리와 로그인 제공자를 enum으로 정의했습니다.
- 각 엔티티에 대응하는 Repository를 추가했습니다.
- Repository 저장/조회 테스트를 추가했습니다.

확인한 내용:

- `spring.jpa.hibernate.ddl-auto=validate` 설정을 유지했습니다.
- `mvn test`가 성공했습니다.
- PostgreSQL에 `flyway_schema_history` 테이블이 생성된 것을 확인했습니다.
- PostgreSQL에 `users`, `quiz_questions`, `quiz_choices`, `quiz_results`, `quiz_answers` 테이블이 생성된 것을 확인했습니다.

현재 상태:

- DB 테이블 생성은 Hibernate 자동 생성이 아니라 Flyway SQL로 관리합니다.
- JPA 엔티티는 Flyway가 만든 테이블과 매핑됩니다.
- 초기 문제 데이터 삽입과 실제 API 구현은 아직 진행하지 않았습니다.

### 백엔드 기본 구조 구현

작업 내용:

- `api`, `common`, `domain` 기준의 백엔드 패키지 구조를 추가했습니다.
- `/api/health` Health Check API를 추가했습니다.
- 공통 에러 응답 DTO인 `ErrorResponse`를 추가했습니다.
- 전역 예외 처리 클래스인 `GlobalExceptionHandler`를 추가했습니다.
- 공개 API와 보호 API를 구분하기 위한 기본 `SecurityConfig`를 추가했습니다.
- Health Check API 테스트를 추가했습니다.

확인한 내용:

- 최초 테스트에서 `/api/health`가 Spring Security 기본 설정 때문에 401로 막히는 것을 확인했습니다.
- `/api/health`, `/api/categories`, `/api/rankings`를 공개 API로 설정했습니다.
- `mvn test`가 성공했습니다.
- 백엔드 서버 실행 후 `http://127.0.0.1:8080/api/health`가 200 응답을 반환했습니다.

현재 상태:

- 백엔드 서버의 기본 패키지 구조가 준비되었습니다.
- 서버 상태 확인용 `/api/health` API가 준비되었습니다.
- 실제 퀴즈 API, DB 엔티티, Google 로그인 구현은 아직 진행하지 않았습니다.

### 프론트엔드 정적 화면 구현

작업 내용:

- Vite 기본 샘플 화면을 헷갈림 퀴즈 화면으로 교체했습니다.
- 홈, 로그인, 카테고리 선택, 퀴즈 풀이, 결과, 랭킹 화면을 구현했습니다.
- 실제 API 연결 전 화면 확인을 위해 더미 카테고리, 문제, 랭킹 데이터를 추가했습니다.
- 로그인과 화면 이동은 React 상태로 임시 처리했습니다.
- 문제 선택, 이전/다음 이동, 미응답 제출 안내, 결과 확인 흐름을 구현했습니다.

확인한 내용:

- `npm run lint`가 성공했습니다.
- `npm run build`가 성공했습니다.
- 프론트엔드 개발 서버가 `http://127.0.0.1:5173`에서 정상 응답했습니다.

발생한 문제:

| 문제 | 원인 | 대응 |
|---|---|---|
| 일반 실행에서 `npm run build` 실패 | Vite 내부 프로세스 실행이 권한 제한으로 막힘 | 승인 실행으로 빌드를 다시 실행해 성공 확인 |

현재 상태:

- API 연결 없이 화면 흐름을 확인할 수 있습니다.
- 실제 Google 로그인, 문제 조회, 결과 저장은 아직 연결하지 않았습니다.
- 다음 단계에서 화면 구조를 기준으로 API 연결 범위를 나누면 됩니다.

### 프로젝트 초기 세팅

작업 내용:

- `frontend` 폴더에 React + TypeScript + Vite 프로젝트를 생성했습니다.
- `backend` 폴더에 Spring Boot + Maven 프로젝트를 생성했습니다.
- 로컬 개발용 PostgreSQL 실행을 위해 `docker-compose.yml`을 추가했습니다.
- Git에 올리지 않을 파일을 관리하기 위해 루트 `.gitignore`를 추가했습니다.
- 환경 변수 예시를 정리하기 위해 `.env.example`을 추가했습니다.
- 실제 생성된 프로젝트 구조에 맞게 `README.md`를 수정했습니다.
- 백엔드 DB 연결 기본 설정을 `backend/src/main/resources/application.properties`에 추가했습니다.

확인한 내용:

- Node.js, npm, Java, Maven, Docker 명령어가 설치되어 있는 것을 확인했습니다.
- 프론트엔드 의존성 설치를 완료했습니다.
- 프론트엔드 빌드가 성공했습니다.
- 백엔드 패키징이 성공했습니다.

발생한 문제:

| 문제 | 원인 | 대응 |
|---|---|---|
| `npm create vite` 실행 실패 | npm 패키지를 내려받기 위한 네트워크 접근이 제한됨 | 승인 실행으로 Vite 템플릿을 내려받아 생성 |
| `npm install` 실행 실패 | npm 패키지를 내려받기 위한 네트워크 접근이 제한됨 | 승인 실행으로 프론트엔드 의존성 설치 |
| Maven Wrapper 실행 실패 | 사용자 홈의 `.m2` 폴더에 wrapper 파일을 생성할 권한이 제한됨 | 설치된 Maven 명령어인 `mvn`으로 대체 실행 |
| 백엔드 테스트 실패 | JPA 의존성이 있는데 DB 접속 설정이 없어서 `DataSource`를 만들 수 없음 | `application.properties`에 PostgreSQL 접속 기본값 추가 |
| `docker compose up -d` 실행 실패 | Docker Desktop 엔진이 실행 중이 아니거나 현재 실행 권한에서 Docker API 접근이 제한됨 | Docker Desktop 실행 상태 확인 후 승인 실행으로 PostgreSQL 컨테이너 실행 |
| 프론트엔드 빌드 실패 | Vite 내부 프로세스 실행이 권한 제한으로 막힘 | 승인 실행으로 `npm run build` 재실행 |
| 백엔드 패키징 실패 | Maven 플러그인을 사용자 홈 `.m2`에 내려받는 과정에서 권한 제한 발생 | 승인 실행으로 `mvn -DskipTests package` 재실행 |

현재 상태:

- 프론트엔드 프로젝트는 생성과 빌드 확인이 완료되었습니다.
- 백엔드 프로젝트는 생성과 패키징 확인이 완료되었습니다.
- PostgreSQL 컨테이너 실행 확인이 완료되었습니다.
- 백엔드 테스트가 성공했습니다.

다음 작업:

1. 현재 확인 결과 커밋
2. 프론트엔드 개발 서버 실행 확인
3. 백엔드 서버 실행 확인
4. 2단계 프론트엔드 정적 화면 구현 시작
