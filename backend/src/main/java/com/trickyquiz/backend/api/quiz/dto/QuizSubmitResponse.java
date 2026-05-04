package com.trickyquiz.backend.api.quiz.dto;

import java.util.List;

/**
 * 퀴즈 제출 결과 응답입니다.
 */
public record QuizSubmitResponse(
        Long resultId,
        String category,
        int score,
        int totalCount,
        int elapsedSeconds,
        List<QuizSubmitAnswerResponse> answers
) {
}
