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
                        // ✅ 로그인은 항상 허용
                        .requestMatchers("/api/auth/login").permitAll()

                        // ✅ 지금 개발/테스트 중이라서 주요 API는 전부 풀어줌
                        .requestMatchers(
                                "/api/orders/**",
                                "/api/menus/**",
                                "/api/admin/stats/**",
                                "/api/stats/**",
                                "/api/tables/**",
                                "/ws/**",
                                "/topic/**",
                                "/app/**"
                        ).permitAll()

                        // 나머지 admin 전용 API 있으면 여기서만 ROLE_ADMIN 요구
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 그 외는 일단 허용
                        .anyRequest().permitAll()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
