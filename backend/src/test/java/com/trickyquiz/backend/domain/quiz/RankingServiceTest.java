package com.trickyquiz.backend.domain.quiz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.trickyquiz.backend.domain.user.AuthProvider;
import com.trickyquiz.backend.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private QuizResultRepository quizResultRepository;

    @InjectMocks
    private RankingService rankingService;

    @Test
    void getRankingsReturnsTop10InOrder() {
        QuizResult first = result(1L, "사용자A", 10, 10, 61, "2026-05-03T20:00:00");
        QuizResult second = result(2L, "사용자B", 9, 10, 55, "2026-05-03T20:05:00");

        when(quizResultRepository.findTop10ByCategoryOrderByScoreDescElapsedSecondsAscPlayedAtAsc(eq(QuizCategory.GENERAL)))
                .thenReturn(List.of(first, second));

        var response = rankingService.getRankings("GENERAL");

        assertThat(response.category()).isEqualTo("GENERAL");
        assertThat(response.rankings()).hasSize(2);
        assertThat(response.rankings().get(0).rank()).isEqualTo(1);
        assertThat(response.rankings().get(0).nickname()).isEqualTo("사용자A");
        assertThat(response.rankings().get(1).rank()).isEqualTo(2);
        assertThat(response.rankings().get(1).nickname()).isEqualTo("사용자B");
    }

    @Test
    void getRankingsRejectsUnknownCategory() {
        assertThatThrownBy(() -> rankingService.getRankings("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 카테고리입니다.");
    }

    private QuizResult result(Long id, String nickname, int score, int totalCount, int elapsedSeconds, String playedAt) {
        User user = new User(AuthProvider.GOOGLE, "provider-user-" + id, nickname + "@example.com", nickname);
        ReflectionTestUtils.setField(user, "id", id);

        QuizResult result = new QuizResult(user, QuizCategory.GENERAL, score, totalCount, elapsedSeconds);
        ReflectionTestUtils.setField(result, "id", id);
        ReflectionTestUtils.setField(result, "playedAt", LocalDateTime.parse(playedAt));
        return result;
    }
}
