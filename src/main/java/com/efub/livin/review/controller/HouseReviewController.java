package com.efub.livin.review.controller;

import com.efub.livin.review.dto.request.HouseReviewCreateRequestDto;
import com.efub.livin.review.dto.response.HouseReviewDetailResponseDto;
import com.efub.livin.review.dto.response.HouseReviewListResponseDto;
import com.efub.livin.review.service.HouseReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/house")
public class HouseReviewController {

    private final HouseReviewService houseReviewService;

    //리뷰 생성
    @PostMapping("{houseId}/review")
    public ResponseEntity<Long> createHouseReview(@PathVariable Long houseId,
                                                  @RequestBody HouseReviewCreateRequestDto request){
        Long id = houseReviewService.createHouseReview(houseId, request);
        return ResponseEntity.ok(id);
    }

    //리뷰 전체 목록 조회
    @GetMapping("/{houseId}/review")
    public ResponseEntity<List<HouseReviewListResponseDto>> getHouseReviewList(
            @PathVariable Long houseId
    ){
        List<HouseReviewListResponseDto> list = houseReviewService.getHouseReviewList(houseId);
        return ResponseEntity.ok(list);
    }

    //리뷰 상세 조회
    @GetMapping("/{houseId}/review/{reviewId}")
    public ResponseEntity<HouseReviewDetailResponseDto> getHouseReviewDetail(@PathVariable Long houseId,
                                                                             @PathVariable Long reviewId){
        return ResponseEntity.ok(houseReviewService.getHouseReviewDetail(reviewId));
    }

    //리뷰 삭제
    @DeleteMapping("{houseId}/review/{reviewId}")
    public ResponseEntity<Void> deleteHouseReview(@PathVariable Long houseId,
                                                  @PathVariable Long reviewId){
        houseReviewService.deleteHouseReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
