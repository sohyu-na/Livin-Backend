package com.efub.livin.house.service;

import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.house.domain.Document;
import com.efub.livin.house.domain.House;
import com.efub.livin.house.domain.HouseType;
import com.efub.livin.house.dto.response.*;
import com.efub.livin.house.dto.request.HouseCreateRequest;
import com.efub.livin.bookmark.repository.BookmarkRepository;
import com.efub.livin.house.repository.HouseRepository;
import com.efub.livin.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepository houseRepository;
    private final BookmarkRepository bookmarkRepository;
    private final KakaoApiClient kakaoApiClient;
    private final MapService mapService;

    private static final int house_list_size = 20;  // 임시로 20개 설정. 추후 프론트와 연동해보며 조절 예정
    private static final int bookmark_list_size = 10;

    // 새 자취/하숙 정보 등록
    @Transactional
    public HouseResponse addHouse(HouseCreateRequest request, User user) {

        // 주소 -> 좌표 변환
        Document doc = kakaoApiClient.searchAddress(request.getAddress()).getDocuments().get(0);

        // House 저장
        House house = request.toEntity(doc.getX(), doc.getY());
        House savedHouse = houseRepository.save(house);

        return HouseResponse.from(savedHouse);
    }

    // 자취/하숙 상세 정보 조회
    @Transactional(readOnly = true)
    public HouseResponse getHouse(Long id, User user) {
        House house = houseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_NOT_FOUND));
        boolean bookmarked = isBookmarked(house, user);
        return HouseResponse.from(house, bookmarked);
    }

    // 자취/하숙 검색 및 필터링
    @Transactional(readOnly = true)
    public HousePagingListResponse search(
            String keyword, // null 가능
            String sort,    // review(default), bookmark
            String type,    // all(default), private, boarding
            String address, // all(default), 서대문구, 마포구, 종로구, 중구, 은평구, 용산구
            int page,
            User user
    ) {
        // 페이징을 반영한 검색
        Pageable pageable = PageRequest.of(page, house_list_size);
        HouseType houseType = parseHouseType(type);
        Page<House> searchHouses = houseRepository.search(keyword, sort, houseType, address, pageable);

        // houseId 리스트만 뽑기
        List<Long> houseIds = searchHouses.getContent().stream()
                .map(House::getHouseId)
                .collect(Collectors.toList());

        // 유저가 북마크한 리스트 id 조회
        Set<Long> bookmarkedSet = new HashSet<>(
                bookmarkRepository.findBookmarkedIdsIn(user.getUserId(), houseIds)
        );

        // 북마크 정보 반영한 dto list 생성
        List<HouseResponse> content = searchHouses.getContent().stream()
                .map(house -> HouseResponse.from(
                        house,
                        bookmarkedSet.contains(house.getHouseId())
                ))
                .toList();

        return new HousePagingListResponse(searchHouses, content);
    }

    // 자취/하숙 및 근처 편의시설 조회
    @Transactional(readOnly = true)
    public MapDataResponse getMapWithPoiData(
            double minLat, double maxLat, double minLon, double maxLon,
            double centerLat, double centerLon, double doubleRadius,
            String type, boolean isShowCafe, boolean isShowStore, boolean isShowFood, boolean isShowTransport
    ) {
        // houseType 파싱
        HouseType houseType = parseHouseType(type);

        // 자취/하숙 조회
        List<HouseMapResponse> houses = mapService.getHousesInBounds(minLat, maxLat, minLon, maxLon, houseType);

        // 편의시설 조회 (토글에 따라 선택적으로)
        List<PoiResponse> cafes = List.of();
        List<PoiResponse> stores = List.of();
        List<PoiResponse> foods = List.of();
        List<PoiResponse> transports = List.of();

        int radius = (int) Math.ceil(doubleRadius);
        if (isShowCafe) {
            cafes = mapService.getCafes(centerLon, centerLat, radius);
        }
        if (isShowStore) {
            stores = mapService.getStores(centerLon, centerLat, radius);
        }
        if (isShowFood) {
            foods = mapService.getFoods(centerLon, centerLat, radius);
        }
        if (isShowTransport) {
            transports = mapService.getTransports(centerLon, centerLat, radius);
        }

        return MapDataResponse.builder()
                .houses(houses)
                .cafes(cafes)
                .stores(stores)
                .restaurants(foods)
                .transports(transports)
                .build();
    }

    // HouseType String -> enum으로 파싱
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

    // 북마크 되어있는지 확인
    private boolean isBookmarked(House house, User user) {
        return bookmarkRepository.existsByUserAndHouse(user, house);
    }
}
