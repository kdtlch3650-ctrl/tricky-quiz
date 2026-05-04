package com.trickyquiz.backend.domain.quiz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.trickyquiz.backend.api.quiz.dto.QuizQuestionsResponse;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizChoiceRepository quizChoiceRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    void getQuestionsReturnsTenQuestionsWithChoices() {
        List<QuizQuestion> questions = LongStream.rangeClosed(1, 10)
                .mapToObj(this::question)
                .toList();

        when(quizQuestionRepository.findByCategoryAndActiveTrue(eq(QuizCategory.GENERAL)))
                .thenReturn(questions);
        when(quizChoiceRepository.findByQuestionIdOrderByChoiceOrderAsc(anyLong()))
                .thenReturn(List.of(
                        choice(1L, "보기 A", false),
                        choice(2L, "보기 B", true)
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

    private QuizChoice choice(Long id, String text, boolean correct) {
        QuizChoice choice = new QuizChoice(question(100L + id), id.intValue(), text, correct);
        ReflectionTestUtils.setField(choice, "id", id);
        return choice;
    }
}
