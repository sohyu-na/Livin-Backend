package com.efub.livin.house.repository;

import com.efub.livin.house.domain.House;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomHouseRepository {
    Page<House> search(String keyword, String sort, String type, String address, Pageable pageable);
}