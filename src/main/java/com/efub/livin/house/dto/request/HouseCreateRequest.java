package com.efub.livin.house.dto.request;

import com.efub.livin.house.domain.House;
import com.efub.livin.house.domain.HouseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HouseCreateRequest {

    @NotNull
    private HouseType type;

    @NotBlank
    private String buildingName;

    private String address;
    private Boolean parking;
    private String options;
    private String imageUrl;
    private Integer floor;
    private Boolean elevator;

    public House toEntity(String lon, String lat) {
        return House.builder()
                .type(this.type)
                .buildingName(this.buildingName)
                .address(this.address)
                .parking(this.parking)
                .elevator(this.elevator)
                .floor(this.floor)
                .options(this.options)
                .imageUrl(this.imageUrl)
                .lon(lon)
                .lat(lat)
                .build();
    }

}
