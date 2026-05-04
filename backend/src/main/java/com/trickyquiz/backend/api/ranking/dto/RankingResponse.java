package com.trickyquiz.backend.api.ranking.dto;

import java.util.List;

/**
 * 카테고리별 랭킹 조회 응답입니다.
 */
public record RankingResponse(
        String category,
        List<RankingEntryResponse> rankings
) {
}
