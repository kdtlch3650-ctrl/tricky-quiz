package com.trickyquiz.backend.common.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 초기 API 개발 단계에서는 세션/프론트 연동 전까지 CSRF를 끄고 진행합니다.
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 문서 기준으로 비로그인 사용자도 접근할 수 있는 공개 API입니다.
                        .requestMatchers("/api/health", "/api/categories", "/api/rankings").permitAll()
                        // 위에서 공개하지 않은 API는 Spring Security가 로그인 여부를 검사합니다.
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        // API에서는 비로그인 요청을 로그인 페이지 이동이 아니라 401 응답으로 처리합니다.
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                );

        return http.build();
    }
}
