package com.ptproject.back_sq.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private final int status;          // HTTP 상태 코드 (400, 404, 500...)
    private final String error;        // 상태명 (Bad Request, Not Found...)
    private final String message;      // 상세 메시지
    private final String path;         // 요청 URL
    private final LocalDateTime timestamp; // 발생 시각
}
