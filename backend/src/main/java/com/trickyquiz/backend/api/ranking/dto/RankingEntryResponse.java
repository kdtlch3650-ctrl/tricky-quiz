package com.trickyquiz.backend.api.ranking.dto;

import java.time.LocalDateTime;

/**
 * 랭킹 목록의 한 줄 응답입니다.
 */
public record RankingEntryResponse(
        int rank,
        String nickname,
        int score,
        int totalCount,
        int elapsedSeconds,
        LocalDateTime playedAt
) {
}
