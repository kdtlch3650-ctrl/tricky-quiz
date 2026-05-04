package com.trickyquiz.backend.api.ranking;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.trickyquiz.backend.api.ranking.dto.RankingEntryResponse;
import com.trickyquiz.backend.api.ranking.dto.RankingResponse;
import com.trickyquiz.backend.common.config.SecurityConfig;
import com.trickyquiz.backend.domain.quiz.RankingService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RankingController.class)
@Import(SecurityConfig.class)
class RankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingService rankingService;

    @Test
    void getRankingsReturnsRankingList() throws Exception {
        RankingResponse response = new RankingResponse(
                "GENERAL",
                List.of(
                        new RankingEntryResponse(1, "사용자A", 10, 10, 61, LocalDateTime.parse("2026-05-03T20:00:00")),
                        new RankingEntryResponse(2, "사용자B", 9, 10, 55, LocalDateTime.parse("2026-05-03T20:05:00"))
                )
        );

        when(rankingService.getRankings(eq("GENERAL"))).thenReturn(response);

        mockMvc.perform(get("/api/rankings")
                        .param("category", "GENERAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("GENERAL"))
                .andExpect(jsonPath("$.rankings[0].rank").value(1))
                .andExpect(jsonPath("$.rankings[0].nickname").value("사용자A"))
                .andExpect(jsonPath("$.rankings[0].score").value(10))
                .andExpect(jsonPath("$.rankings[0].playedAt").value("2026-05-03T20:00:00"));
    }
}
