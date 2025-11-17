package com.efub.livin.house.service;

import com.efub.livin.house.domain.Document;
import com.efub.livin.house.domain.House;
import com.efub.livin.house.dto.response.HousePagingListResponse;
import com.efub.livin.house.dto.request.HouseCreateRequest;
import com.efub.livin.house.dto.response.HouseResponse;
import com.efub.livin.house.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepository houseRepository;
    private final KakaoApiClient kakaoApiClient;

    private static final int house_list_size = 20;  // 임시로 20개 설정. 추후 프론트와 연동해보며 조절 예정

    // 새 자취/하숙 정보 등록
    @Transactional
    public HouseResponse addHouse(HouseCreateRequest request) {

        // 주소 -> 좌표 변환
        Document doc = kakaoApiClient.searchAddress(request.getAddress()).getDocuments().get(0);

        // House 저장
        House house = House.create(request, doc.getX(), doc.getY());
        House savedHouse = houseRepository.save(house);

        return HouseResponse.from(savedHouse);
    }

    // 자취/하숙 검색 및 필터링
    @Transactional(readOnly = true)
    public HousePagingListResponse search(
            String keyword, // null 가능
            String sort,    // review(default), bookmark
            String type,    // all(default), private, boarding
            String address, // all(default), 서대문구, 마포구, 종로구, 중구, 은평구, 용산구
            int page) {
        Pageable pageable = PageRequest.of(page, house_list_size);
        Page<House> searchHouses = houseRepository.search(keyword, sort, type, address, pageable);

        Page<HouseResponse> dtoPage = searchHouses.map(HouseResponse::from);
        return new HousePagingListResponse(dtoPage);
    }
}
