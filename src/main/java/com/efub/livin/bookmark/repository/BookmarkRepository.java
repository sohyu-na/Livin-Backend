package com.efub.livin.bookmark.repository;

import com.efub.livin.bookmark.domain.Bookmark;
import com.efub.livin.house.domain.House;
import com.efub.livin.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserAndHouse(User user, House house);
    Boolean existsByUserAndHouse(User user, House house);

    // houseIds 중에 유저가 북마크한 리스트
    @Query("select b.house.houseId from Bookmark b " +
            "where b.user.userId = :userId and b.house.houseId in :houseIds")
    List<Long> findBookmarkedIdsIn(@Param("userId") Long userId,
                                   @Param("houseIds") List<Long> houseIds);
}
