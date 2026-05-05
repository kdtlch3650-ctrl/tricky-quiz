package com.trickyquiz.backend.api.user;

import com.trickyquiz.backend.api.user.dto.UserResponse;
import com.trickyquiz.backend.domain.user.AuthProvider;
import com.trickyquiz.backend.domain.user.User;
import com.trickyquiz.backend.domain.user.UserRepository;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 현재 로그인한 사용자의 기본 정보를 반환합니다.
     *
     * OAuth2 로그인 후에는 Google 계정 정보를 저장하거나 기존 사용자 정보를 돌려줍니다.
     */
    @GetMapping("/api/me")
    public UserResponse me(@AuthenticationPrincipal OAuth2User oauth2User) {
        String providerUserId = oauth2User.getName();
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = attributeOrFallback(attributes, "email", providerUserId + "@local.test");
        String nickname = attributeOrFallback(attributes, "name", email);

        User user = userRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, providerUserId)
                .orElseGet(() -> userRepository.save(new User(
                        AuthProvider.GOOGLE,
                        providerUserId,
                        email,
                        nickname
                )));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProvider().name()
        );
    }

    private String attributeOrFallback(Map<String, Object> attributes, String key, String fallback) {
        Object value = attributes.get(key);
        if (value instanceof String text && !text.isBlank()) {
            return text;
        }

        return fallback;
    }
}
