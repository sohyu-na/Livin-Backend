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
import com.efub.livin.user.domain.User;
import com.efub.livin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseReviewService {
    private final HouseReviewRepository houseReviewRepository;
    private final HouseSaveRepository houseSaveRepository;
    private final UserRepository userRepository;

    //리뷰 생성
    @Transactional
    public Long createHouseReview(Long houseId, HouseReviewCreateRequestDto request, Long loginUserId){
        House house = houseSaveRepository.findById(houseId)
                .orElseThrow(()-> new CustomException(ErrorCode.HOUSE_NOT_FOUND));

        User user = userRepository.findById(loginUserId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        HouseReview review = request.toEntitiy(house, user);

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
    public void deleteHouseReview(Long id, Long loginUserId){
        HouseReview review = houseReviewRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.HOUSE_REVIEW_NOT_FOUND));

        if(!review.getUser().getUserId().equals(loginUserId)){
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        houseReviewRepository.delete(review);
    }
}
