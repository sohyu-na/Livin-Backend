package com.efub.livin.house.service;

import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.house.dto.response.KakaoResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KakaoApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.map.kakao-api-key}")
    private String kakaoApiKey;

    private static final String KEYWORD_API_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private static final String ADDRESS_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String CATEGORY_API_URL = "https://dapi.kakao.com/v2/local/search/category.json";

    // 키워드로 장소 검색
    public KakaoResponseDto searchByKeyword(String keyword) {

        String x = "126.95"; // 중심 경도
        String y = "37.56"; // 중심 위도
        Integer radius = 1500; // 반경. 단위(m). 1.5km

        // 카카오 요청 url
        String url = KEYWORD_API_URL + "?query={keyword}&x={x}&y={y}&radius={radius}&sort=distance";
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("x", x);
        params.put("y", y);
        params.put("radius", radius);

        // Header 설정
        HttpEntity<String> entity = createAuthHeaders();

        // Kakao API 호출
        ResponseEntity<KakaoResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                KakaoResponseDto.class,
                params
        );

        return response.getBody();
    }


    // 주소로 좌표 검색
    public KakaoResponseDto searchAddress(String address) {
        // 헤더 설정
        HttpEntity<String> entity = createAuthHeaders();

        // URI 템플릿 설정
        String url = ADDRESS_API_URL + "?query={query}";

        Map<String, String> params = new HashMap<>();
        params.put("query", address);

        // API 호출
        try {
            ResponseEntity<KakaoResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    KakaoResponseDto.class,
                    params
            );

            if (response.getBody() == null) {
                throw new CustomException(ErrorCode.KAKAO_API_EMPTY_RESPONSE);
            }
            return response.getBody();

        } catch (Exception e) {
            log.error("Kakao 주소 검색 API 호출 오류: {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_API_ERROR);
        }
    }

    // 카테고리로 poi 검색
    public KakaoResponseDto searchPoiByCategory(String categoryCode, double x, double y, int radius){
        // URI 템플릿 설정
        String url = CATEGORY_API_URL + "?category_group_code={code}&x={x}&y={y}&radius={radius}&sort=distance";
        Map<String, Object> params = new HashMap<>();
        params.put("code", categoryCode);
        params.put("x", String.valueOf(x));
        params.put("y", String.valueOf(y));
        params.put("radius", radius);

        // 해더 설정
        HttpEntity<String> entity = createAuthHeaders();

        // API 호출
        try {
            ResponseEntity<KakaoResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    KakaoResponseDto.class,
                    params
            );

            if (response.getBody() == null) {
                throw new CustomException(ErrorCode.KAKAO_API_EMPTY_RESPONSE);
            }
            return response.getBody();

        } catch (Exception e) {
            log.error("Kakao 카테고리 검색 API 호출 오류: {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_API_ERROR);
        }
    }

    /**
     * Kakao 인증 헤더 생성
     * */
    private HttpEntity<String> createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        return new HttpEntity<>(headers);
    }
}
