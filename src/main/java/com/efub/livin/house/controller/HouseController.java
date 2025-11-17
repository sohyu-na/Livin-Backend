package com.efub.livin.house.controller;

import com.efub.livin.house.dto.request.HouseCreateRequest;
import com.efub.livin.house.dto.response.HousePagingListResponse;
import com.efub.livin.house.dto.response.HouseResponse;
import com.efub.livin.house.service.HouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;

    // 새 자취/하숙 생성 컨트롤러
    @PostMapping(value = "/new")
    public ResponseEntity<HouseResponse> save(
            @Valid @RequestBody HouseCreateRequest request) {

        HouseResponse response = houseService.addHouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 자취/하숙 검색 및 필터링 컨트롤러
    // GET /house/search?keyword=신촌&sort=Popular&type=PRIVATE&address=서대문구&page=0
    @GetMapping(value = "/search")
    public ResponseEntity<HousePagingListResponse> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "address") String address,
            @RequestParam(value = "page") int page
    ) {
        HousePagingListResponse response = houseService.search(keyword, sort, type, address, page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
