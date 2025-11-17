package com.efub.livin.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.efub.livin.house.domain.Document;

import java.util.List;

@Getter
@AllArgsConstructor
public class KakaoResponseDto {

    private List<Document> documents;
}
