package com.efub.livin.auth.service;

import com.efub.livin.auth.dto.request.LoginRequest;
import com.efub.livin.auth.dto.response.TokenResponse;
import com.efub.livin.auth.jwt.JwtUtil;
import com.efub.livin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public TokenResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            String accessToken = jwtUtil.generateAccessToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(authentication);

            // refresh token - redis 저장
            //

            return new TokenResponse(accessToken,refreshToken);
        } catch (Exception e){
            throw e;
        }
    }
}
