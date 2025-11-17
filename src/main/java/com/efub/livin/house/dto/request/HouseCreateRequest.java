package com.efub.livin.house.dto.request;

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

}
