package com.trickyquiz.backend.domain.quiz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
// 퀴즈에 출제할 문제 본문과 카테고리, 해설 정보를 저장합니다.
@Table(
        name = "quiz_questions",
        // 카테고리별로 출제 가능한 문제만 빠르게 조회하기 위한 인덱스입니다.
        indexes = @Index(name = "idx_quiz_questions_category_active", columnList = "category, active")
)
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuizCategory category;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    // 문제를 삭제하지 않고 출제 여부만 제어하기 위한 값입니다.
    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected QuizQuestion() {
    }

    public QuizQuestion(QuizCategory category, String questionText, String explanation, boolean active) {
        this.category = category;
        this.questionText = questionText;
        this.explanation = explanation;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public QuizCategory getCategory() {
        return category;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getExplanation() {
        return explanation;
    }

    public boolean isActive() {
        return active;
    }
}
