# 개발 기록

## 2026-05-04

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
| `docker compose up -d` 실행 실패 | Docker Desktop 엔진이 실행 중이 아님 | Docker Desktop 실행 후 다시 확인 예정 |
| 프론트엔드 빌드 실패 | Vite 내부 프로세스 실행이 권한 제한으로 막힘 | 승인 실행으로 `npm run build` 재실행 |
| 백엔드 패키징 실패 | Maven 플러그인을 사용자 홈 `.m2`에 내려받는 과정에서 권한 제한 발생 | 승인 실행으로 `mvn -DskipTests package` 재실행 |

현재 상태:

- 프론트엔드 프로젝트는 생성과 빌드 확인이 완료되었습니다.
- 백엔드 프로젝트는 생성과 패키징 확인이 완료되었습니다.
- DB 실행 확인은 Docker Desktop 실행 후 다시 진행해야 합니다.
- 백엔드 테스트는 DB가 실행된 뒤 다시 확인해야 합니다.

다음 작업:

1. Docker Desktop 실행
2. `docker compose up -d`로 PostgreSQL 실행 확인
3. `mvn test`로 백엔드 테스트 확인
4. 초기 세팅 작업 커밋
