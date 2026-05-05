# 테스트 계획

이 문서는 트릭퀴즈 프로젝트의 테스트 전략과 실제 시나리오를 정리한 문서입니다.

## 1. 테스트 전략

이 프로젝트의 테스트는 다음 4가지로 나눈다.

1. 단위 테스트
2. 슬라이스 테스트
3. 통합 테스트
4. 수동 테스트

이렇게 나누는 이유는 테스트 목적이 다르기 때문이다.

- 단위 테스트는 함수나 클래스 내부 로직이 맞는지 확인한다.
- 슬라이스 테스트는 특정 계층만 잘 붙는지 확인한다.
- 통합 테스트는 여러 계층이 함께 동작하는지 확인한다.
- 수동 테스트는 실제 브라우저와 화면 흐름을 확인한다.

## 2. 단위 테스트

단위 테스트는 비즈니스 로직만 검증한다. DB나 웹 요청은 직접 다루지 않는다.

### 현재 프로젝트의 단위 테스트 예시

- [`QuizServiceTest`](../backend/src/test/java/com/trickyquiz/backend/domain/quiz/QuizServiceTest.java)
- [`RankingServiceTest`](../backend/src/test/java/com/trickyquiz/backend/domain/quiz/RankingServiceTest.java)

### 검증 항목

- 문제를 카테고리별로 10개 가져오는지 확인한다.
- 제출한 답안을 채점하는지 확인한다.
- 결과 점수와 정답 여부를 올바르게 계산하는지 확인한다.
- 랭킹 정렬 기준이 점수, 시간, 제출 시각 순서인지 확인한다.

### 목적

- 서비스 계층의 핵심 규칙을 빠르게 검증한다.
- DB나 웹 계층 문제와 분리해서 원인을 찾기 쉽게 만든다.

## 3. 슬라이스 테스트

슬라이스 테스트는 특정 계층만 잘 연결되는지 확인한다.

### 현재 프로젝트의 슬라이스 테스트 예시

- [`CategoryControllerTest`](../backend/src/test/java/com/trickyquiz/backend/api/category/CategoryControllerTest.java)
- [`HealthControllerTest`](../backend/src/test/java/com/trickyquiz/backend/api/health/HealthControllerTest.java)
- [`QuizControllerTest`](../backend/src/test/java/com/trickyquiz/backend/api/quiz/QuizControllerTest.java)
- [`RankingControllerTest`](../backend/src/test/java/com/trickyquiz/backend/api/ranking/RankingControllerTest.java)
- [`UserControllerTest`](../backend/src/test/java/com/trickyquiz/backend/api/user/UserControllerTest.java)
- [`QuizRepositoryTest`](../backend/src/test/java/com/trickyquiz/backend/domain/quiz/QuizRepositoryTest.java)

### 검증 항목

- 컨트롤러가 올바른 HTTP 상태를 반환하는지 확인한다.
- 요청 JSON이 응답 JSON으로 제대로 변환되는지 확인한다.
- Spring Security 설정 때문에 인증이 필요한 API와 필요 없는 API가 구분되는지 확인한다.
- Repository가 실제 PostgreSQL에 데이터를 저장하고 조회하는지 확인한다.

### 목적

- API 모양과 응답 형식을 빠르게 검증한다.
- 컨트롤러, 보안, DB 매핑 같은 경계 문제를 찾는다.

## 4. 통합 테스트

통합 테스트는 여러 계층이 같이 붙어서 실제에 가까운 방식으로 동작하는지 확인한다.

### 현재 프로젝트에서 통합 테스트로 볼 수 있는 것

- 전체 `mvn test`
- 실제 PostgreSQL을 사용하는 Repository 테스트
- 인증이 포함된 컨트롤러 흐름 검증

### 검증 항목

- 백엔드 애플리케이션이 실제 설정으로 시작되는지 확인한다.
- Spring Security, OAuth, Repository, Service가 함께 동작하는지 확인한다.
- Docker PostgreSQL과 연결된 상태에서 실제 테이블과 데이터가 정상인지 확인한다.

### 목적

- 단위 테스트나 슬라이스 테스트에서 놓친 결합 문제를 찾는다.
- 실제 실행 환경과 비슷한 조건에서 기능을 확인한다.

## 5. 수동 테스트

