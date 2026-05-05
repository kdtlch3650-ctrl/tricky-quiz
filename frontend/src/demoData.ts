import type {
  CategoryCode,
  QuizQuestion,
  QuizQuestionsResponse,
  QuizSubmitRequest,
  QuizSubmitResponse,
  RankingEntry,
  RankingResponse,
} from './api'

type DemoQuestion = QuizQuestion & {
  correctChoiceId: number
  explanation: string
}

const demoChoices = [
  { id: 1, text: 'O' },
  { id: 2, text: 'X' },
] as const

function buildQuestion(
  id: number,
  questionText: string,
  correctChoiceId: number,
  explanation: string,
): DemoQuestion {
  return {
    id,
    questionText,
    choices: demoChoices.map((choice) => ({ ...choice })),
    correctChoiceId,
    explanation,
  }
}

const demoQuestions: Record<CategoryCode, DemoQuestion[]> = {
  GENERAL: [
    buildQuestion(1, '돌고래는 물고기다.', 2, '돌고래는 포유류다.'),
    buildQuestion(2, '금은 녹슬지 않는다.', 1, '금은 산화에 매우 강하다.'),
    buildQuestion(3, '인간은 뇌의 10%만 사용한다.', 2, '뇌는 여러 영역이 함께 사용된다.'),
    buildQuestion(4, '번개는 같은 장소에 여러 번 칠 수 있다.', 1, '동일 지점에 반복될 수 있다.'),
    buildQuestion(5, '꿀은 상하지 않는다.', 1, '미생물 번식이 어렵다.'),
    buildQuestion(6, '북극에는 펭귄이 산다.', 2, '펭귄은 남반구에 주로 산다.'),
    buildQuestion(7, '박쥐는 눈이 멀다.', 2, '박쥐도 시각을 사용한다.'),
    buildQuestion(8, '인간의 혈액은 파란색이다.', 2, '혈액은 항상 붉은색 계열이다.'),
    buildQuestion(9, '유리는 액체다.', 2, '유리는 고체 상태다.'),
    buildQuestion(10, '상어는 뼈가 없다.', 1, '상어는 연골로 이루어져 있다.'),
  ],
  IT: [
    buildQuestion(1, '인터넷과 웹은 같은 것이다.', 2, '웹은 인터넷 위의 서비스다.'),
    buildQuestion(2, 'HTML은 프로그래밍 언어다.', 2, 'HTML은 마크업 언어다.'),
    buildQuestion(3, 'Java와 JavaScript는 같은 언어다.', 2, '완전히 다른 언어다.'),
    buildQuestion(4, '컴퓨터는 0과 1로 데이터를 처리한다.', 1, '이진수 기반으로 동작한다.'),
    buildQuestion(5, 'RAM은 전원이 꺼지면 데이터가 사라진다.', 1, 'RAM은 휘발성 메모리다.'),
    buildQuestion(6, 'SSD는 HDD보다 일반적으로 빠르다.', 1, 'SSD는 접근 속도가 빠르다.'),
    buildQuestion(7, 'IP 주소는 항상 고정이다.', 2, '동적 IP도 많이 사용된다.'),
    buildQuestion(8, 'HTTPS는 HTTP보다 안전하다.', 1, '암호화 통신을 사용한다.'),
    buildQuestion(9, 'DNS는 도메인을 IP로 변환한다.', 1, '주소 변환 역할을 한다.'),
    buildQuestion(10, 'API는 프로그램 간 통신 방법이다.', 1, '서로 다른 프로그램을 연결한다.'),
  ],
  SCIENCE: [
    buildQuestion(1, '물은 H2O로 이루어져 있다.', 1, '수소 2개와 산소 1개다.'),
    buildQuestion(2, '태양은 노란색이다.', 2, '실제로는 거의 흰색에 가깝다.'),
    buildQuestion(3, '우주는 계속 팽창하고 있다.', 1, '관측 결과가 이를 뒷받침한다.'),
    buildQuestion(4, '소리는 진공에서 전달된다.', 2, '소리는 매질이 필요하다.'),
    buildQuestion(5, '빛의 속도는 일정하다.', 1, '진공에서 일정하다.'),
    buildQuestion(6, '지구는 완벽한 구형이다.', 2, '약간 납작한 타원체에 가깝다.'),
    buildQuestion(7, 'DNA는 모든 생물에 존재한다.', 1, '생명체의 기본 물질이다.'),
    buildQuestion(8, '철은 물에 뜬다.', 2, '대부분 가라앉는다.'),
    buildQuestion(9, '물은 얼면 부피가 줄어든다.', 2, '얼 때 부피가 늘어난다.'),
    buildQuestion(10, '중력은 모든 물체에 작용한다.', 1, '질량이 있는 물체 사이에 작용한다.'),
  ],
  LIFE: [
    buildQuestion(1, '사람은 물 없이 오래 살 수 없다.', 1, '생존에 필수적이다.'),
    buildQuestion(2, '하루 8시간 수면은 모든 사람에게 필수다.', 2, '개인차가 있다.'),
    buildQuestion(3, '스트레스는 항상 나쁘다.', 2, '적당한 스트레스는 도움이 된다.'),
    buildQuestion(4, '운동은 정신 건강에도 좋다.', 1, '심리적으로도 긍정적이다.'),
    buildQuestion(5, '커피는 항상 건강에 해롭다.', 2, '적당량은 문제가 되지 않는다.'),
    buildQuestion(6, '휴대폰은 수면에 영향을 줄 수 있다.', 1, '블루라이트가 영향을 줄 수 있다.'),
    buildQuestion(7, '웃음은 스트레스 감소에 도움 된다.', 1, '심리적 완화 효과가 있다.'),
    buildQuestion(8, '모든 지방은 나쁘다.', 2, '좋은 지방도 있다.'),
    buildQuestion(9, '규칙적인 운동은 수명을 늘릴 수 있다.', 1, '건강 개선에 도움이 된다.'),
    buildQuestion(10, '수면 부족은 건강에 영향을 준다.', 1, '다양한 신체 기능에 영향을 준다.'),
  ],
}

