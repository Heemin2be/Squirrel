package com.ptproject.back_sq.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String employeeName;
    private String role; // "ROLE_ADMIN" / "ROLE_STAFF"
}
