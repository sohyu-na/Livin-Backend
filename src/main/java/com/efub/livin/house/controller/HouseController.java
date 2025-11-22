package com.efub.livin.house.controller;

import com.efub.livin.auth.domain.CustomUserDetails;
import com.efub.livin.house.dto.request.HouseCreateRequest;
import com.efub.livin.bookmark.dto.response.BookmarkResponse;
import com.efub.livin.house.dto.response.HousePagingListResponse;
import com.efub.livin.house.dto.response.HouseResponse;
import com.efub.livin.house.dto.response.MapDataResponse;
import com.efub.livin.house.service.HouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;

    // 새 자취/하숙 생성 컨트롤러
    @PostMapping(value = "/new")
    public ResponseEntity<HouseResponse> save(
            @Valid @RequestBody HouseCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        HouseResponse response = houseService.addHouse(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 자취/하숙 상세 조회 컨트롤러
    @GetMapping(value = "/{houseId}")
    public ResponseEntity<HouseResponse> getOneHouse(@PathVariable Long houseId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        HouseResponse response = houseService.getHouse(houseId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 자취/하숙 검색 및 필터링 컨트롤러
    // GET /house/search?keyword=신촌&sort=Popular&type=PRIVATE&address=서대문구&page=0
    @GetMapping(value = "/search")
    public ResponseEntity<HousePagingListResponse> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "address") String address,
            @RequestParam(value = "page") int page,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        HousePagingListResponse response = houseService.search(keyword, sort, type, address, page, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 지도 위에 띄울 자취/하숙 정보 및 편의시설 정보 조회 컨트롤러
     /** GET /house/map?minLat=1127385&maxLat=1128001.25&minLon=487431.875&maxLon=488315.625
     * &centerLat=1127693.125&centerLon=487873.75&radius=538.697
     * &showCafe=true&showFood=true
     */
    @GetMapping(value = "/map")
    public MapDataResponse getMap(
            @RequestParam(value = "minLat") double minLat,
            @RequestParam(value = "maxLat") double maxLat,
            @RequestParam(value = "minLon") double minLon,
            @RequestParam(value = "maxLon") double maxLon,
            @RequestParam(value = "centerLat") double centerLat,
            @RequestParam(value = "centerLon") double centerLon,
            @RequestParam(value = "radius") double radius,
            @RequestParam(value = "houseType", defaultValue = "ALL") String houseType,
            @RequestParam(value = "showCafe", defaultValue = "false") boolean showCafe,
            @RequestParam(value = "showStore", defaultValue = "false") boolean showStore,
            @RequestParam(value = "showFood", defaultValue = "false") boolean showFood,
            @RequestParam(value = "showTransport", defaultValue = "false") boolean showTransport
    ) {
        return houseService.getMapWithPoiData(
                minLat, maxLat, minLon, maxLon,
                centerLat, centerLon, radius,
                houseType, showCafe, showStore, showFood, showTransport
        );
    }
}
