# DB 설계서

## 1. 설계 범위

초기 버전에서는 Google 로그인 사용자, 퀴즈 문제, 선택지, 퀴즈 플레이 결과, 문제별 답안 기록을 저장합니다.

랭킹은 별도 테이블로 중복 저장하지 않고, 퀴즈 플레이 결과 데이터를 기준으로 조회합니다.

## 2. ERD 개요

```text
users
  └─ quiz_results
       └─ quiz_answers

quiz_questions
  ├─ quiz_choices
  └─ quiz_answers
```

관계 요약:

| 관계 | 설명 |
|---|---|
| users 1:N quiz_results | 한 사용자는 여러 번 퀴즈를 플레이할 수 있다. |
| quiz_results 1:N quiz_answers | 한 번의 퀴즈 결과는 여러 문제별 답안을 가진다. |
| quiz_questions 1:N quiz_choices | 한 문제는 4개의 선택지를 가진다. |
| quiz_questions 1:N quiz_answers | 한 문제는 여러 사용자의 답안 기록에 연결될 수 있다. |

## 3. 테이블 목록

| 테이블명 | 설명 |
|---|---|
| users | Google 로그인 사용자 정보 |
| quiz_questions | 퀴즈 문제 |
| quiz_choices | 퀴즈 선택지 |
| quiz_results | 퀴즈 플레이 결과 |
| quiz_answers | 문제별 사용자 답안 |

## 4. 테이블 상세

### users

Google 로그인 사용자의 기본 정보를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 사용자 ID |
| provider | VARCHAR(30) | NOT NULL | 로그인 제공자. 초기 버전은 `GOOGLE` |
| provider_user_id | VARCHAR(100) | NOT NULL | Google 계정 식별값 |
| email | VARCHAR(255) | NOT NULL | 사용자 이메일 |
| nickname | VARCHAR(100) | NOT NULL | 화면에 표시할 닉네임 |
| created_at | TIMESTAMP | NOT NULL | 최초 로그인 일시 |
| updated_at | TIMESTAMP | NOT NULL | 사용자 정보 수정 일시 |

인덱스:

| 인덱스 | 컬럼 | 설명 |
|---|---|---|
| UK_users_provider_user_id | provider, provider_user_id | 같은 Google 계정 중복 저장 방지 |
| UK_users_email | email | 이메일 중복 방지 |

### quiz_questions

퀴즈 문제 정보를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 문제 ID |
| category | VARCHAR(30) | NOT NULL | 카테고리. `GENERAL`, `IT`, `SCIENCE`, `LIFE` |
| question_text | TEXT | NOT NULL | 문제 내용 |
| explanation | TEXT | NULL | 정답 해설 |
| active | BOOLEAN | NOT NULL | 출제 가능 여부 |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 |

인덱스:

| 인덱스 | 컬럼 | 설명 |
|---|---|---|
| IDX_quiz_questions_category_active | category, active | 카테고리별 출제 가능 문제 조회 |

### quiz_choices

각 문제의 선택지를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 선택지 ID |
| question_id | BIGINT | FK, NOT NULL | 문제 ID |
| choice_order | INT | NOT NULL | 선택지 순서. 1~4 |
| choice_text | TEXT | NOT NULL | 선택지 내용 |
| is_correct | BOOLEAN | NOT NULL | 정답 여부 |

제약조건:

| 제약조건 | 설명 |
|---|---|
| FK_quiz_choices_question | question_id는 quiz_questions.id를 참조 |
| UK_quiz_choices_question_order | 한 문제 안에서 선택지 순서는 중복될 수 없음 |
| UK_quiz_choices_correct | 한 문제에는 정답 선택지가 하나만 존재해야 함 |

### quiz_results

