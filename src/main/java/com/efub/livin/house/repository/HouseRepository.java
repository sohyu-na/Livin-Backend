package com.efub.livin.house.repository;

import com.efub.livin.house.domain.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Long>, CustomHouseRepository {
}
