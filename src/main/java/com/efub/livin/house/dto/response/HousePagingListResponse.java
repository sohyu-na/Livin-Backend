package com.efub.livin.house.dto.response;

import com.efub.livin.house.domain.House;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class HousePagingListResponse {
    private List<HouseResponse> houses;
    private int totalPages;
    private int currentPage;
    private int length;

    public HousePagingListResponse(Page<House> pageInfo, List<HouseResponse> housesWithBookmark){
        this.houses = housesWithBookmark;
        this.totalPages = pageInfo.getTotalPages();
        this.currentPage = pageInfo.getNumber();
        this.length = pageInfo.getNumberOfElements();
    }
}
