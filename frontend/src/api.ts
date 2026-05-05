export type CategoryCode = 'GENERAL' | 'IT' | 'SCIENCE' | 'LIFE'

export type CategoryItem = {
  code: CategoryCode
  name: string
  description: string
}

export type QuizChoice = {
  id: number
  text: string
}

export type QuizQuestion = {
  id: number
  questionText: string
  choices: QuizChoice[]
}

export type QuizQuestionsResponse = {
  category: CategoryCode
  totalCount: number
  questions: QuizQuestion[]
}

export type QuizSubmitAnswerRequest = {
  questionId: number
  selectedChoiceId: number
}

export type QuizSubmitRequest = {
  category: CategoryCode
  elapsedSeconds: number
  answers: QuizSubmitAnswerRequest[]
}

export type QuizSubmitAnswerResponse = {
  questionId: number
  questionText: string
  selectedChoiceId: number
  selectedChoiceText: string
  correctChoiceId: number
  correctChoiceText: string
  correct: boolean
  explanation: string
}

export type QuizSubmitResponse = {
  resultId: number
  category: CategoryCode
  score: number
  totalCount: number
  elapsedSeconds: number
  answers: QuizSubmitAnswerResponse[]
}

export type RankingEntry = {
  rank: number
  nickname: string
  score: number
  totalCount: number
  elapsedSeconds: number
  playedAt: string
}

export type RankingResponse = {
  category: string
  rankings: RankingEntry[]
}

export type CurrentUser = {
  id: number
  email: string
  nickname: string
  provider: string
}

type ApiErrorResponse = {
  message?: string
}

async function fetchJson<T>(path: string, init?: RequestInit): Promise<T> {
  const { headers: initHeaders, ...restInit } = init ?? {}
  const headers = new Headers(initHeaders)
  headers.set('Accept', 'application/json')

  const response = await fetch(path, {
    headers,
    ...restInit,
  })

  if (!response.ok) {
    let message = `요청 실패: ${response.status}`

    try {
      const body = (await response.json()) as ApiErrorResponse
      if (body.message) {
        message = body.message
      }
    } catch {
      // 서버가 JSON을 주지 않으면 기본 메시지를 유지합니다.
    }

    throw new Error(message)
  }

  return (await response.json()) as T
}

/**
 * 카테고리별 랭킹 목록을 가져옵니다.
 */
export function getRankings(category: CategoryCode): Promise<RankingResponse> {
  return fetchJson<RankingResponse>(`/api/rankings?category=${category}`)
}

/**
 * 현재 로그인한 사용자의 정보를 가져옵니다.
 */
export function getCurrentUser(): Promise<CurrentUser> {
  return fetchJson<CurrentUser>('/api/me')
}

/**
 * 카테고리 목록을 가져옵니다.
 */
export function getCategories(): Promise<CategoryItem[]> {
  return fetchJson<CategoryItem[]>('/api/categories')
}

/**
 * 퀴즈 문제 목록을 가져옵니다.
 */
export function getQuizQuestions(category: CategoryCode): Promise<QuizQuestionsResponse> {
  return fetchJson<QuizQuestionsResponse>(`/api/quiz/questions?category=${category}`)
}

/**
 * 퀴즈 결과를 제출합니다.
 */
export function submitQuizResult(request: QuizSubmitRequest): Promise<QuizSubmitResponse> {
  return fetchJson<QuizSubmitResponse>('/api/quiz/results', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  })
}

/**
 * 현재 로그인 세션을 종료합니다.
 */
export async function logoutCurrentUser(): Promise<void> {
  const response = await fetch('/api/auth/logout', {
    method: 'POST',
    credentials: 'include',
    headers: {
      Accept: 'application/json',
    },
  })

  if (!response.ok) {
    throw new Error(`요청 실패: ${response.status}`)
  }
}
