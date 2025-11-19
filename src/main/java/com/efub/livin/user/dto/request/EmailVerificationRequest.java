package com.efub.livin.user.dto.request;

import lombok.Getter;


@Getter
public class EmailVerificationRequest {
    private String email;
    private String verificationCode;
}
