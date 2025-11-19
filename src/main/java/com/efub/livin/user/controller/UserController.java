package com.efub.livin.user.controller;

import com.efub.livin.auth.domain.CustomUserDetails;
import com.efub.livin.user.dto.request.EmailVerificationRequest;
import com.efub.livin.user.dto.request.PasswordRequest;
import com.efub.livin.user.dto.request.SignupRequest;
import com.efub.livin.user.dto.request.UpdateUserRequest;
import com.efub.livin.user.dto.response.UserInfoResponse;
import com.efub.livin.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request){
        userService.signup(request);
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }

    // 이메일 인증 코드 검증
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationRequest request){
        boolean verified = userService.verifyEmail(request);
        if (verified){
            return ResponseEntity.ok("이메일 인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드가 일치하지 않습니다.");
        }
    }

    // 비밀번호 입력 및 최종 회원 가입
    @PostMapping("/signup/password")
    public ResponseEntity<String> setPassword(@RequestBody PasswordRequest request){
        userService.setPassword(request);
        return ResponseEntity.ok("회원가입 완료");
    }

    // 회원 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        UserInfoResponse userInfoResponse = new UserInfoResponse(userDetails.getUsername(),userDetails.getUserEmail(),userDetails.getSchool());
        return ResponseEntity.ok(userInfoResponse);
    }

    // 회원 정보 _ 닉네임 수정
    @PatchMapping("/me")
    public ResponseEntity<UserInfoResponse> updateInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                       @RequestBody UpdateUserRequest request) {
        UserInfoResponse userInfoResponse = userService.updateInfo(userDetails.getUser(), request.nickname());
        return ResponseEntity.ok(userInfoResponse);
    }
}
