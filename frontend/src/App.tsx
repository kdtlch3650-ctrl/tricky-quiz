import { useEffect, useState } from 'react'
import {
  getCategories,
  getCurrentUser,
  getQuizQuestions,
  getRankings,
  submitQuizResult,
  type CategoryCode,
  type CategoryItem,
  type CurrentUser,
  type QuizQuestion,
  type QuizSubmitRequest,
  type QuizSubmitResponse,
  type RankingEntry,
} from './api'
import './App.css'

type Screen = 'home' | 'login' | 'categories' | 'quiz' | 'result' | 'ranking'
type Category = CategoryItem

const fallbackCategories: Category[] = [
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

function App() {
  const [screen, setScreen] = useState<Screen>('home')
  const [loggedIn, setLoggedIn] = useState(false)
  const [selectedCategory, setSelectedCategory] = useState<CategoryCode>('GENERAL')
  const [categories, setCategories] = useState<Category[]>(fallbackCategories)
  const [quizQuestions, setQuizQuestions] = useState<QuizQuestion[]>([])
  const [questionIndex, setQuestionIndex] = useState(0)
  const [answers, setAnswers] = useState<Record<number, number>>({})
  const [notice, setNotice] = useState('')
  const [quizLoading, setQuizLoading] = useState(false)
  const [quizError, setQuizError] = useState('')
  const [quizStartedAt, setQuizStartedAt] = useState<number | null>(null)
  const [quizResult, setQuizResult] = useState<QuizSubmitResponse | null>(null)
  const [rankingEntries, setRankingEntries] = useState<RankingEntry[]>([])
  const [rankingLoading, setRankingLoading] = useState(false)
  const [rankingError, setRankingError] = useState('')
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null)

  const currentCategory = categories.find((category) => category.code === selectedCategory)
  const currentQuestion = quizQuestions[questionIndex]
  const resultCategory = categories.find((category) => category.code === quizResult?.category)
  const displayCategory = resultCategory ?? currentCategory

  const goQuizStart = () => {
    if (!loggedIn) {
      setScreen('login')
      return
    }

    setNotice('')
    setQuizError('')
    setScreen('categories')
  }

  const startQuiz = async () => {
    if (quizLoading) {
      return
    }

    setQuizLoading(true)
    setQuizError('')
    setNotice('')

    try {
      const response = await getQuizQuestions(selectedCategory)
      setQuizQuestions(response.questions)
      setQuestionIndex(0)
      setAnswers({})
      setQuizResult(null)
      setQuizStartedAt(Date.now())
      setScreen('quiz')
    } catch (error) {
      setQuizError(
        error instanceof Error ? error.message : '퀴즈 문제를 불러오지 못했습니다.',
      )
      setScreen('categories')
    } finally {
      setQuizLoading(false)
    }
  }

  const submitQuiz = async () => {
    if (quizLoading) {
      return
    }

    if (quizQuestions.length === 0) {
      setNotice('불러온 문제가 없습니다.')
      return
    }

    const unansweredQuestion = quizQuestions.find((question) => answers[question.id] === undefined)
    if (unansweredQuestion) {
      setNotice('아직 답을 고르지 않은 문제가 있습니다.')
      return
    }

    if (quizStartedAt === null) {
      setNotice('퀴즈 시작 시간을 확인할 수 없습니다.')
      return
    }

    const payload: QuizSubmitRequest = {
      category: selectedCategory,
      elapsedSeconds: Math.max(1, Math.round((Date.now() - quizStartedAt) / 1000)),
      answers: quizQuestions.map((question) => ({
        questionId: question.id,
        selectedChoiceId: answers[question.id]!,
      })),
    }

    setQuizLoading(true)
    setNotice('')

    try {
      const response = await submitQuizResult(payload)
      setQuizResult(response)
      setScreen('result')
    } catch (error) {
      setNotice(error instanceof Error ? error.message : '퀴즈 결과를 제출하지 못했습니다.')
    } finally {
      setQuizLoading(false)
    }
  }

  const handleLogin = () => {
    setLoggedIn(true)
    setScreen('categories')
  }

  useEffect(() => {
    let cancelled = false

    const loadCategories = async () => {
      try {
        const response = await getCategories()
        if (!cancelled) {
          setCategories(response)
        }
      } catch {
        if (!cancelled) {
          setCategories(fallbackCategories)
        }
      }
    }

    void loadCategories()

    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    let cancelled = false

    const loadCurrentUser = async () => {
      try {
        const user = await getCurrentUser()
        if (!cancelled) {
          setCurrentUser(user)
          setLoggedIn(true)
        }
      } catch {
        if (!cancelled) {
          setCurrentUser(null)
          setLoggedIn(false)
        }
      }
    }

    void loadCurrentUser()

    return () => {
      cancelled = true
    }
  }, [])

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
          트릭퀴즈
        </button>
        <nav className="nav-actions" aria-label="주요 메뉴">
          <button type="button" onClick={goQuizStart}>
            퀴즈 시작
          </button>
          <button type="button" onClick={() => setScreen('ranking')}>
            랭킹
          </button>
          {loggedIn ? (
            <span className="user-chip">{currentUser?.nickname ?? '사용자'}</span>
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
              <p className="eyebrow">10문제 챌린지 퀴즈</p>
              <h1>트릭퀴즈는 상식형으로 가볍게 풀어보세요.</h1>
              <p className="lead">
                짧은 시간에 문제를 풀고 점수와 순위를 확인하는 웹 퀴즈 프로젝트입니다.
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
                <strong>순위</strong>
                <span>실시간 비교</span>
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
                Google OAuth 로그인으로 이동하고, 로그인 후 사용자 정보를 상단에 표시합니다.
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
              <h1>오늘 풀 주제와 문제를 골라주세요.</h1>
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
            {quizError && <p className="notice">{quizError}</p>}
            <div className="button-row">
              <button className="primary-button" type="button" onClick={startQuiz} disabled={quizLoading}>
                {quizLoading ? '불러오는 중...' : `${currentCategory?.name ?? selectedCategory} 퀴즈 시작`}
              </button>
            </div>
          </section>
        )}

        {screen === 'quiz' && (
          <section className="page-section quiz-layout">
            <div className="quiz-header">
              <div>
                <p className="eyebrow">{currentCategory?.name ?? selectedCategory} 퀴즈</p>
                <h1>
                  {questionIndex + 1} / {quizQuestions.length}
                </h1>
              </div>
              <div className="progress-text">
                남은 문제 {quizQuestions.length - questionIndex - 1}개
              </div>
            </div>

            <div className="progress-bar" aria-hidden="true">
              <span
                style={{
                  width: `${((questionIndex + 1) / quizQuestions.length) * 100}%`,
                }}
              />
            </div>

            {currentQuestion ? (
              <article className="question-panel">
                <h2>{currentQuestion.questionText}</h2>
                <div className="choice-list">
                  {currentQuestion.choices.map((choice, choiceIndex) => (
                    <button
                      className={
                        answers[currentQuestion.id] === choice.id
                          ? 'choice-button selected'
                          : 'choice-button'
                      }
                      key={choice.id}
                      type="button"
                      onClick={() =>
                        setAnswers((prev) => ({
                          ...prev,
                          [currentQuestion.id]: choice.id,
                        }))
                      }
                    >
                      <span>{choiceIndex + 1}</span>
                      {choice.text}
                    </button>
                  ))}
                </div>
              </article>
            ) : (
              <article className="question-panel">
                <h2>문제를 불러오지 못했습니다.</h2>
              </article>
            )}

            {notice && <p className="notice">{notice}</p>}

            <div className="button-row between">
              <button
                className="secondary-button"
                type="button"
                disabled={questionIndex === 0 || quizLoading}
                onClick={() => setQuestionIndex((value) => value - 1)}
              >
                이전
              </button>
              {questionIndex < quizQuestions.length - 1 ? (
                <button
                  className="primary-button"
                  type="button"
                  disabled={quizLoading}
                  onClick={() => setQuestionIndex((value) => value + 1)}
                >
                  다음
                </button>
              ) : (
                <button className="primary-button" type="button" onClick={submitQuiz} disabled={quizLoading}>
                  {quizLoading ? '제출 중...' : '제출'}
                </button>
              )}
            </div>
          </section>
        )}

        {screen === 'result' && quizResult && (
          <section className="page-section">
            <div className="result-summary">
              <p className="eyebrow">퀴즈 결과</p>
              <h1>
                {quizResult.score} / {quizResult.totalCount}점
              </h1>
              <p className="lead">
                {displayCategory?.name ?? quizResult.category} 퀴즈를 {quizResult.elapsedSeconds}
                초 만에 완료했습니다.
              </p>
              <div className="button-row">
                <button className="primary-button" type="button" onClick={() => setScreen('ranking')}>
                  랭킹 보기
                </button>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => {
                    setScreen('categories')
                    setNotice('')
                  }}
                >
                  다시 풀기
                </button>
              </div>
            </div>

            <div className="answer-list">
              {quizResult.answers.map((answer, index) => (
                <article className="answer-item" key={answer.questionId}>
                  <div>
                    <strong>
                      {index + 1}. {answer.questionText}
                    </strong>
                    <p>내가 고른 답: {answer.selectedChoiceText}</p>
                    <p>정답: {answer.correctChoiceText}</p>
                    <p>{answer.explanation}</p>
                  </div>
                  <span className={answer.correct ? 'result-badge correct' : 'result-badge wrong'}>
                    {answer.correct ? '정답' : '오답'}
                  </span>
                </article>
              ))}
            </div>
          </section>
        )}

        {screen === 'ranking' && (
          <section className="page-section">
            <div className="section-heading">
              <p className="eyebrow">랭킹</p>
              <h1>카테고리별 점수를 비교해보세요.</h1>
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
