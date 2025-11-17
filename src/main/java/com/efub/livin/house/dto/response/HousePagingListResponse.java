package com.efub.livin.house.dto.response;

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

    public HousePagingListResponse(Page<HouseResponse> page){
        this.houses = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.length = page.getNumberOfElements();
    }
}
