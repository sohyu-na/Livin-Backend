package com.efub.livin.bookmark.service;

import com.efub.livin.bookmark.dto.response.BookmarkResponse;
import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.bookmark.domain.Bookmark;
import com.efub.livin.house.domain.House;
import com.efub.livin.house.dto.response.*;
import com.efub.livin.bookmark.repository.BookmarkRepository;
import com.efub.livin.house.repository.HouseRepository;
import com.efub.livin.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookmarkService {

    private final HouseRepository houseRepository;
    private final BookmarkRepository bookmarkRepository;

    // 자취/하숙 북마크
    @Transactional
    public BookmarkResponse toggleBookmark(Long houseId, User user) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByUserAndHouse(user, house)
                .orElse(null);
        if(bookmark != null){
            log.info("bookmark id : {}", bookmark.getId());
        }
        if(bookmark == null){
            log.info("bookmark add");
            Bookmark saved = bookmarkRepository.save(Bookmark.builder()
                    .user(user)
                    .house(house)
                    .build());
            houseRepository.increaseBookmarkCount(house.getHouseId());
            return new BookmarkResponse(true, saved.getId());
        } else {
            bookmarkRepository.delete(bookmark);
            log.info("bookmark delete");
            houseRepository.decreaseBookmarkCount(house.getHouseId());
            return new BookmarkResponse(false, null);
        }
    }

    // 본인이 한 북마크 리스트 조회
    @Transactional(readOnly = true)
    public List<HouseResponse> getMyBookmark(User user) {
        List<House> houseList = houseRepository.findByMyBookmark(user);
        return houseList.stream()
                .map(house -> HouseResponse.from(house, true))
                .collect(Collectors.toList());
    }
}

