package com.trickyquiz.backend.domain.quiz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.trickyquiz.backend.api.quiz.dto.QuizSubmitAnswerRequest;
import com.trickyquiz.backend.api.quiz.dto.QuizSubmitRequest;
import com.trickyquiz.backend.api.quiz.dto.QuizSubmitResponse;
import com.trickyquiz.backend.domain.user.AuthProvider;
import com.trickyquiz.backend.domain.user.User;
import com.trickyquiz.backend.domain.user.UserRepository;
import com.trickyquiz.backend.api.quiz.dto.QuizQuestionsResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizChoiceRepository quizChoiceRepository;

    @Mock
    private QuizResultRepository quizResultRepository;

    @Mock
    private QuizAnswerRepository quizAnswerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    void getQuestionsReturnsTenQuestionsWithChoices() {
        List<QuizQuestion> questions = LongStream.rangeClosed(1, 10)
                .mapToObj(this::question)
                .toList();

        when(quizQuestionRepository.findByCategoryAndActiveTrue(eq(QuizCategory.GENERAL)))
                .thenReturn(questions);
        QuizQuestion sampleQuestion = question(100L);
        when(quizChoiceRepository.findByQuestionIdOrderByChoiceOrderAsc(anyLong()))
                .thenReturn(List.of(
                        choice(sampleQuestion, 1L, "보기 A", false),
                        choice(sampleQuestion, 2L, "보기 B", true)
                ));

        QuizQuestionsResponse response = quizService.getQuestions("GENERAL");

        assertThat(response.category()).isEqualTo("GENERAL");
        assertThat(response.totalCount()).isEqualTo(10);
        assertThat(response.questions()).hasSize(10);
        assertThat(response.questions().getFirst().choices())
                .extracting("text")
                .containsExactly("보기 A", "보기 B");
    }

    @Test
    void getQuestionsRejectsUnknownCategory() {
        assertThatThrownBy(() -> quizService.getQuestions("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 카테고리입니다.");
    }

    @Test
    void getQuestionsRejectsWhenQuestionCountIsNotEnough() {
        when(quizQuestionRepository.findByCategoryAndActiveTrue(eq(QuizCategory.GENERAL)))
                .thenReturn(List.of(question(1L)));

        assertThatThrownBy(() -> quizService.getQuestions("GENERAL"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출제 가능한 문제가 부족합니다.");
    }

    @Test
    void submitResultSavesResultAndAnswers() {
        User user = user(1L);
        when(userRepository.findByProviderAndProviderUserId(eq(AuthProvider.GOOGLE), eq("tester")))
                .thenReturn(Optional.of(user));

        List<QuizQuestion> questions = LongStream.rangeClosed(1, 10)
                .mapToObj(this::question)
                .toList();
        when(quizQuestionRepository.findById(anyLong()))
                .thenAnswer(invocation -> {
                    Long questionId = invocation.getArgument(0, Long.class);
                    return questions.stream()
                            .filter(question -> question.getId().equals(questionId))
                            .findFirst();
                });
        when(quizChoiceRepository.findById(anyLong()))
                .thenAnswer(invocation -> {
                    Long choiceId = invocation.getArgument(0, Long.class);
                    if (choiceId % 2 == 0) {
                        QuizQuestion question = question(choiceId / 10);
                        ReflectionTestUtils.setField(question, "id", choiceId / 10);
                        return Optional.of(choice(question, choiceId, "정답 " + choiceId, true));
                    }
                    QuizQuestion question = question(choiceId / 10);
                    ReflectionTestUtils.setField(question, "id", choiceId / 10);
                    return Optional.of(choice(question, choiceId, "오답 " + choiceId, false));
                });
        when(quizChoiceRepository.findByQuestionIdOrderByChoiceOrderAsc(anyLong()))
                .thenAnswer(invocation -> {
                    Long questionId = invocation.getArgument(0, Long.class);
                    QuizQuestion question = question(questionId);
                    ReflectionTestUtils.setField(question, "id", questionId);
                    return List.of(
                            choice(question, questionId * 10 + 1, "오답", false),
                            choice(question, questionId * 10 + 2, "정답", true)
                    );
                });
        when(quizResultRepository.save(org.mockito.ArgumentMatchers.any(QuizResult.class)))
                .thenAnswer(invocation -> {
                    QuizResult result = invocation.getArgument(0, QuizResult.class);
                    ReflectionTestUtils.setField(result, "id", 99L);
                    return result;
                });
        when(quizAnswerRepository.saveAll(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0, List.class));

        List<QuizSubmitAnswerRequest> answers = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            answers.add(new QuizSubmitAnswerRequest(i, i * 10 + 2));
        }

        QuizSubmitResponse response = quizService.submitResult(
                "tester",
                new QuizSubmitRequest("GENERAL", 82, answers)
        );

        assertThat(response.resultId()).isEqualTo(99L);
        assertThat(response.category()).isEqualTo("GENERAL");
        assertThat(response.score()).isEqualTo(10);
        assertThat(response.totalCount()).isEqualTo(10);
        assertThat(response.elapsedSeconds()).isEqualTo(82);
        assertThat(response.answers()).hasSize(10);
        assertThat(response.answers().getFirst().correct()).isTrue();
        assertThat(response.answers().getFirst().correctChoiceText()).isEqualTo("정답");
    }

    @Test
    void submitResultRejectsInvalidAnswerCount() {
        when(userRepository.findByProviderAndProviderUserId(eq(AuthProvider.GOOGLE), eq("tester")))
                .thenReturn(Optional.of(user(1L)));

        assertThatThrownBy(() -> quizService.submitResult(
                "tester",
                new QuizSubmitRequest("GENERAL", 82, List.of(new QuizSubmitAnswerRequest(1L, 2L)))
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("답안 개수는 10개여야 합니다.");
    }

    @Test
    void submitResultReturns404WhenQuestionDoesNotExist() {
        when(userRepository.findByProviderAndProviderUserId(eq(AuthProvider.GOOGLE), eq("tester")))
                .thenReturn(Optional.of(user(1L)));
        when(quizQuestionRepository.findById(anyLong())).thenReturn(Optional.empty());

        List<QuizSubmitAnswerRequest> answers = LongStream.rangeClosed(1, 10)
                .mapToObj(i -> new QuizSubmitAnswerRequest(i, i * 10 + 2))
                .toList();

        assertThatThrownBy(() -> quizService.submitResult(
                "tester",
                new QuizSubmitRequest("GENERAL", 82, answers)
        ))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    private QuizQuestion question(Long id) {
        QuizQuestion question = new QuizQuestion(
                QuizCategory.GENERAL,
                "문제 " + id,
                "해설 " + id,
                true
        );
        ReflectionTestUtils.setField(question, "id", id);
        return question;
    }

    private QuizChoice choice(QuizQuestion question, Long id, String text, boolean correct) {
        QuizChoice choice = new QuizChoice(question, id.intValue(), text, correct);
        ReflectionTestUtils.setField(choice, "id", id);
        return choice;
    }

    private User user(Long id) {
        User user = new User(AuthProvider.GOOGLE, "tester", "tester@local.test", "tester");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
