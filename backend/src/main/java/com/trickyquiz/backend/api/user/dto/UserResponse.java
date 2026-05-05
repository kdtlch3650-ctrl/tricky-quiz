package com.trickyquiz.backend.api.user.dto;

/**
 * 현재 로그인한 사용자의 기본 정보 응답입니다.
 */
public record UserResponse(
        Long id,
        String email,
        String nickname,
        String provider
) {
}
