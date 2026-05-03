# API 명세서

## 1. API 설계 범위

초기 버전에서는 Google 로그인 사용자 조회, 카테고리 조회, 퀴즈 문제 조회, 퀴즈 결과 저장, 랭킹 조회 API를 제공합니다.

Google OAuth 로그인 자체는 Spring Security OAuth2 흐름을 사용합니다. 따라서 직접 회원가입 API나 이메일/비밀번호 로그인 API는 만들지 않습니다.

## 2. 공통 규칙

### Base URL

```text
http://localhost:8080
```

### 응답 형식

모든 API 응답은 JSON 형식을 사용합니다.

```http
Content-Type: application/json
```

### 인증 방식

초기 버전에서는 Google OAuth 로그인 후 Spring Security의 세션 기반 인증을 사용합니다.

보호 API는 로그인한 사용자만 호출할 수 있습니다.

### 공통 에러 응답

| 상태 코드 | 의미 | 발생 상황 |
|---|---|---|
| 400 | Bad Request | 요청 파라미터나 요청 본문이 올바르지 않음 |
| 401 | Unauthorized | 로그인이 필요한 API를 비로그인 상태로 호출 |
| 404 | Not Found | 요청한 리소스가 존재하지 않음 |
| 500 | Internal Server Error | 서버 내부 오류 |

에러 응답 예시:

```json
{
  "message": "요청을 처리할 수 없습니다."
}
```

## 3. 인증 API

### AUTH-001 Google 로그인 시작

| 항목 | 내용 |
|---|---|
| Method | GET |
| URL | `/oauth2/authorization/google` |
| 인증 필요 | 아니오 |
| 설명 | Google OAuth 로그인 화면으로 이동합니다. |
| 관련 화면 | 로그인 화면 |

요청 예시:

```http
GET /oauth2/authorization/google
```

처리:

```text
사용자가 Google 로그인 버튼 클릭
  ↓
Spring Security OAuth2가 Google 인증 화면으로 리다이렉트
```

### AUTH-002 Google 로그인 콜백

| 항목 | 내용 |
|---|---|
| Method | GET |
| URL | `/login/oauth2/code/google` |
| 인증 필요 | 아니오 |
| 설명 | Google 인증 성공 후 호출되는 콜백 URL입니다. |
| 관련 화면 | 로그인 화면 |

처리:

```text
Google 인증 성공
  ↓
서버가 사용자 정보를 조회
  ↓
users 테이블에 사용자 저장 또는 기존 사용자 조회
  ↓
프론트엔드 화면으로 이동
```

이 API는 사용자가 직접 호출하지 않고 OAuth 흐름에서 자동 호출됩니다.

### AUTH-003 로그아웃

| 항목 | 내용 |
|---|---|
| Method | POST |
| URL | `/logout` |
| 인증 필요 | 예 |
| 설명 | 현재 로그인 세션을 종료합니다. |
| 관련 화면 | 공통 상단 영역 |

응답 예시:

```json
{
  "success": true
}
```

## 4. 사용자 API

### USER-001 내 정보 조회

| 항목 | 내용 |
|---|---|
| Method | GET |
| URL | `/api/me` |
| 인증 필요 | 예 |
| 설명 | 현재 로그인한 사용자의 정보를 조회합니다. |
| 관련 화면 | 공통 상단 영역, 카테고리 선택 화면 |
| 관련 DB | users |

응답 예시:

```json
{
  "id": 1,
  "email": "user@example.com",
  "nickname": "사용자",
  "provider": "GOOGLE"
}
```

에러:

| 상태 코드 | 상황 |
|---|---|
| 401 | 로그인하지 않은 상태 |

## 5. 카테고리 API

### QUIZ-001 카테고리 목록 조회

| 항목 | 내용 |
|---|---|
| Method | GET |
| URL | `/api/categories` |
| 인증 필요 | 아니오 |
| 설명 | 퀴즈 카테고리 목록을 조회합니다. |
| 관련 화면 | 카테고리 선택 화면, 랭킹 화면 |

응답 예시:

```json
[
  {
    "code": "GENERAL",
    "name": "상식",
    "description": "일상에서 접하기 쉬운 일반 상식 문제"
  },
  {
    "code": "IT",
    "name": "IT",
    "description": "개발, 컴퓨터, 인터넷 관련 문제"
  },
  {
    "code": "SCIENCE",
    "name": "과학",
    "description": "기초 과학과 생활 속 과학 문제"
  },
  {
    "code": "LIFE",
    "name": "생활",
    "description": "생활 정보와 상황 판단 문제"
  }
]
```

## 6. 퀴즈 API

### QUIZ-002 퀴즈 문제 조회

| 항목 | 내용 |
|---|---|
| Method | GET |
| URL | `/api/quiz/questions?category={category}` |
| 인증 필요 | 예 |
| 설명 | 선택한 카테고리에 맞는 퀴즈 문제 10개를 조회합니다. |
| 관련 화면 | 퀴즈 풀이 화면 |
| 관련 DB | quiz_questions, quiz_choices |

요청 파라미터:

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| category | string | 예 | `GENERAL`, `IT`, `SCIENCE`, `LIFE` |

응답 예시:

