package com.efub.livin.user.service;

import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.user.domain.User;
import com.efub.livin.user.dto.SignupData;
import com.efub.livin.user.dto.request.EmailVerificationRequest;
import com.efub.livin.user.dto.request.PasswordRequest;
import com.efub.livin.user.dto.request.SignupRequest;
import com.efub.livin.user.dto.response.UserInfoResponse;
import com.efub.livin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    // 이메일 -> 인증 코드 저장
    private final ConcurrentHashMap<String, String> emailVerificationCodes = new ConcurrentHashMap<>();

    // 이메일 -> 회원가입 임시 정보 저장
    private final ConcurrentHashMap<String, SignupData> tempSignupData = new ConcurrentHashMap<>();

    @Transactional
    public void signup(SignupRequest request){
        if (isNicknameExists(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATED);  // 에러코드 별도 정의 필요
        }
        String email = request.getEmail();

        // 인증 코드 생성
        String code = UUID.randomUUID().toString().substring(0, 6);

        // 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + code);
        mailSender.send(message);

        // 이메일 인증 코드 저장
        emailVerificationCodes.put(email, code);

        // 사용자 정보 임시 저장
        tempSignupData.put(request.getEmail(),new SignupData(request.getNickname(),request.getSchool()));
    }

    @Transactional(readOnly = true)
    public boolean verifyEmail(EmailVerificationRequest request){
        String email = request.getEmail();
        String inputCode = request.getVerificationCode();

        String correctCode = emailVerificationCodes.get(email);
        if (correctCode != null && correctCode.equals(inputCode)) {
            emailVerificationCodes.remove(email);
            return true;
        }
        return false;
    }

    @Transactional
    public void setPassword(PasswordRequest request){
        String email = request.getEmail();

        // 임시 저장 사용자 정보 조회
        SignupData data = tempSignupData.get(email);
        if(data == null){
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .email(email)
                .nickname(data.nickname())
                .school(data.school())
                .password(encodedPassword)
                .build();
        userRepository.save(user);

        // 임시 저장 사용자 정보 삭제
        tempSignupData.remove(email);
    }

    @Transactional
    public UserInfoResponse updateInfo(User user, String nickname){
        if (isNicknameExists(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATED);  // 에러코드 별도 정의 필요
        }
        user.changeNickname(nickname);
        return new UserInfoResponse(user.getNickname(), user.getEmail(), user.getSchool());
    }

    // 닉네임 중복 검사
    private boolean isNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
