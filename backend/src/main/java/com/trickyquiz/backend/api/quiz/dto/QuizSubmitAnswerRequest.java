package com.trickyquiz.backend.api.quiz.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 제출 요청의 문제별 답안입니다.
 */
public record QuizSubmitAnswerRequest(
        @NotNull Long questionId,
        @NotNull Long selectedChoiceId
) {
}
