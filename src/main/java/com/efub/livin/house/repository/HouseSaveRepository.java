package com.efub.livin.house.repository;

import com.efub.livin.house.domain.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseSaveRepository extends JpaRepository<House, Long> {
    // 해당 주소가 이미 존재하는지 확인
    boolean existsByAddress(String address);
}
