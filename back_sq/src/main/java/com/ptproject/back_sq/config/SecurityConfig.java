package com.ptproject.back_sq.config;

import com.ptproject.back_sq.config.jwt.JwtAuthenticationFilter;
import com.ptproject.back_sq.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 🔹 WebSocket (STOMP)는 전부 허용
                        .requestMatchers("/ws/**", "/topic/**", "/app/**").permitAll()

                        // 🔹 로그인 API는 허용
                        .requestMatchers("/api/auth/login").permitAll()

                        // 🔹 주문/메뉴/테이블 API는 일단 개발 단계에서 모두 허용
                        //    (키오스크에서도 토큰 없이 쓰게 하려면 이대로 두면 됨)
                        .requestMatchers(
                                "/api/orders/**",
                                "/api/menus/**",
                                "/api/tables/**"
                        ).permitAll()

                        // 🔹 관리자 전용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 그 외 API는 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
