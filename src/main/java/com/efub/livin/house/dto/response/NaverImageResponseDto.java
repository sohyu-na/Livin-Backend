package com.efub.livin.house.dto.response;

import com.efub.livin.house.domain.Item;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // items 외 다른 필드는 무시
public class NaverImageResponseDto {
    private List<Item> items; // Naver의 items 결과 리스트
}
