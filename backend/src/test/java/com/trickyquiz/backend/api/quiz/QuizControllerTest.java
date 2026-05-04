package com.trickyquiz.backend.api.quiz;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.trickyquiz.backend.api.quiz.dto.QuizChoiceResponse;
import com.trickyquiz.backend.api.quiz.dto.QuizQuestionResponse;
import com.trickyquiz.backend.api.quiz.dto.QuizQuestionsResponse;
import com.trickyquiz.backend.common.config.SecurityConfig;
import com.trickyquiz.backend.domain.quiz.QuizService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(QuizController.class)
@Import(SecurityConfig.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    void getQuestionsReturnsQuizQuestionsWithoutCorrectAnswer() throws Exception {
        QuizQuestionsResponse response = new QuizQuestionsResponse(
                "GENERAL",
                1,
                List.of(new QuizQuestionResponse(
                        1L,
                        "다음 중 과일은 무엇일까요?",
                        List.of(
                                new QuizChoiceResponse(10L, "자동차"),
                                new QuizChoiceResponse(11L, "사과")
                        )
                ))
        );

        when(quizService.getQuestions(eq("GENERAL"))).thenReturn(response);

        mockMvc.perform(get("/api/quiz/questions")
                        .param("category", "GENERAL")
                        .with(user("test-user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("GENERAL"))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.questions[0].id").value(1))
                .andExpect(jsonPath("$.questions[0].questionText").value("다음 중 과일은 무엇일까요?"))
                .andExpect(jsonPath("$.questions[0].choices[0].id").value(10))
                .andExpect(jsonPath("$.questions[0].choices[0].text").value("자동차"))
                .andExpect(jsonPath("$.questions[0].choices[0].correct").doesNotExist());
    }

    @Test
    void getQuestionsRequiresLogin() throws Exception {
        mockMvc.perform(get("/api/quiz/questions")
                        .param("category", "GENERAL"))
                .andExpect(status().isUnauthorized());
    }
}
