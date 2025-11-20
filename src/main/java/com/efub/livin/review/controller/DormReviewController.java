package com.efub.livin.review.controller;

import com.efub.livin.review.dto.request.DormReviewCreateRequestDto;
import com.efub.livin.review.dto.response.DormReviewDetailResponseDto;
import com.efub.livin.review.dto.response.DormReviewListResponseDto;
import com.efub.livin.review.service.DormReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dorm/review")
public class DormReviewController {

    private final DormReviewService dormReviewService;

    // 리뷰 생성
    @PostMapping
    public ResponseEntity<Long> createDormReview(
            @RequestBody DormReviewCreateRequestDto request
            ) {
        Long id = dormReviewService.createDormReview(request);
        return ResponseEntity.ok(id);
    }

    // 리뷰 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<DormReviewListResponseDto>> getDormReviewList(
            @RequestParam(required = false) String buildName,
            @RequestParam(required = false) String buildNum,
            @RequestParam(required = false) Integer minFinalRate
    ){
        List<DormReviewListResponseDto> list = dormReviewService.getDormReviewList(buildName, buildNum, minFinalRate);
        return ResponseEntity.ok(list);
    }

    //리뷰 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<DormReviewDetailResponseDto> getDormReviewDetail(
            @PathVariable Long id
    ){
        DormReviewDetailResponseDto response = dormReviewService.getDormReviewDetail(id);
        return ResponseEntity.ok(response);
    }

    //리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDormReview(@PathVariable Long id){
        dormReviewService.deleteDormReview(id);
        return ResponseEntity.noContent().build();
    }
}
