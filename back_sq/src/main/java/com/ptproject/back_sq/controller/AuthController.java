package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.auth.LoginRequest;
import com.ptproject.back_sq.dto.auth.LoginResponse;
import com.ptproject.back_sq.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.loginByPin(request);
    }
}