사용자가 한 번 퀴즈를 플레이한 결과를 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 결과 ID |
| user_id | BIGINT | FK, NOT NULL | 사용자 ID |
| category | VARCHAR(30) | NOT NULL | 플레이한 카테고리 |
| score | INT | NOT NULL | 맞힌 문제 수 |
| total_count | INT | NOT NULL | 전체 문제 수. 초기 버전은 10 |
| elapsed_seconds | INT | NOT NULL | 풀이 시간 |
| played_at | TIMESTAMP | NOT NULL | 플레이 완료 일시 |

인덱스:

| 인덱스 | 컬럼 | 설명 |
|---|---|---|
| IDX_quiz_results_ranking | category, score DESC, elapsed_seconds ASC, played_at ASC | 랭킹 조회 정렬 |
| IDX_quiz_results_user | user_id, played_at DESC | 사용자별 플레이 기록 조회 |

### quiz_answers

퀴즈 결과에 포함된 문제별 답안 기록을 저장합니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGSERIAL | PK | 답안 ID |
| result_id | BIGINT | FK, NOT NULL | 퀴즈 결과 ID |
| question_id | BIGINT | FK, NOT NULL | 문제 ID |
| selected_choice_id | BIGINT | FK, NOT NULL | 사용자가 선택한 선택지 ID |
| correct | BOOLEAN | NOT NULL | 정답 여부 |

제약조건:

| 제약조건 | 설명 |
|---|---|
| FK_quiz_answers_result | result_id는 quiz_results.id를 참조 |
| FK_quiz_answers_question | question_id는 quiz_questions.id를 참조 |
| FK_quiz_answers_selected_choice | selected_choice_id는 quiz_choices.id를 참조 |
| UK_quiz_answers_result_question | 한 결과 안에서 같은 문제는 한 번만 기록 |

## 5. 카테고리 코드

| 코드 | 화면 표시명 | 설명 |
|---|---|---|
| GENERAL | 상식 | 일반 상식 문제 |
| IT | IT | 개발, 컴퓨터, 인터넷 관련 문제 |
| SCIENCE | 과학 | 기초 과학과 생활 속 과학 문제 |
| LIFE | 생활 | 생활 정보와 상황 판단 문제 |

## 6. 랭킹 조회 기준

랭킹은 `quiz_results` 테이블을 기준으로 조회합니다.

정렬 기준:

1. `score` 높은 순
2. `elapsed_seconds` 낮은 순
3. `played_at` 빠른 순

카테고리별 랭킹만 초기 버전에서 제공합니다.

## 7. 데이터 저장 흐름

### 최초 로그인

```text
Google 로그인 성공
  ↓
provider + provider_user_id로 사용자 조회
  ↓
없으면 users에 신규 저장
  ↓
있으면 기존 사용자 정보 사용
```

### 퀴즈 제출

```text
사용자 답안 제출
  ↓
정답 여부 계산
  ↓
quiz_results 저장
  ↓
quiz_answers에 문제별 답안 저장
  ↓
결과 화면에 점수와 정답 여부 반환
```

## 8. 설계 메모

- 랭킹은 `quiz_results` 데이터를 기준으로 계산하므로 별도 랭킹 테이블을 만들지 않습니다.
- 문제 삭제는 초기 버전에서 제공하지 않고, `active` 값으로 출제 여부를 제어합니다.
- OpenAI 문제 생성과 관리자 검토 기능은 초기 버전에서 제외하므로 관련 테이블은 만들지 않습니다.
- 선택지는 각 문제당 4개를 기본으로 합니다.
- 하나의 문제에는 하나의 정답 선택지만 존재해야 합니다.
- 초기 버전에서는 퀴즈 시작 시 별도 라운드 테이블을 만들지 않고, 제출 완료 시 `quiz_results`를 저장합니다.

## ERD 링크
https://drive.google.com/file/d/1Skwmrr6G4MO5LNRt_-wZ00GbTrfBXWcg/view?usp=sharing
## 테이블 명세서
https://docs.google.com/spreadsheets/d/1LcSbtoZYwTN2SffkXg9lTdyw1kV6wSQjgHjYmQSTzRI/edit?usp=sharing