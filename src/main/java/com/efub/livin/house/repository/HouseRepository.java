package com.efub.livin.house.repository;

import com.efub.livin.house.domain.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HouseRepository extends JpaRepository<House, Long>, CustomHouseRepository {
    @Modifying
    @Query("update House h set h.bookmarkCnt = h.bookmarkCnt + 1 where h.houseId = :houseId")
    int increaseBookmarkCount(@Param("houseId") Long houseId);

    @Modifying
    @Query("update House h set h.bookmarkCnt = h.bookmarkCnt - 1 where h.houseId = :houseId and h.bookmarkCnt > 0")
    int decreaseBookmarkCount(@Param("houseId") Long houseId);
}
