package com.trickyquiz.backend.api.user;

import com.trickyquiz.backend.api.user.dto.UserResponse;
import com.trickyquiz.backend.domain.user.AuthProvider;
import com.trickyquiz.backend.domain.user.User;
import com.trickyquiz.backend.domain.user.UserRepository;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
     */
    @GetMapping("/api/me")
    public UserResponse me(Principal principal) {
        String providerUserId = principal.getName();
        User user = userRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, providerUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자 정보를 찾을 수 없습니다."
                ));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProvider().name()
        );
    }
}
