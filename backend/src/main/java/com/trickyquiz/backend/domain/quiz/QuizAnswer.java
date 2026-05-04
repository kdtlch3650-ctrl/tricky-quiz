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

/**
 * 퀴즈 한 번의 결과 안에서 각 문제별 답안 기록을 저장하는 엔티티입니다.
 *
 * 예를 들어 사용자가 10문제 퀴즈를 제출하면 QuizResult는 1개 생성되고,
 * QuizAnswer는 문제 개수만큼 10개 생성됩니다.
 */
@Entity
@Table(
        name = "quiz_answers",
        // 같은 퀴즈 결과 안에서 같은 문제가 두 번 저장되는 것을 막습니다.
        uniqueConstraints = @UniqueConstraint(
                name = "uk_quiz_answers_result_question",
                columnNames = {"result_id", "question_id"}
        )
)
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 답안 기록이 속한 전체 퀴즈 결과입니다.
    // QuizResult 1개에는 여러 개의 QuizAnswer가 연결됩니다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id", nullable = false)
    private QuizResult result;

    // 사용자가 답한 대상 문제입니다.
    // 나중에 결과 상세 화면에서 "몇 번 문제를 맞혔는지" 보여줄 때 사용합니다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    // 사용자가 실제로 선택한 선택지입니다.
    // 정답 선택지가 아니라, 사용자가 고른 값을 저장합니다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "selected_choice_id", nullable = false)
    private QuizChoice selectedChoice;

    // 제출 시점에 계산된 정답 여부입니다.
    // 선택지가 나중에 수정되더라도 당시 결과를 그대로 보여주기 위해 별도로 저장합니다.
    @Column(nullable = false)
    private boolean correct;

    // JPA가 엔티티를 만들 때 사용하는 기본 생성자입니다.
    // 외부에서 의미 없는 빈 답안 객체를 만들지 못하도록 protected로 둡니다.
    protected QuizAnswer() {
    }

    // 퀴즈 제출 처리 로직에서 답안 기록을 생성할 때 사용하는 생성자입니다.
    public QuizAnswer(QuizResult result, QuizQuestion question, QuizChoice selectedChoice, boolean correct) {
        this.result = result;
        this.question = question;
        this.selectedChoice = selectedChoice;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public QuizResult getResult() {
        return result;
    }

    public QuizQuestion getQuestion() {
        return question;
    }

    public QuizChoice getSelectedChoice() {
        return selectedChoice;
    }

    public boolean isCorrect() {
        return correct;
    }
}
