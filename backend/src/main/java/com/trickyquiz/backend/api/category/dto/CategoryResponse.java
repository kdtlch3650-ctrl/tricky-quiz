package com.trickyquiz.backend.api.category.dto;

/**
 * 카테고리 목록 응답입니다.
 */
public record CategoryResponse(
        String code,
        String name,
        String description
) {
}
