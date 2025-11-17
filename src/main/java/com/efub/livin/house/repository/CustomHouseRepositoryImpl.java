package com.efub.livin.house.repository;

import com.efub.livin.house.domain.House;
import com.efub.livin.house.domain.HouseType;
import com.efub.livin.house.domain.QHouse;
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
            String type,    // all(default), private, boarding
            String address, // all(default), 서대문구, 마포구...
            Pageable pageable) {
        QHouse house = QHouse.house;

        BooleanBuilder builder = new BooleanBuilder();

        // keyword 검색
        if (hasText(keyword)) {
            builder.and(house.buildingName.contains(keyword));
        }
        // 전체/자취/하숙 필터링
        HouseType houseType = parseHouseType(type);
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

    private HouseType parseHouseType(String type) {
        if (type == null || type.equalsIgnoreCase("all")) {
            return null; // 전체 조회
        }

        if (type.equalsIgnoreCase("private")) {
            return HouseType.PRIVATE;
        }

        if (type.equalsIgnoreCase("boarding")) {
            return HouseType.BOARDING;
        }

        return null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
