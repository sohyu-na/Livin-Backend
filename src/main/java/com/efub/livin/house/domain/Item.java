package com.efub.livin.house.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // thumbnail 외 다른 필드 무시
public class Item {

    // 네이버 응답의 이미지 주소 키 thumbnail
    private String thumbnail;
}
