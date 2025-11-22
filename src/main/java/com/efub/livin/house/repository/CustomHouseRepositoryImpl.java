package com.efub.livin.house.repository;

import com.efub.livin.bookmark.domain.QBookmark;
import com.efub.livin.house.domain.House;
import com.efub.livin.house.domain.HouseType;
import com.efub.livin.house.domain.QHouse;
import com.efub.livin.user.domain.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CustomHouseRepositoryImpl implements CustomHouseRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<House> search(
            String keyword, // null 가능
            String sort,    // review(default), bookmark
            HouseType houseType,
            String address, // all(default), 서대문구, 마포구...
            Pageable pageable) {
        QHouse house = QHouse.house;

        BooleanBuilder builder = new BooleanBuilder();

        // keyword 검색
        if (hasText(keyword)) {
            builder.and(house.buildingName.contains(keyword));
        }
        // 전체/자취/하숙 필터링
        if(houseType != null){
            builder.and(house.type.eq(houseType));
        }
        // 자치구 필터링
        if (hasText(address) && !address.equalsIgnoreCase("all")) {
            builder.and(house.address.contains(address));
        }
        // 정렬 (review/bookmark)
        OrderSpecifier<?> sortOrder;
        if(sort != null && sort.equalsIgnoreCase("bookmark")){
            sortOrder = house.bookmarkCnt.desc();
        } else {
            sortOrder = house.rate.desc();
        }

        List<House> content = queryFactory
                .selectFrom(house)
                .where(builder)
                .orderBy(sortOrder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 목록 개수
        Long total = queryFactory
                .select(house.count())
                .from(house)
                .where(builder)
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public List<House> findInBounds(double minLat, double maxLat, double minLon, double maxLon,
                                    HouseType houseType) {
        QHouse house = QHouse.house;

        BooleanBuilder builder = new BooleanBuilder()
                .and(house.lat.castToNum(Double.class).between(minLat, maxLat))
                .and(house.lon.castToNum(Double.class).between(minLon, maxLon));

        // 자취/하숙/전체
        if(houseType != null){
            builder.and(house.type.eq(houseType));
        }

        return queryFactory
                .selectFrom(house)
                .where(builder)
                .fetch();
    }

    @Override
    public List<House> findByMyBookmark(User user) {
        QHouse house = QHouse.house;
        QBookmark bookmark = QBookmark.bookmark;

        return queryFactory
                .select(house)
                .from(bookmark)
                .join(bookmark.house, house)
                .where(bookmark.user.eq(user))
                .orderBy(bookmark.id.desc())
                .fetch();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