```json
{
  "category": "GENERAL",
  "totalCount": 10,
  "questions": [
    {
      "id": 1,
      "questionText": "다음 중 헷갈리기 쉬운 표현으로 올바른 것은?",
      "choices": [
        {
          "id": 101,
          "text": "선택지 A"
        },
        {
          "id": 102,
          "text": "선택지 B"
        },
        {
          "id": 103,
          "text": "선택지 C"
        },
        {
          "id": 104,
          "text": "선택지 D"
        }
      ]
    }
  ]
}
```

주의:

- 정답 여부는 문제 조회 응답에 포함하지 않습니다.
- 정답과 해설은 결과 제출 후 반환합니다.

에러:

| 상태 코드 | 상황 |
|---|---|
| 400 | 지원하지 않는 카테고리 |
| 401 | 로그인하지 않은 상태 |

### QUIZ-003 퀴즈 결과 제출

| 항목 | 내용 |
|---|---|
| Method | POST |
| URL | `/api/quiz/results` |
| 인증 필요 | 예 |
| 설명 | 사용자의 퀴즈 답안을 제출하고 결과를 저장합니다. |
| 관련 화면 | 퀴즈 풀이 화면, 결과 화면 |
| 관련 DB | quiz_results, quiz_answers, quiz_questions, quiz_choices |

요청 본문:

```json
{
  "category": "GENERAL",
  "elapsedSeconds": 82,
  "answers": [
    {
      "questionId": 1,
      "selectedChoiceId": 102
    },
    {
      "questionId": 2,
      "selectedChoiceId": 205
    }
  ]
}
```

요청 필드:

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| category | string | 예 | 플레이한 카테고리 |
| elapsedSeconds | number | 예 | 풀이 시간 |
| answers | array | 예 | 문제별 답안 목록 |
| answers[].questionId | number | 예 | 문제 ID |
| answers[].selectedChoiceId | number | 예 | 선택한 선택지 ID |

응답 예시:

```json
{
  "resultId": 1,
  "category": "GENERAL",
  "score": 7,
  "totalCount": 10,
  "elapsedSeconds": 82,
  "answers": [
    {
      "questionId": 1,
      "questionText": "다음 중 헷갈리기 쉬운 표현으로 올바른 것은?",
      "selectedChoiceId": 102,
      "selectedChoiceText": "선택지 B",
      "correctChoiceId": 103,
      "correctChoiceText": "선택지 C",
      "correct": false,
      "explanation": "정답 해설입니다."
    }
  ]
}
```

처리:

```text
사용자 답안 제출
  ↓
서버에서 정답 여부 계산
  ↓
quiz_results 저장
  ↓
quiz_answers 저장
  ↓
결과 응답 반환
```

에러:

| 상태 코드 | 상황 |
|---|---|
| 400 | 답안 개수가 10개가 아니거나 유효하지 않은 선택지 |
| 401 | 로그인하지 않은 상태 |
| 404 | 존재하지 않는 문제 또는 선택지 |

## 7. 랭킹 API

### RANK-001 카테고리별 랭킹 조회

| 항목 | 내용 |
|---|---|
| Method | GET |
| URL | `/api/rankings?category={category}` |
| 인증 필요 | 아니오 |
| 설명 | 카테고리별 랭킹 목록을 조회합니다. |
| 관련 화면 | 랭킹 화면 |
| 관련 DB | quiz_results, users |

요청 파라미터:

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| category | string | 예 | `GENERAL`, `IT`, `SCIENCE`, `LIFE` |

응답 예시:

```json
{
  "category": "GENERAL",
  "rankings": [
    {
      "rank": 1,
      "nickname": "사용자A",
      "score": 10,
      "totalCount": 10,
      "elapsedSeconds": 61,
      "playedAt": "2026-05-03T20:00:00"
    },
    {
      "rank": 2,
      "nickname": "사용자B",
      "score": 9,
      "totalCount": 10,
      "elapsedSeconds": 55,
      "playedAt": "2026-05-03T20:05:00"
    }
  ]
}
```

정렬 기준:

1. 점수 높은 순
2. 점수가 같으면 풀이 시간 짧은 순
3. 점수와 풀이 시간이 같으면 먼저 제출한 순

에러:

| 상태 코드 | 상황 |
|---|---|
| 400 | 지원하지 않는 카테고리 |

## 8. API 목록 요약

| ID | Method | URL | 인증 필요 | 설명 |
|---|---|---|---|---|
| AUTH-001 | GET | `/oauth2/authorization/google` | 아니오 | Google 로그인 시작 |
| AUTH-002 | GET | `/login/oauth2/code/google` | 아니오 | Google 로그인 콜백 |
| AUTH-003 | POST | `/logout` | 예 | 로그아웃 |
| USER-001 | GET | `/api/me` | 예 | 내 정보 조회 |
| QUIZ-001 | GET | `/api/categories` | 아니오 | 카테고리 목록 조회 |
| QUIZ-002 | GET | `/api/quiz/questions` | 예 | 퀴즈 문제 조회 |
| QUIZ-003 | POST | `/api/quiz/results` | 예 | 퀴즈 결과 제출 |
| RANK-001 | GET | `/api/rankings` | 아니오 | 랭킹 조회 |

## 9. 설계 메모

- 문제 조회 API는 정답 정보를 내려주지 않습니다.
- 정답 검증은 클라이언트가 아니라 서버에서 처리합니다.
- 랭킹 조회는 비로그인 사용자도 사용할 수 있습니다.
- 결과 저장은 로그인한 사용자만 사용할 수 있습니다.
- 초기 버전에서는 별도 퀴즈 라운드 생성 API를 만들지 않습니다.
