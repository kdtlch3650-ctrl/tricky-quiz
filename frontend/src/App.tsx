import { useEffect, useRef, useState } from 'react'
import {
  ApiError,
  getCategories,
  getCurrentUser,
  getQuizQuestions,
  getRankings,
  logoutCurrentUser,
  submitQuizResult,
  type CategoryCode,
  type CategoryItem,
  type CurrentUser,
  type QuizQuestion,
  type QuizSubmitRequest,
  type QuizSubmitResponse,
  type RankingEntry,
} from './api'
import {
  DEMO_CURRENT_USER,
  getDemoQuizQuestions,
  getDemoRankings,
  submitDemoQuizResult,
} from './demoData'
import './App.css'

type Screen = 'home' | 'login' | 'categories' | 'quiz' | 'result' | 'ranking'
type Category = CategoryItem

const backendOrigin = import.meta.env.VITE_BACKEND_ORIGIN ?? 'http://localhost:8080'
const demoModeForced = import.meta.env.VITE_DEMO_MODE === 'true'

const fallbackCategories: Category[] = [
  {
    code: 'GENERAL',
    name: '상식',
    description: '헷갈리게 만든 일반 상식 문제',
  },
  {
    code: 'IT',
    name: 'IT',
    description: '개발, 컴퓨터, 인터넷 함정 문제',
  },
  {
    code: 'SCIENCE',
    name: '과학',
    description: '생활 속 과학과 기초 과학 함정 문제',
  },
  {
    code: 'LIFE',
    name: '생활',
    description: '일상 상황과 생활 정보 함정 문제',
  },
]

function calculateElapsedSeconds(startedAt: number) {
  return Math.max(1, Math.round((Date.now() - startedAt) / 1000))
}