const demoRankings: Record<CategoryCode, RankingEntry[]> = {
  GENERAL: [
    {
      rank: 1,
      nickname: '데모 챔피언',
      score: 10,
      totalCount: 10,
      elapsedSeconds: 18,
      playedAt: '2026-05-05T14:22:00Z',
    },
    {
      rank: 2,
      nickname: '트릭러버',
      score: 9,
      totalCount: 10,
      elapsedSeconds: 24,
      playedAt: '2026-05-05T13:04:00Z',
    },
  ],
  IT: [
    {
      rank: 1,
      nickname: '코드마스터',
      score: 10,
      totalCount: 10,
      elapsedSeconds: 19,
      playedAt: '2026-05-05T15:08:00Z',
    },
    {
      rank: 2,
      nickname: '버그헌터',
      score: 8,
      totalCount: 10,
      elapsedSeconds: 28,
      playedAt: '2026-05-05T12:41:00Z',
    },
  ],
  SCIENCE: [
    {
      rank: 1,
      nickname: '실험러',
      score: 10,
      totalCount: 10,
      elapsedSeconds: 17,
      playedAt: '2026-05-05T11:38:00Z',
    },
    {
      rank: 2,
      nickname: '관측자',
      score: 9,
      totalCount: 10,
      elapsedSeconds: 23,
      playedAt: '2026-05-05T09:12:00Z',
    },
  ],
  LIFE: [
    {
      rank: 1,
      nickname: '생활인',
      score: 10,
      totalCount: 10,
      elapsedSeconds: 16,
      playedAt: '2026-05-05T16:00:00Z',
    },
    {
      rank: 2,
      nickname: '루틴러',
      score: 8,
      totalCount: 10,
      elapsedSeconds: 26,
      playedAt: '2026-05-05T10:18:00Z',
    },
  ],
}

function getDemoQuestionBank(category: CategoryCode) {
  return demoQuestions[category]
}

export const DEMO_CURRENT_USER = {
  id: 0,
  email: 'demo@trickyquiz.local',
  nickname: '데모 사용자',
  provider: 'DEMO',
}

export function getDemoQuizQuestions(category: CategoryCode): QuizQuestionsResponse {
  const questions = getDemoQuestionBank(category).map(({ id, questionText, choices }) => ({
    id,
    questionText,
    choices,
  }))

  return {
    category,
    totalCount: questions.length,
    questions,
  }
}

export function submitDemoQuizResult(request: QuizSubmitRequest): QuizSubmitResponse {
  const questionBank = getDemoQuestionBank(request.category)
  const questionMap = new Map(questionBank.map((question) => [question.id, question]))

  const answers = request.answers.map((answer) => {
    const question = questionMap.get(answer.questionId)
    if (!question) {
      throw new Error('데모 문제를 찾을 수 없습니다.')
    }

    const selectedChoice = question.choices.find((choice) => choice.id === answer.selectedChoiceId)
    const correctChoice = question.choices.find((choice) => choice.id === question.correctChoiceId)

    if (!selectedChoice || !correctChoice) {
      throw new Error('데모 선택지를 찾을 수 없습니다.')
    }

    return {
      questionId: question.id,
      questionText: question.questionText,
      selectedChoiceId: selectedChoice.id,
      selectedChoiceText: selectedChoice.text,
      correctChoiceId: correctChoice.id,
      correctChoiceText: correctChoice.text,
      correct: selectedChoice.id === correctChoice.id,
      explanation: question.explanation,
    }
  })

  return {
    resultId: Date.now(),
    category: request.category,
    score: answers.filter((answer) => answer.correct).length,
    totalCount: questionBank.length,
    elapsedSeconds: request.elapsedSeconds,
    answers,
  }
}

export function getDemoRankings(category: CategoryCode): RankingResponse {
  return {
    category,
    rankings: demoRankings[category],
  }
}
