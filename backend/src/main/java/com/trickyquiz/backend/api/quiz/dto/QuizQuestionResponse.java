package com.trickyquiz.backend.api.quiz.dto;

import java.util.List;

/**
 * 퀴즈 풀이 화면에 보여줄 문제 응답입니다.
 */
public record QuizQuestionResponse(
        Long id,
        String questionText,
        List<QuizChoiceResponse> choices
) {
}
