package com.trickyquiz.backend.api.category;

import com.trickyquiz.backend.api.category.dto.CategoryResponse;
import com.trickyquiz.backend.domain.quiz.QuizCategory;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

    /**
     * 퀴즈에서 사용할 카테고리 목록을 반환합니다.
     */
    @GetMapping("/api/categories")
    public List<CategoryResponse> categories() {
        return List.of(
                category(QuizCategory.GENERAL, "상식", "가볍게 풀 수 있는 일반 상식 문제"),
                category(QuizCategory.IT, "IT", "개발, 컴퓨터, 인터넷 관련 문제"),
                category(QuizCategory.SCIENCE, "과학", "생활 속 과학과 기초 과학 문제"),
                category(QuizCategory.LIFE, "생활", "일상 상황과 생활 정보 문제")
        );
    }

    private CategoryResponse category(QuizCategory category, String name, String description) {
        return new CategoryResponse(category.name(), name, description);
    }
}
