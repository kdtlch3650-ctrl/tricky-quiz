package com.trickyquiz.backend.domain.quiz;

import com.trickyquiz.backend.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
// 사용자가 퀴즈를 한 번 제출했을 때의 전체 결과를 저장합니다.
@Table(
        name = "quiz_results",
        indexes = {
                // 랭킹 화면은 점수 높은 순, 시간 짧은 순, 먼저 제출한 순으로 조회합니다.
                @Index(
                        name = "idx_quiz_results_ranking",
                        columnList = "category, score DESC, elapsed_seconds ASC, played_at ASC"
                ),
                @Index(name = "idx_quiz_results_user", columnList = "user_id, played_at DESC")
        }
)
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 한 사용자는 여러 번 퀴즈를 플레이할 수 있으므로 N:1 관계로 연결합니다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuizCategory category;

    @Column(nullable = false)
    private int score;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "elapsed_seconds", nullable = false)
    private int elapsedSeconds;

    @CreationTimestamp
    @Column(name = "played_at", nullable = false, updatable = false)
    private LocalDateTime playedAt;

    protected QuizResult() {
    }

    public QuizResult(User user, QuizCategory category, int score, int totalCount, int elapsedSeconds) {
        this.user = user;
        this.category = category;
        this.score = score;
        this.totalCount = totalCount;
        this.elapsedSeconds = elapsedSeconds;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public QuizCategory getCategory() {
        return category;
    }

    public int getScore() {
        return score;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }
}
