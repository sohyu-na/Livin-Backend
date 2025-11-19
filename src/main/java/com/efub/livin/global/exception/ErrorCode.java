package com.efub.livin.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED_ACCESS(401, "인증되지 않은 사용자입니다."),

    // 유저 관련
    NICKNAME_DUPLICATED(409, "이미 존재하는 닉네임입니다."),
    EMAIL_NOT_VERIFIED(400, "해당 이메일로 요청한 사용자 정보가 없습니다."),
    NICKNAME_BLANK(400, "닉네임은 비어 있을 수 없습니다."),

    // 인증 관련
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
    INVALID_TOKEN(401, "유효하지 않는 토큰입니다."),

    // House 저장 관련
    KAKAO_API_ERROR(500, "카카오 API 연동 중 오류가 발생했습니다."),
    KAKAO_API_EMPTY_RESPONSE(500, "카카오 API로부터 빈 응답을 받았습니다."),
    HOUSE_NOT_FOUND(404, "해당 하숙/자취 건물을 찾을 수 없습니다."),

    //리뷰 관련
    HOUSE_REVIEW_NOT_FOUND(404, "해당 자취/하숙 리뷰를 찾을 수 없습니다."),
    DORM_REVIEW_NOT_FOUND(404, "해당 기숙사 리뷰를 찾을 수 없습니다.");


    private final int status;
    private final String message;
}
