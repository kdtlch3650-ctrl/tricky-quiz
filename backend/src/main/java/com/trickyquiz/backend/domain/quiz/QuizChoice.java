package com.trickyquiz.backend.domain.quiz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
// 한 문제에 속한 객관식 선택지를 저장합니다.
@Table(
        name = "quiz_choices",
        // 한 문제 안에서 선택지 순서가 중복되지 않도록 제한합니다.
        uniqueConstraints = @UniqueConstraint(
                name = "uk_quiz_choices_question_order",
                columnNames = {"question_id", "choice_order"}
        )
)
public class QuizChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 선택지는 하나의 문제에 속합니다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(name = "choice_order", nullable = false)
    private int choiceOrder;

    @Column(name = "choice_text", nullable = false, columnDefinition = "TEXT")
    private String choiceText;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    protected QuizChoice() {
    }

    public QuizChoice(QuizQuestion question, int choiceOrder, String choiceText, boolean correct) {
        this.question = question;
        this.choiceOrder = choiceOrder;
        this.choiceText = choiceText;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public QuizQuestion getQuestion() {
        return question;
    }

    public int getChoiceOrder() {
        return choiceOrder;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public boolean isCorrect() {
        return correct;
    }
}
