package com.trickyquiz.backend.domain.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import com.trickyquiz.backend.domain.user.AuthProvider;
import com.trickyquiz.backend.domain.user.User;
import com.trickyquiz.backend.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
// 실제 PostgreSQL과 Flyway 마이그레이션을 사용해 엔티티-테이블 매핑을 검증합니다.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuizRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizChoiceRepository quizChoiceRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Test
    void savesQuizResultWithAnswer() {
        User user = userRepository.save(new User(
                AuthProvider.GOOGLE,
                "google-user-1",
                "user1@example.com",
                "사용자1"
        ));

        QuizQuestion question = quizQuestionRepository.save(new QuizQuestion(
                QuizCategory.GENERAL,
                "세계에서 가장 넓은 바다는 무엇일까요?",
                "태평양은 지구에서 가장 넓은 바다입니다.",
                true
        ));

        QuizChoice wrongChoice = quizChoiceRepository.save(new QuizChoice(question, 1, "대서양", false));
        QuizChoice correctChoice = quizChoiceRepository.save(new QuizChoice(question, 2, "태평양", true));

        QuizResult result = quizResultRepository.save(new QuizResult(
                user,
                QuizCategory.GENERAL,
                1,
                1,
                15
        ));

        QuizAnswer answer = quizAnswerRepository.save(new QuizAnswer(
                result,
                question,
                correctChoice,
                true
        ));

        assertThat(user.getId()).isNotNull();
        assertThat(question.getId()).isNotNull();
        assertThat(wrongChoice.getId()).isNotNull();
        assertThat(correctChoice.getId()).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(answer.getId()).isNotNull();
        assertThat(quizQuestionRepository.findByCategoryAndActiveTrue(QuizCategory.GENERAL)).hasSize(21);
        assertThat(quizChoiceRepository.findByQuestionIdOrderByChoiceOrderAsc(question.getId()))
                .extracting(QuizChoice::getChoiceText)
                .containsExactly("대서양", "태평양");
        assertThat(quizResultRepository.findTop10ByCategoryOrderByScoreDescElapsedSecondsAscPlayedAtAsc(QuizCategory.GENERAL))
                .hasSize(1);
        assertThat(quizAnswerRepository.findByResultId(result.getId())).hasSize(1);
    }
}
