package com.trickyquiz.backend.api.quiz;

import com.trickyquiz.backend.api.quiz.dto.QuizQuestionsResponse;
import com.trickyquiz.backend.api.quiz.dto.QuizSubmitRequest;
import com.trickyquiz.backend.api.quiz.dto.QuizSubmitResponse;
import com.trickyquiz.backend.domain.quiz.QuizService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import java.security.Principal;

@RestController
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * 퀴즈 풀이 시작에 필요한 문제와 선택지를 조회합니다.
     */
    @GetMapping("/api/quiz/questions")
    public QuizQuestionsResponse getQuestions(@RequestParam String category) {
        return quizService.getQuestions(category);
    }

    /**
     * 사용자가 푼 문제와 선택지를 제출하고 채점 결과를 저장합니다.
     */
    @PostMapping("/api/quiz/results")
    public QuizSubmitResponse submitResult(
            Principal principal,
            @Valid @RequestBody QuizSubmitRequest request
    ) {
        return quizService.submitResult(principal.getName(), request);
    }
}
