package com.ptproject.back_sq.service;

import com.ptproject.back_sq.config.jwt.JwtTokenProvider;
import com.ptproject.back_sq.dto.auth.LoginRequest;
import com.ptproject.back_sq.dto.auth.LoginResponse;
import com.ptproject.back_sq.entity.employee.Employee;
import com.ptproject.back_sq.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse loginByPin(LoginRequest request) {
        String rawPin = request.getPin();

        Employee employee = employeeRepository.findAll().stream()
                .filter(e -> passwordEncoder.matches(rawPin, e.getPin()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("PIN 번호가 올바르지 않습니다."));

        String token = jwtTokenProvider.createToken(
                employee.getId(),
                employee.getName(),
                employee.getRole()
        );

        return new LoginResponse(
                token,
                employee.getName(),
                employee.getRole().name()
        );
    }
}
