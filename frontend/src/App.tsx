import { useEffect, useMemo, useState } from 'react'
import { getRankings, type CategoryCode, type RankingEntry } from './api'
import './App.css'

type Screen = 'home' | 'login' | 'categories' | 'quiz' | 'result' | 'ranking'

type Category = {
  code: CategoryCode
  name: string
  description: string
}

type Question = {
  id: number
  text: string
  choices: string[]
  answerIndex: number
  explanation: string
}

const categories: Category[] = [
  {
    code: 'GENERAL',
    name: '상식',
    description: '가볍게 풀 수 있는 일반 상식 문제',
  },
  {
    code: 'IT',
    name: 'IT',
    description: '개발, 컴퓨터, 인터넷 관련 문제',
  },
  {
    code: 'SCIENCE',
    name: '과학',
    description: '생활 속 과학과 기초 과학 문제',
  },
  {
    code: 'LIFE',
    name: '생활',
    description: '일상 상황과 생활 정보 문제',
  },
]

const sampleQuestions: Question[] = [
  {
    id: 1,
    text: '세계에서 가장 넓은 바다는 무엇일까요?',
    choices: ['대서양', '태평양', '인도양', '북극해'],
    answerIndex: 1,
    explanation: '태평양은 지구에서 가장 넓은 바다입니다.',
  },
  {
    id: 2,
    text: 'HTTP 상태 코드 404는 보통 무엇을 뜻할까요?',
    choices: ['성공', '권한 없음', '찾을 수 없음', '서버 오류'],
    answerIndex: 2,
    explanation: '404는 요청한 리소스를 찾을 수 없을 때 사용됩니다.',
  },
  {
    id: 3,
    text: '물의 화학식으로 올바른 것은 무엇일까요?',
    choices: ['CO2', 'H2O', 'O2', 'NaCl'],
    answerIndex: 1,
    explanation: '물은 수소 원자 2개와 산소 원자 1개로 이루어져 있습니다.',
  },
  {
    id: 4,
    text: '다음 중 브라우저에서 실행되는 언어는 무엇일까요?',
    choices: ['SQL', 'JavaScript', 'Docker', 'PostgreSQL'],
    answerIndex: 1,
    explanation: 'JavaScript는 웹 브라우저에서 실행되는 대표적인 언어입니다.',
  },
  {
    id: 5,
    text: '하루는 몇 시간일까요?',
    choices: ['12시간', '18시간', '24시간', '36시간'],
    answerIndex: 2,
    explanation: '일반적으로 하루는 24시간으로 계산합니다.',
  },
  {
    id: 6,
    text: 'Git에서 변경 이력을 저장하는 작업은 무엇일까요?',
    choices: ['commit', 'install', 'deploy', 'render'],
    answerIndex: 0,
    explanation: 'commit은 현재 변경사항을 하나의 이력으로 저장하는 작업입니다.',
  },
  {
    id: 7,
    text: '다음 중 데이터베이스에 가까운 것은 무엇일까요?',
    choices: ['React', 'Vite', 'PostgreSQL', 'CSS'],
    answerIndex: 2,
    explanation: 'PostgreSQL은 관계형 데이터베이스입니다.',
  },
  {
    id: 8,
    text: '소셜 로그인에서 자주 쓰이는 인증 표준은 무엇일까요?',
    choices: ['OAuth', 'HTML', 'PNG', 'UTF-8'],
    answerIndex: 0,
    explanation: 'OAuth는 외부 계정을 이용한 인증 흐름에서 자주 쓰입니다.',
  },
  {
    id: 9,
    text: 'CSS는 주로 무엇을 담당할까요?',
    choices: ['데이터 저장', '화면 스타일', '서버 실행', '버전 관리'],
    answerIndex: 1,
    explanation: 'CSS는 색상, 배치, 크기 같은 화면 스타일을 담당합니다.',
  },
  {
    id: 10,
    text: 'Docker Compose 파일의 일반적인 이름은 무엇일까요?',
    choices: ['package.json', 'README.md', 'docker-compose.yml', 'index.html'],
    answerIndex: 2,
    explanation: 'docker-compose.yml에 컨테이너 실행 설정을 작성합니다.',
  },
]

