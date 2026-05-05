package com.trickyquiz.backend.domain.quiz;

import com.trickyquiz.backend.api.ranking.dto.RankingEntryResponse;
import com.trickyquiz.backend.api.ranking.dto.RankingResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RankingService {

    private final QuizResultRepository quizResultRepository;

    public RankingService(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    /**
     * 카테고리별 상위 10개 랭킹을 반환합니다.
     */
    @Transactional(readOnly = true)
    public RankingResponse getRankings(String categoryCode) {
        QuizCategory category = parseCategory(categoryCode);
        List<RankingEntryResponse> rankings = quizResultRepository
                .findTop10ByCategoryOrderByScoreDescElapsedSecondsAscPlayedAtAsc(category)
                .stream()
                .map(result -> new RankingEntryResponse(
                        0,
                        result.getUser().getNickname(),
                        result.getScore(),
                        result.getTotalCount(),
                        result.getElapsedSeconds(),
                        result.getPlayedAt()
                ))
                .toList();

        rankings = updateRanks(rankings);
        return new RankingResponse(category.name(), rankings);
    }

    private QuizCategory parseCategory(String categoryCode) {
        try {
            return QuizCategory.valueOf(categoryCode);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("지원하지 않는 카테고리입니다.");
        }
    }

    private List<RankingEntryResponse> updateRanks(List<RankingEntryResponse> rankings) {
        return java.util.stream.IntStream.range(0, rankings.size())
                .mapToObj(index -> {
                    RankingEntryResponse ranking = rankings.get(index);
                    return new RankingEntryResponse(
                            index + 1,
                            ranking.nickname(),
                            ranking.score(),
                            ranking.totalCount(),
                            ranking.elapsedSeconds(),
                            ranking.playedAt()
                    );
                })
                .toList();
    }
}
