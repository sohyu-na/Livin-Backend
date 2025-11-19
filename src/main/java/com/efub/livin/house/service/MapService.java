package com.efub.livin.house.service;

import com.efub.livin.house.domain.House;
import com.efub.livin.house.domain.HouseType;
import com.efub.livin.house.dto.response.HouseMapResponse;
import com.efub.livin.house.dto.response.KakaoResponseDto;
import com.efub.livin.house.dto.response.PoiResponse;
import com.efub.livin.house.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final KakaoApiClient kakaoApiClient;
    private final HouseRepository houseRepository;

    // 카페
    public List<PoiResponse> getCafes(double centerX, double centerY, int radius) {
        KakaoResponseDto dto = kakaoApiClient.searchPoiByCategory("CE7", centerX, centerY, radius);
        return toPoiResponses(dto, "cafe");
    }

    // 마트 (편의점 & 대형마트)
    public List<PoiResponse> getStores(double centerX, double centerY, int radius) {
        // 편의점
        KakaoResponseDto storeDto = kakaoApiClient.searchPoiByCategory("CS2", centerX, centerY, radius);
        // 대형마트
        KakaoResponseDto martDto = kakaoApiClient.searchPoiByCategory("MT1", centerX, centerY, radius);

        List<PoiResponse> store = new ArrayList<>();

        store.addAll(toPoiResponses(storeDto, "store"));
        store.addAll(toPoiResponses(martDto, "store"));

        return store;
    }

    // 식당
    public List<PoiResponse> getFoods(double centerX, double centerY, int radius) {
        KakaoResponseDto dto = kakaoApiClient.searchPoiByCategory("FD6", centerX, centerY, radius);
        return toPoiResponses(dto, "food");
    }

    // 교통 (지하철)
    // TODO: 버스정류장 저장. kakao에서 제공하는 category에는 버스정류장은 따로 없어서, 검색어 입력으로 받아와야할 듯.
    public List<PoiResponse> getTransports(double centerX, double centerY, int radius) {
        // 지하철
        KakaoResponseDto dto = kakaoApiClient.searchPoiByCategory("SW8", centerX, centerY, radius);
        return toPoiResponses(dto, "transport");
    }

    // 지도에 보일 자취/하숙 정보 불러오기
    public List<HouseMapResponse> getHousesInBounds(
            double minLat, double maxLat,
            double minLon, double maxLon,
            HouseType typeOrNull // null이면 ALL
    ) {
        List<House> result = houseRepository.findInBounds(minLat, maxLat, minLon, maxLon, typeOrNull);
        return result.stream()
                .map(HouseMapResponse::from)
                .toList();
    }

    private List<PoiResponse> toPoiResponses(KakaoResponseDto dto, String category) {
        return dto.getDocuments().stream()
                .map(doc -> PoiResponse.builder()
                        .placeName(doc.getPlace_name())
                        .y(Double.parseDouble(doc.getY()))
                        .x(Double.parseDouble(doc.getX()))
                        .address(doc.getAddress_name())
                        .category(category)
                        .build())
                .toList();
    }
}
