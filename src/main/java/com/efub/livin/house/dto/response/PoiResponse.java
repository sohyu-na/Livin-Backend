package com.efub.livin.house.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PoiResponse {
    private String placeName;
    private String address;
    private String category;
    private Double x; // 경도
    private Double y; // 위도
}
