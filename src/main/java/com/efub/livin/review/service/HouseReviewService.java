package com.efub.livin.review.service;

import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.house.domain.House;
import com.efub.livin.house.repository.HouseSaveRepository;
import com.efub.livin.review.domain.HouseReview;
import com.efub.livin.review.domain.ReviewImage;
import com.efub.livin.review.dto.request.HouseReviewCreateRequestDto;
import com.efub.livin.review.dto.response.HouseReviewDetailResponseDto;
import com.efub.livin.review.dto.response.HouseReviewListResponseDto;
import com.efub.livin.review.repository.HouseReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseReviewService {
    private final HouseReviewRepository houseReviewRepository;
    private final HouseSaveRepository houseSaveRepository;

    //리뷰 생성
    @Transactional
    public Long createHouseReview(Long houseId, HouseReviewCreateRequestDto request){
        House house = houseSaveRepository.findById(houseId)
                .orElseThrow(()-> new CustomException(ErrorCode.HOUSE_NOT_FOUND));

        HouseReview review = request.toEntitiy(house);

        if(request.getImageUrls() != null){
            request.getImageUrls().forEach(url -> {
                ReviewImage image = new ReviewImage(url);
                review.addImage(image);
            });
        }

        return houseReviewRepository.save(review).getId();
    }

    //리뷰 전체 목록 조회
    @Transactional(readOnly = true)
    public List<HouseReviewListResponseDto> getHouseReviewList(Long houseId){
        List<HouseReview> review = houseReviewRepository.findByHouse_HouseIdOrderByCreatedAtDesc(houseId);
        return review.stream()
                .map(HouseReviewListResponseDto::from)
                .toList();
    }

    //리뷰 상세 조회
    @Transactional(readOnly = true)
    public HouseReviewDetailResponseDto getHouseReviewDetail(Long reviewId){
        HouseReview review = houseReviewRepository.findById(reviewId)
                .orElseThrow(()-> new CustomException(ErrorCode.HOUSE_REVIEW_NOT_FOUND));

        return HouseReviewDetailResponseDto.from(review);
    }

    //리뷰 삭제
    @Transactional
    public void deleteHouseReview(Long id){
        HouseReview review = houseReviewRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.HOUSE_REVIEW_NOT_FOUND));
        houseReviewRepository.delete(review);
    }
}
