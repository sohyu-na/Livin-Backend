package com.efub.livin.house.service;

import com.efub.livin.house.dto.response.NaverImageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class NaverApiClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String NAVER_API_URL = "https://openapi.naver.com/v1/search/image";

    @Value("${naver.search.naver-client-id}")
    private String naverClientId;

    @Value("${naver.search.naver-client-secret}")
    private String naverClientSecret;

    public NaverImageResponseDto searchImage(String query) {
        // HTTP 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverClientId);
        headers.set("X-Naver-Client-Secret", naverClientSecret);
        headers.set("Accept", "application/json"); // JSON 응답 요청
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // URI 템플릿 설정
        String url = NAVER_API_URL + "?query={query}&display=1&sort=sim"; // 1개만, 유사도순

        Map<String, String> params = new HashMap<>();
        params.put("query", query); // 카카오에서 받은 장소 이름

        // API 호출
        try {
            ResponseEntity<NaverImageResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    NaverImageResponseDto.class, // DTO로 자동 변환
                    params
            );
            return response.getBody();

        } catch (Exception e) {
            log.error("Naver API 호출 오류 (query: {}): {}. 이미지를 null로 저장합니다.",
                    query, e.getMessage());
            return null; // 오류 발생 시 null 반환
        }
    }
}
