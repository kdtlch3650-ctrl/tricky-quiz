package com.trickyquiz.backend.api.quiz.dto;

/**
 * 퀴즈 제출 응답의 문제별 채점 결과입니다.
 */
public record QuizSubmitAnswerResponse(
        Long questionId,
        String questionText,
        Long selectedChoiceId,
        String selectedChoiceText,
        Long correctChoiceId,
        String correctChoiceText,
        boolean correct,
        String explanation
) {
}