수동 테스트는 사람이 브라우저에서 직접 확인한다.

### 5-1. 로그인 시나리오

1. `http://localhost:5173`에 접속한다.
2. 로그인 버튼을 클릭한다.
3. Google OAuth 로그인 화면으로 이동하는지 확인한다.
4. 로그인 후 프론트 화면으로 돌아오는지 확인한다.
5. 상단에 사용자 닉네임이 표시되는지 확인한다.

기대 결과:

- 로그인 성공 후 사용자 정보가 상단에 표시된다.
- `/api/me`가 로그인 사용자 정보를 반환한다.

### 5-2. 로그아웃 시나리오

1. 로그인 상태에서 상단 닉네임을 클릭한다.
2. `로그아웃` 메뉴가 표시되는지 확인한다.
3. 메뉴 바깥을 클릭하면 메뉴가 닫히는지 확인한다.
4. `로그아웃`을 클릭한다.
5. 홈 화면으로 돌아오고 사용자 닉네임이 사라지는지 확인한다.
6. 새로고침 후에도 비로그인 상태인지 확인한다.

기대 결과:

- 세션이 종료된다.
- 프론트 상태도 초기화된다.

### 5-3. 카테고리 및 퀴즈 시작 시나리오

1. 로그인 상태에서 `퀴즈 시작`을 클릭한다.
2. 카테고리 목록이 보이는지 확인한다.
3. GENERAL, IT, SCIENCE, LIFE 중 하나를 선택한다.
4. `함정 퀴즈 시작` 버튼으로 문제 목록이 로드되는지 확인한다.
5. O/X 버튼이 보이는지 확인한다.

기대 결과:

- 카테고리별 문제 10개가 로드된다.
- 문제 화면으로 정상 진입한다.

### 5-4. 퀴즈 진행 시나리오

1. 문제를 하나 선택한다.
2. 같은 선택지를 한 번 더 누르면 다음 문제로 넘어가는지 확인한다.
3. 이전 버튼으로 이전 문제로 돌아갈 수 있는지 확인한다.
4. 마지막 문제에서는 같은 선택지를 다시 눌러 제출되는지 확인한다.

기대 결과:

- 답 선택 흐름이 끊기지 않는다.
- 마지막 문제에서 제출까지 자연스럽게 이어진다.

### 5-5. 결과 및 랭킹 시나리오

1. 퀴즈를 끝까지 제출한다.
2. 결과 점수와 해설이 보이는지 확인한다.
3. 랭킹 보기 버튼으로 랭킹 화면으로 이동하는지 확인한다.
4. 해당 카테고리 랭킹이 표시되는지 확인한다.

기대 결과:

- 결과가 `quiz_results`와 `quiz_answers`에 저장된다.
- 랭킹이 점수, 시간, 제출 시각 기준으로 정렬된다.

### 5-6. 모바일 시나리오

1. 브라우저 폭을 모바일 크기로 줄인다.
2. 상단 메뉴가 한 줄로 무너지지 않는지 확인한다.
3. 카테고리 버튼이 2열로 보이는지 확인한다.
4. 로그아웃 메뉴가 화면 밖으로 넘치지 않는지 확인한다.

기대 결과:

- 모바일에서 세로 스크롤 부담이 줄어든다.
- 주요 버튼을 손가락으로 누르기 쉽다.

## 6. 완료 기준

- 단위 테스트가 통과한다.
- 슬라이스 테스트가 통과한다.
- 통합 테스트 성격의 전체 `mvn test`가 통과한다.
- 프론트엔드 lint와 build가 통과한다.
- 로그인, 로그아웃, 퀴즈, 결과, 랭킹, 모바일 확인이 수동으로 완료된다.

## 7. 참고

- 로컬 개발 환경에서는 Google OAuth 설정값이 필요하다.
- DB는 Docker PostgreSQL의 `tricky_quiz`를 사용한다.
- 수동 테스트 중 문제가 생기면 `docs/09-dev-log.md`에 이슈와 원인을 함께 기록한다.

## 8. 테스트 시트

실제 수동 테스트 케이스는 아래 Google Sheets에서 관리한다.

- [테스트 케이스 엑셀](https://docs.google.com/spreadsheets/d/1mV7YmM_DHA_KmUq72hfjmdo5noZTF4epx7f-teHFLZM/edit?usp=sharing)