function App() {
  const [screen, setScreen] = useState<Screen>('home')
  const [loggedIn, setLoggedIn] = useState(false)
  const [demoMode, setDemoMode] = useState(demoModeForced)
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
  const [logoutMenuOpen, setLogoutMenuOpen] = useState(false)
  const userMenuRef = useRef<HTMLDivElement | null>(null)

  const currentCategory = categories.find((category) => category.code === selectedCategory)
  const currentQuestion = quizQuestions[questionIndex]
  const resultCategory = categories.find((category) => category.code === quizResult?.category)
  const displayCategory = resultCategory ?? currentCategory
  const visibleRankingEntries = demoMode
    ? getDemoRankings(selectedCategory).rankings
    : rankingEntries
  const visibleRankingLoading = demoMode ? false : rankingLoading
  const visibleRankingError = demoMode ? '' : rankingError

  const enterDemoSession = () => {
    setLoggedIn(true)
    setCurrentUser(DEMO_CURRENT_USER)
    setLogoutMenuOpen(false)
    setNotice('데모 모드로 실행 중입니다. 백엔드가 연결되면 실제 API를 사용합니다.')
  }

  const goQuizStart = () => {
    if (!loggedIn) {
      if (demoMode) {
        enterDemoSession()
        setQuizError('')
        setScreen('categories')
        return
      }

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
      const response = demoMode
        ? getDemoQuizQuestions(selectedCategory)
        : await getQuizQuestions(selectedCategory)
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
      elapsedSeconds: calculateElapsedSeconds(quizStartedAt),
      answers: quizQuestions.map((question) => ({
        questionId: question.id,
        selectedChoiceId: answers[question.id]!,
      })),
    }

    setQuizLoading(true)
    setNotice('')

    try {
      const response = demoMode
        ? submitDemoQuizResult(payload)
        : await submitQuizResult(payload)
      setQuizResult(response)
      setScreen('result')
    } catch (error) {
      setNotice(error instanceof Error ? error.message : '퀴즈 결과를 제출하지 못했습니다.')
    } finally {
      setQuizLoading(false)
    }
  }

  const handleChoiceSelect = (choiceId: number) => {
    if (!currentQuestion) {
      return
    }

    const selectedChoiceId = answers[currentQuestion.id]

    if (selectedChoiceId === choiceId) {
      if (questionIndex === quizQuestions.length - 1) {
        void submitQuiz()
        return
      }

      setNotice('')
      setQuestionIndex((value) => value + 1)
      return
    }

    setAnswers((prev) => ({
      ...prev,
      [currentQuestion.id]: choiceId,
    }))
  }

  const handleLogin = () => {
    if (demoMode) {
      enterDemoSession()
      setQuizError('')
      setScreen('categories')
      return
    }

    window.location.assign(`${backendOrigin}/oauth2/authorization/google`)
  }

  const handleLogout = async () => {
    if (!loggedIn) {
      return
    }

    if (demoMode) {
      setLoggedIn(false)
      setCurrentUser(null)
      setScreen('home')
      setNotice('')
      setQuizError('')
      setRankingError('')
      setQuizQuestions([])
      setQuestionIndex(0)
      setAnswers({})
      setQuizStartedAt(null)
      setQuizResult(null)
      setRankingEntries([])
      setLogoutMenuOpen(false)
      return
    }

    try {
      await logoutCurrentUser()
      setLoggedIn(false)
      setCurrentUser(null)
      setScreen('home')
      setNotice('')
      setQuizError('')
      setRankingError('')
      setQuizQuestions([])
      setQuestionIndex(0)
      setAnswers({})
      setQuizStartedAt(null)
      setQuizResult(null)
      setRankingEntries([])
      setLogoutMenuOpen(false)
    } catch (error) {
      setNotice(error instanceof Error ? error.message : '로그아웃에 실패했습니다.')
    }
  }

  useEffect(() => {
    if (demoModeForced) {
      return
    }

    let cancelled = false

    const loadCategories = async () => {
      try {
        const response = await getCategories()
        if (!cancelled) {
          setCategories(response)
        }
      } catch (error) {
        if (!cancelled) {
          setCategories(fallbackCategories)
          if (!(error instanceof ApiError && error.status === 401)) {
            setDemoMode(true)
          }
        }
      }
    }

    void loadCategories()

    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    const handlePointerDown = (event: PointerEvent) => {
      if (!logoutMenuOpen) {
        return
      }

      const target = event.target
      if (!(target instanceof Node)) {
        return
      }

      if (userMenuRef.current && !userMenuRef.current.contains(target)) {
        setLogoutMenuOpen(false)
      }
    }

    document.addEventListener('pointerdown', handlePointerDown)
    return () => {
      document.removeEventListener('pointerdown', handlePointerDown)
    }
  }, [logoutMenuOpen])

  useEffect(() => {
    if (demoModeForced) {
      return
    }

    let cancelled = false

    const loadCurrentUser = async () => {
      try {
        const user = await getCurrentUser()
        if (!cancelled) {
          setCurrentUser(user)
          setLoggedIn(true)
        }
      } catch (error) {
        if (!cancelled) {
          if (error instanceof ApiError && error.status === 401) {
            setCurrentUser(null)
            setLoggedIn(false)
            return
          }

          setDemoMode(true)
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

    if (demoMode) {
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
          setDemoMode(true)
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
  }, [screen, selectedCategory, demoMode])

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
            <div className="user-menu" ref={userMenuRef}>
              <button
                className="user-chip"
                type="button"
                aria-expanded={logoutMenuOpen}
                aria-haspopup="menu"
                onClick={() => setLogoutMenuOpen((value) => !value)}
              >
                {currentUser?.nickname ?? '사용자'}
              </button>
              {logoutMenuOpen && (
                <div className="user-menu-panel" role="menu" aria-label="계정 메뉴">
                  <button type="button" onClick={handleLogout}>
                    로그아웃
                  </button>
                </div>
              )}
            </div>
          ) : (
            <button
              type="button"
              onClick={demoMode ? handleLogin : () => setScreen('login')}
            >
              {demoMode ? '데모 시작' : '로그인'}
            </button>
          )}
        </nav>
      </header>
      {demoMode && (
        <div className="demo-banner" role="status">
          데모 모드로 실행 중입니다. 백엔드가 연결되면 실제 API로 자동 전환됩니다.
        </div>
      )}

      <main>
        {screen === 'home' && (
          <section className="hero-section">
            <div className="hero-copy">
              <p className="eyebrow">10문제 함정 퀴즈</p>
              <h1>트릭퀴즈는 보기에서 한 번 더 헷갈리게 만듭니다.</h1>
              <p className="lead">함정 같은 문제로 가볍게 즐기는 퀴즈</p>
              <div className="button-row">
                <button className="primary-button" type="button" onClick={goQuizStart}>
                  퀴즈 시작
                </button>
                <button className="secondary-button" type="button" onClick={() => setScreen('ranking')}>
                  랭킹 보기
                </button>
              </div>
            </div>
          </section>
        )}

        {screen === 'login' && (
          <section className="center-section">
            <div className="narrow-panel">
              <p className="eyebrow">{demoMode ? '데모 모드' : '로그인 필요'}</p>
              <h1>
                {demoMode
                  ? '백엔드 없이도 샘플 데이터로 바로 둘러볼 수 있습니다.'
                  : '퀴즈 결과 저장과 랭킹 반영을 위해 로그인이 필요합니다.'}
              </h1>
              <p className="lead">
                {demoMode
                  ? 'GitHub Pages처럼 정적 호스팅만 가능한 환경에서는 데모 모드로 전환됩니다.'
                  : 'Google OAuth 로그인으로 이동하고, 로그인 후 사용자 정보를 상단에 표시합니다.'}
              </p>
              <button className="google-button" type="button" onClick={handleLogin}>
                {demoMode ? '데모 퀴즈로 시작' : 'Google 계정으로 시작'}
              </button>
            </div>
          </section>
        )}

        {screen === 'categories' && (
          <section className="page-section">
            <div className="section-heading">
              <p className="eyebrow">카테고리 선택</p>
              <h1>오늘은 어떤 함정부터 풀어볼까요?</h1>
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
                {quizLoading ? '불러오는 중...' : `${currentCategory?.name ?? selectedCategory} 함정 퀴즈 시작`}
              </button>
            </div>
          </section>
        )}

        {screen === 'quiz' && (
          <section className="page-section quiz-layout">
            <div className="quiz-header">
              <div>
                <p className="eyebrow">{currentCategory?.name ?? selectedCategory} 함정 퀴즈</p>
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
                <p className="question-hint">O 또는 X로 판단하세요.</p>
                <div className="choice-list">
                  {currentQuestion.choices.map((choice) => (
                    <button
                      className={
                        answers[currentQuestion.id] === choice.id
                          ? 'choice-button selected'
                          : 'choice-button'
                      }
                      key={choice.id}
                      type="button"
                      onClick={() => handleChoiceSelect(choice.id)}
                    >
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
              <span className="progress-text">
                같은 선택지를 한 번 더 누르면 다음 문제로 넘어갑니다.
              </span>
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
                {displayCategory?.name ?? quizResult.category} 함정 퀴즈를 {quizResult.elapsedSeconds}
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
              <h1>카테고리별 함정 점수를 비교해보세요.</h1>
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
            {visibleRankingLoading && <p className="notice">랭킹을 불러오는 중입니다.</p>}
            {visibleRankingError && <p className="notice">{visibleRankingError}</p>}
            <div className="ranking-table" role="table" aria-label="랭킹 목록">
              <div className="ranking-row heading" role="row">
                <span>순위</span>
                <span>닉네임</span>
                <span>점수</span>
                <span>시간</span>
                <span>일시</span>
              </div>
              {!visibleRankingLoading && visibleRankingEntries.length === 0 && !visibleRankingError ? (
                <div className="ranking-row empty" role="row">
                  <span>아직 기록이 없습니다.</span>
                </div>
              ) : null}
              {visibleRankingEntries.map((ranking) => (
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
