package com.trickyquiz.backend.api.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 퀴즈 결과 제출 요청입니다.
 */
public record QuizSubmitRequest(
        @NotBlank String category,
        @NotNull Integer elapsedSeconds,
        @NotEmpty List<@Valid QuizSubmitAnswerRequest> answers
) {
}
