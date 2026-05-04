package com.trickyquiz.backend.api.ranking;

import com.trickyquiz.backend.api.ranking.dto.RankingResponse;
import com.trickyquiz.backend.domain.quiz.RankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    /**
     * 카테고리별 랭킹 목록을 조회합니다.
     */
    @GetMapping("/api/rankings")
    public RankingResponse getRankings(@RequestParam String category) {
        return rankingService.getRankings(category);
    }
}
