package com.trickyquiz.backend.api.user;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.trickyquiz.backend.common.config.SecurityConfig;
import com.trickyquiz.backend.domain.user.AuthProvider;
import com.trickyquiz.backend.domain.user.User;
import com.trickyquiz.backend.domain.user.UserRepository;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void meReturnsCurrentUser() throws Exception {
        User user = new User(AuthProvider.GOOGLE, "google-user-1", "user@example.com", "사용자");
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findByProviderAndProviderUserId(eq(AuthProvider.GOOGLE), eq("google-user-1")))
                .thenReturn(Optional.of(user));

        OAuth2User oauth2User = new DefaultOAuth2User(
                Collections.emptyList(),
                Map.of(
                        "sub", "google-user-1",
                        "email", "user@example.com",
                        "name", "사용자"
                ),
                "sub"
        );

        mockMvc.perform(get("/api/me").with(oauth2Login().oauth2User(oauth2User)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.nickname").value("사용자"))
                .andExpect(jsonPath("$.provider").value("GOOGLE"));
    }

    @Test
    void meRequiresLogin() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }
}
