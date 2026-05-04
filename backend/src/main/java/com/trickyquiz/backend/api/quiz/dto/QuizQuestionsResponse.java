package com.trickyquiz.backend.api.quiz.dto;

import java.util.List;

/**
 * 퀴즈 시작 시 내려주는 전체 문제 목록 응답입니다.
 */
public record QuizQuestionsResponse(
        String category,
        int totalCount,
        List<QuizQuestionResponse> questions
) {
}