function App() {
  const [screen, setScreen] = useState<Screen>('home')
  const [loggedIn, setLoggedIn] = useState(false)
  const [selectedCategory, setSelectedCategory] = useState<CategoryCode>('GENERAL')
  const [questionIndex, setQuestionIndex] = useState(0)
  const [answers, setAnswers] = useState<Record<number, number>>({})
  const [notice, setNotice] = useState('')
  const [rankingEntries, setRankingEntries] = useState<RankingEntry[]>([])
  const [rankingLoading, setRankingLoading] = useState(false)
  const [rankingError, setRankingError] = useState('')

  const currentCategory = categories.find(
    (category) => category.code === selectedCategory,
  )
  const currentQuestion = sampleQuestions[questionIndex]

  const score = useMemo(
    () =>
      sampleQuestions.filter(
        (question) => answers[question.id] === question.answerIndex,
      ).length,
    [answers],
  )

  const goQuizStart = () => {
    if (!loggedIn) {
      setScreen('login')
      return
    }

    setScreen('categories')
  }

  const startQuiz = () => {
    setQuestionIndex(0)
    setAnswers({})
    setNotice('')
    setScreen('quiz')
  }

  const submitQuiz = () => {
    if (Object.keys(answers).length < sampleQuestions.length) {
      setNotice('아직 답을 선택하지 않은 문제가 있습니다.')
      return
    }

    setNotice('')
    setScreen('result')
  }

  const handleLogin = () => {
    setLoggedIn(true)
    setScreen('categories')
  }

  useEffect(() => {
    if (screen !== 'ranking') {
      return
    }

    let cancelled = false

    const loadRankings = async () => {
      setRankingLoading(true)
      setRankingError('')

      try {
        const response = await getRankings(selectedCategory)
        if (!cancelled) {
          setRankingEntries(response.rankings)
        }
      } catch (error) {
        if (!cancelled) {
          setRankingEntries([])
          setRankingError(
            error instanceof Error ? error.message : '랭킹을 불러오지 못했습니다.',
          )
        }
      } finally {
        if (!cancelled) {
          setRankingLoading(false)
        }
      }
    }

    void loadRankings()

    return () => {
      cancelled = true
    }
  }, [screen, selectedCategory])

  return (
    <div className="app-shell">
      <header className="topbar">
        <button className="brand" type="button" onClick={() => setScreen('home')}>
          헷갈림 퀴즈
        </button>
        <nav className="nav-actions" aria-label="주요 메뉴">
          <button type="button" onClick={goQuizStart}>
            퀴즈 시작
          </button>
          <button type="button" onClick={() => setScreen('ranking')}>
            랭킹
          </button>
          {loggedIn ? (
            <span className="user-chip">사용자</span>
          ) : (
            <button type="button" onClick={() => setScreen('login')}>
              로그인
            </button>
          )}
        </nav>
      </header>

      <main>
        {screen === 'home' && (
          <section className="hero-section">
            <div className="hero-copy">
              <p className="eyebrow">10문제 캐주얼 퀴즈</p>
              <h1>헷갈리는 상식을 가볍게 풀어보세요.</h1>
              <p className="lead">
                짧은 시간에 문제를 풀고 점수와 풀이 시간을 랭킹으로 비교하는
                웹 퀴즈 게임입니다.
              </p>
              <div className="button-row">
                <button className="primary-button" type="button" onClick={goQuizStart}>
                  퀴즈 시작
                </button>
                <button className="secondary-button" type="button" onClick={() => setScreen('ranking')}>
                  랭킹 보기
                </button>
              </div>
            </div>

            <div className="summary-panel">
              <div>
                <strong>4</strong>
                <span>카테고리</span>
              </div>
              <div>
                <strong>10</strong>
                <span>문제</span>
              </div>
              <div>
                <strong>랭킹</strong>
                <span>점수 비교</span>
              </div>
            </div>
          </section>
        )}

        {screen === 'login' && (
          <section className="center-section">
            <div className="narrow-panel">
              <p className="eyebrow">로그인 필요</p>
              <h1>퀴즈 결과 저장을 위해 로그인이 필요합니다.</h1>
              <p className="lead">
                실제 구현 단계에서는 Google OAuth 로그인 화면으로 이동합니다.
                지금은 정적 화면 확인을 위해 임시 로그인 버튼으로 진행합니다.
              </p>
              <button className="google-button" type="button" onClick={handleLogin}>
                Google로 계속하기
              </button>
            </div>
          </section>
        )}

        {screen === 'categories' && (
          <section className="page-section">
            <div className="section-heading">
              <p className="eyebrow">카테고리 선택</p>
              <h1>오늘 풀어볼 주제를 고르세요.</h1>
            </div>
            <div className="category-grid">
              {categories.map((category) => (
                <button
                  className={
                    category.code === selectedCategory
                      ? 'category-card selected'
                      : 'category-card'
                  }
                  key={category.code}
                  type="button"
                  onClick={() => setSelectedCategory(category.code)}
                >
                  <strong>{category.name}</strong>
                  <span>{category.description}</span>
                </button>
              ))}
            </div>
            <div className="button-row">
              <button className="primary-button" type="button" onClick={startQuiz}>
                {currentCategory?.name} 퀴즈 시작
              </button>
            </div>
          </section>
        )}

        {screen === 'quiz' && (
          <section className="page-section quiz-layout">
            <div className="quiz-header">
              <div>
                <p className="eyebrow">{currentCategory?.name} 퀴즈</p>
                <h1>
                  {questionIndex + 1} / {sampleQuestions.length}
                </h1>
              </div>
              <div className="progress-text">
                남은 문제 {sampleQuestions.length - questionIndex - 1}개
              </div>
            </div>

            <div className="progress-bar" aria-hidden="true">
              <span
                style={{
                  width: `${((questionIndex + 1) / sampleQuestions.length) * 100}%`,
                }}
              />
            </div>

            <article className="question-panel">
              <h2>{currentQuestion.text}</h2>
              <div className="choice-list">
                {currentQuestion.choices.map((choice, choiceIndex) => (
                  <button
                    className={
                      answers[currentQuestion.id] === choiceIndex
                        ? 'choice-button selected'
                        : 'choice-button'
                    }
                    key={choice}
                    type="button"
                    onClick={() =>
                      setAnswers((prev) => ({
                        ...prev,
                        [currentQuestion.id]: choiceIndex,
                      }))
                    }
                  >
                    <span>{choiceIndex + 1}</span>
                    {choice}
                  </button>
                ))}
              </div>
            </article>

            {notice && <p className="notice">{notice}</p>}

            <div className="button-row between">
              <button
                className="secondary-button"
                type="button"
                disabled={questionIndex === 0}
                onClick={() => setQuestionIndex((value) => value - 1)}
              >
                이전
              </button>
              {questionIndex < sampleQuestions.length - 1 ? (
                <button
                  className="primary-button"
                  type="button"
                  onClick={() => setQuestionIndex((value) => value + 1)}
                >
                  다음
                </button>
              ) : (
                <button className="primary-button" type="button" onClick={submitQuiz}>
                  제출
                </button>
              )}
            </div>
          </section>
        )}

        {screen === 'result' && (
          <section className="page-section">
            <div className="result-summary">
              <p className="eyebrow">퀴즈 결과</p>
              <h1>
                {score} / {sampleQuestions.length}점
              </h1>
              <p className="lead">풀이 시간 82초 기준의 더미 결과 화면입니다.</p>
              <div className="button-row">
                <button className="primary-button" type="button" onClick={() => setScreen('ranking')}>
                  랭킹 보기
                </button>
                <button className="secondary-button" type="button" onClick={() => setScreen('categories')}>
                  다시 풀기
                </button>
              </div>
            </div>

            <div className="answer-list">
              {sampleQuestions.map((question, index) => {
                const selected = answers[question.id]
                const correct = selected === question.answerIndex

                return (
                  <article className="answer-item" key={question.id}>
                    <div>
                      <strong>
                        {index + 1}. {question.text}
                      </strong>
                      <p>
                        내 선택: {selected === undefined ? '미선택' : question.choices[selected]}
                      </p>
                      <p>정답: {question.choices[question.answerIndex]}</p>
                      <p>{question.explanation}</p>
                    </div>
                    <span className={correct ? 'result-badge correct' : 'result-badge wrong'}>
                      {correct ? '정답' : '오답'}
                    </span>
                  </article>
                )
              })}
            </div>
          </section>
        )}

        {screen === 'ranking' && (
          <section className="page-section">
            <div className="section-heading">
              <p className="eyebrow">랭킹</p>
              <h1>카테고리별 점수를 비교하세요.</h1>
            </div>
            <div className="tab-list" role="tablist" aria-label="랭킹 카테고리">
              {categories.map((category) => (
                <button
                  className={category.code === selectedCategory ? 'tab selected' : 'tab'}
                  key={category.code}
                  type="button"
                  onClick={() => setSelectedCategory(category.code)}
                >
                  {category.name}
                </button>
              ))}
            </div>
            {rankingLoading && <p className="notice">랭킹을 불러오는 중입니다.</p>}
            {rankingError && <p className="notice">{rankingError}</p>}
            <div className="ranking-table" role="table" aria-label="랭킹 목록">
              <div className="ranking-row heading" role="row">
                <span>순위</span>
                <span>닉네임</span>
                <span>점수</span>
                <span>시간</span>
                <span>일시</span>
              </div>
              {!rankingLoading && rankingEntries.length === 0 && !rankingError ? (
                <div className="ranking-row empty" role="row">
                  <span>아직 기록이 없습니다.</span>
                </div>
              ) : null}
              {rankingEntries.map((ranking) => (
                <div className="ranking-row" role="row" key={ranking.rank}>
                  <span>{ranking.rank}</span>
                  <span>{ranking.nickname}</span>
                  <span>
                    {ranking.score}/{ranking.totalCount}
                  </span>
                  <span>{ranking.elapsedSeconds}초</span>
                  <span>{formatPlayedAt(ranking.playedAt)}</span>
                </div>
              ))}
            </div>
            <div className="button-row">
              <button className="primary-button" type="button" onClick={goQuizStart}>
                퀴즈 다시 시작
              </button>
              <button className="secondary-button" type="button" onClick={() => setScreen('home')}>
                홈으로
              </button>
            </div>
          </section>
        )}
      </main>
    </div>
  )
}

function formatPlayedAt(value: string) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }

  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }).format(date)
}

export default App
