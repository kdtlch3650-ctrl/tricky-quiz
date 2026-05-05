package com.trickyquiz.backend.domain.quiz;

import com.trickyquiz.backend.api.quiz.dto.QuizSubmitAnswerRequest;
import com.trickyquiz.backend.api.quiz.dto.QuizSubmitAnswerResponse;
import com.trickyquiz.backend.api.quiz.dto.QuizSubmitRequest;
import com.trickyquiz.backend.api.quiz.dto.QuizSubmitResponse;
import com.trickyquiz.backend.domain.user.AuthProvider;
import com.trickyquiz.backend.domain.user.User;
import com.trickyquiz.backend.domain.user.UserRepository;
import com.trickyquiz.backend.api.quiz.dto.QuizChoiceResponse;
import com.trickyquiz.backend.api.quiz.dto.QuizQuestionResponse;
import com.trickyquiz.backend.api.quiz.dto.QuizQuestionsResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService {

    private static final int QUIZ_QUESTION_COUNT = 10;

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizChoiceRepository quizChoiceRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final UserRepository userRepository;

    public QuizService(
            QuizQuestionRepository quizQuestionRepository,
            QuizChoiceRepository quizChoiceRepository,
            QuizResultRepository quizResultRepository,
            QuizAnswerRepository quizAnswerRepository,
            UserRepository userRepository
    ) {
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizChoiceRepository = quizChoiceRepository;
        this.quizResultRepository = quizResultRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.userRepository = userRepository;
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

    /**
     * 사용자가 제출한 답안을 채점하고 결과와 답안 내역을 저장합니다.
     */
    @Transactional
    public QuizSubmitResponse submitResult(String principalName, QuizSubmitRequest request) {
        QuizCategory category = parseCategory(request.category());
        User user = resolveUser(principalName);
        List<QuizSubmitAnswerRequest> submittedAnswers = validateSubmission(request.answers());

        List<ResolvedSubmission> resolvedSubmissions = new ArrayList<>();
        int score = 0;
        for (QuizSubmitAnswerRequest submittedAnswer : submittedAnswers) {
            QuizQuestion question = loadQuestion(category, submittedAnswer.questionId());
            QuizChoice selectedChoice = loadSelectedChoice(question.getId(), submittedAnswer.selectedChoiceId());
            QuizChoice correctChoice = loadCorrectChoice(question.getId());
            boolean correct = selectedChoice.isCorrect();

            if (correct) {
                score++;
            }

            resolvedSubmissions.add(new ResolvedSubmission(question, selectedChoice, correctChoice, correct));
        }

        QuizResult result = quizResultRepository.save(new QuizResult(
                user,
                category,
                score,
                resolvedSubmissions.size(),
                request.elapsedSeconds()
        ));

        List<QuizAnswer> quizAnswers = resolvedSubmissions.stream()
                .map(submission -> new QuizAnswer(
                        result,
                        submission.question(),
                        submission.selectedChoice(),
                        submission.correct()
                ))
                .toList();
        quizAnswerRepository.saveAll(quizAnswers);

        List<QuizSubmitAnswerResponse> answerResponses = resolvedSubmissions.stream()
                .map(submission -> new QuizSubmitAnswerResponse(
                        submission.question().getId(),
                        submission.question().getQuestionText(),
                        submission.selectedChoice().getId(),
                        submission.selectedChoice().getChoiceText(),
                        submission.correctChoice().getId(),
                        submission.correctChoice().getChoiceText(),
                        submission.correct(),
                        submission.question().getExplanation()
                ))
                .toList();

        return new QuizSubmitResponse(
                result.getId(),
                category.name(),
                score,
                resolvedSubmissions.size(),
                request.elapsedSeconds(),
                answerResponses
        );
    }

    private QuizCategory parseCategory(String categoryCode) {
        try {
            return QuizCategory.valueOf(categoryCode);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("지원하지 않는 카테고리입니다.");
        }
    }

    private User resolveUser(String principalName) {
        Objects.requireNonNull(principalName, "principalName");

        return userRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, principalName)
                .orElseGet(() -> userRepository.save(new User(
                        AuthProvider.GOOGLE,
                        principalName,
                        principalName + "@local.test",
                        principalName
                )));
    }

    private List<QuizSubmitAnswerRequest> validateSubmission(List<QuizSubmitAnswerRequest> submittedAnswers) {
        if (submittedAnswers.size() != QUIZ_QUESTION_COUNT) {
            throw new IllegalArgumentException("답안 개수는 10개여야 합니다.");
        }

        List<Long> questionIds = submittedAnswers.stream()
                .map(QuizSubmitAnswerRequest::questionId)
                .toList();
        if (new LinkedHashSet<>(questionIds).size() != questionIds.size()) {
            throw new IllegalArgumentException("중복된 문제 답안은 제출할 수 없습니다.");
        }

        return submittedAnswers;
    }

    private QuizQuestion loadQuestion(QuizCategory category, Long questionId) {
        return quizQuestionRepository.findById(questionId)
                .filter(question -> question.getCategory() == category && question.isActive())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "문제를 찾을 수 없습니다."
                ));
    }

    private QuizChoice loadSelectedChoice(Long questionId, Long selectedChoiceId) {
        return quizChoiceRepository.findById(selectedChoiceId)
                .filter(choice -> choice.getQuestion().getId().equals(questionId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "선택지를 찾을 수 없습니다."
                ));
    }

    private QuizChoice loadCorrectChoice(Long questionId) {
        return quizChoiceRepository.findByQuestionIdOrderByChoiceOrderAsc(questionId).stream()
                .filter(QuizChoice::isCorrect)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("정답 선택지를 찾을 수 없습니다."));
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

    private record ResolvedSubmission(
            QuizQuestion question,
            QuizChoice selectedChoice,
            QuizChoice correctChoice,
            boolean correct
    ) {
    }
}
