package com.efub.livin.house.dto.response;

import com.efub.livin.house.domain.House;
import com.efub.livin.house.domain.HouseType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseResponse {

    private Long houseId;
    private String buildingName;
    private String address;
    private Boolean parking;
    private Boolean elevator;
    private Integer floor;
    private HouseType type;
    private String options;
    private String lon; // 경도
    private String lat; // 위도
    private String imageUrl;
    private String place_url;
    private String phone;
    private boolean isBookmarked;   // 북마크 여부

    public static HouseResponse from(House house, boolean isBookmarked){
        return HouseResponse.builder()
                .houseId(house.getHouseId())
                .buildingName(house.getBuildingName())
                .address(house.getAddress())
                .parking(house.getParking())
                .elevator(house.getElevator())
                .floor(house.getFloor())
                .type(house.getType())
                .options(house.getOptions())
                .lon(house.getLon())
                .lat(house.getLat())
                .imageUrl(house.getImageUrl())
                .isBookmarked(isBookmarked)
                .build();
    }

    // 북마크 정보 필요 없는 경우
    public static HouseResponse from(House house) {
        return from(house, false);
    }
}
