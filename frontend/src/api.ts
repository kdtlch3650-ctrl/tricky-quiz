export type CategoryCode = 'GENERAL' | 'IT' | 'SCIENCE' | 'LIFE'

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

type ApiErrorResponse = {
  message?: string
}

async function fetchJson<T>(path: string): Promise<T> {
  const response = await fetch(path, {
    headers: {
      Accept: 'application/json',
    },
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
