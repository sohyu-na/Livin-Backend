package com.efub.livin.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    private String id;
    private String place_name;
    private String phone;
    private String address_name; // 전체 지번 주소
    private String road_address_name; // 전체 도로명 주소
    private String x; // 경도
    private String y; // 위도
    private String place_url; // 상세 페이지 url
    private String distance; // 중심 좌표까지 거리
}
