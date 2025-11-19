package com.efub.livin.house.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MapDataResponse {
    private List<HouseMapResponse> houses;   // 자취/하숙
    private List<PoiResponse> cafes;         // 카페
    private List<PoiResponse> stores;        // 편의점/마트
    private List<PoiResponse> restaurants;   // 식당
    private List<PoiResponse> transports;    // 교통(지하철)
}
