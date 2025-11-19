package com.efub.livin.house.dto.response;

import com.efub.livin.house.domain.House;
import com.efub.livin.house.domain.HouseType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class HouseMapResponse {
    private Long houseId;
    private String buildingName;
    private String address;
    private String category;
    private HouseType type;
    private String x; // 경도
    private String y; // 위도

    public static HouseMapResponse from (House house) {
        return HouseMapResponse.builder()
                .houseId(house.getHouseId())
                .buildingName(house.getBuildingName())
                .address(house.getAddress())
                .category("house")
                .type(house.getType())
                .x(house.getLon())  // 경도
                .y(house.getLat())  // 위도
                .build();
    }
}
