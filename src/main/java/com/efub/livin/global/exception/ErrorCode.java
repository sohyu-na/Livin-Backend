package com.efub.livin.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED_ACCESS(401, "인증되지 않은 사용자입니다."),

    // House 저장 관련
    KAKAO_API_ERROR(500, "카카오 API 연동 중 오류가 발생했습니다."),
    KAKAO_API_EMPTY_RESPONSE(500, "카카오 API로부터 빈 응답을 받았습니다.");

    private final int status;
    private final String message;
}
