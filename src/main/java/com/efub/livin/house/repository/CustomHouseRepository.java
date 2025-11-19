package com.efub.livin.house.repository;

import com.efub.livin.house.domain.House;
import com.efub.livin.house.domain.HouseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomHouseRepository {
    // 검색어 기반 자취/하숙 필터링
    Page<House> search(String keyword, String sort, HouseType houseType, String address, Pageable pageable);

    // 지도 중심 범위 기준 자취/하숙 리스트
    List<House> findInBounds(double minLat, double maxLat, double minLon, double maxLon, HouseType houseType);
}