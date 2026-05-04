package com.trickyquiz.backend.domain.quiz;

import com.trickyquiz.backend.api.quiz.dto.QuizChoiceResponse;
import com.trickyquiz.backend.api.quiz.dto.QuizQuestionResponse;
import com.trickyquiz.backend.api.quiz.dto.QuizQuestionsResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService {

    private static final int QUIZ_QUESTION_COUNT = 10;

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizChoiceRepository quizChoiceRepository;

    public QuizService(
            QuizQuestionRepository quizQuestionRepository,
            QuizChoiceRepository quizChoiceRepository
    ) {
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizChoiceRepository = quizChoiceRepository;
    }

    /**
     * 선택한 카테고리에서 출제 가능한 문제 10개와 각 문제의 선택지를 조회합니다.
     *
     * Entity에는 정답 여부가 들어 있지만, 응답 DTO에는 정답 여부를 담지 않습니다.
     */
    @Transactional(readOnly = true)
    public QuizQuestionsResponse getQuestions(String categoryCode) {
        QuizCategory category = parseCategory(categoryCode);
        List<QuizQuestion> selectedQuestions = selectQuestions(category);

        List<QuizQuestionResponse> questionResponses = selectedQuestions.stream()
                .map(this::toQuestionResponse)
                .toList();

        return new QuizQuestionsResponse(
                category.name(),
                questionResponses.size(),
                questionResponses
        );
    }

    private QuizCategory parseCategory(String categoryCode) {
        try {
            return QuizCategory.valueOf(categoryCode);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("지원하지 않는 카테고리입니다.");
        }
    }

    private List<QuizQuestion> selectQuestions(QuizCategory category) {
        List<QuizQuestion> questions = new ArrayList<>(
                quizQuestionRepository.findByCategoryAndActiveTrue(category)
        );

        if (questions.size() < QUIZ_QUESTION_COUNT) {
            throw new IllegalArgumentException("출제 가능한 문제가 부족합니다.");
        }

        Collections.shuffle(questions);
        return questions.subList(0, QUIZ_QUESTION_COUNT);
    }

    private QuizQuestionResponse toQuestionResponse(QuizQuestion question) {
        List<QuizChoiceResponse> choices = quizChoiceRepository
                .findByQuestionIdOrderByChoiceOrderAsc(question.getId())
                .stream()
                .map(choice -> new QuizChoiceResponse(choice.getId(), choice.getChoiceText()))
                .toList();

        return new QuizQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                choices
        );
    }
}
