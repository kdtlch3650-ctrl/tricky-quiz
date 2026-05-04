package com.trickyquiz.backend.api.quiz.dto;

/**
 * 퀴즈 풀이 화면에 보여줄 선택지 응답입니다.
 *
 * 정답 여부는 클라이언트에 보내지 않습니다.
 * 정답 검증은 퀴즈 제출 API에서 서버가 처리합니다.
 */
public record QuizChoiceResponse(
        Long id,
        String text
) {
}
