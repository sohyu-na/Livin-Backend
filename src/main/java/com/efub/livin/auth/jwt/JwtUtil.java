package com.efub.livin.auth.jwt;

import com.efub.livin.auth.domain.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final UserDetailsService userDetailsService;

    private long ACC_EXP_MS = 604800000; //임시: 7일
    private long REFRESH_EXP_MS = 604800000;

    public JwtUtil(@Value("${jwt.secret}") String secretKey, UserDetailsService userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)); // HMAC SHA 키 생성
        this.userDetailsService = userDetailsService;
    }

    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // ROLE_USER, ROLE_ADMIN 등 문자열만 추출
                .collect(Collectors.joining(","));
        Date now = new Date();
        return Jwts.builder()
                .setSubject(customUserDetails.getUserEmail())
                .claim("userId", customUserDetails.getUserId())
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ACC_EXP_MS))
                .signWith(secretKey)
                .compact();
    }
    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // ROLE_USER, ROLE_ADMIN 등 문자열만 추출
                .collect(Collectors.joining(","));
        Date now = new Date();
        return Jwts.builder()
                .setSubject(customUserDetails.getUserEmail())
                .claim("userId", customUserDetails.getUserId())
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_EXP_MS))
                .signWith(secretKey)
                .compact();
    }
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        Object authoritiesClaim = claims.get("authorities");

        if (authoritiesClaim == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authoritiesClaim.toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
    private Claims parseClaims(String token) {
        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}

