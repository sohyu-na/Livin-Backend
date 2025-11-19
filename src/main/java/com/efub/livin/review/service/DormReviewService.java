package com.efub.livin.review.service;

import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.review.domain.DormReview;
import com.efub.livin.review.domain.ReviewImage;
import com.efub.livin.review.dto.request.DormReviewCreateRequestDto;
import com.efub.livin.review.dto.response.DormReviewDetailResponseDto;
import com.efub.livin.review.dto.response.DormReviewListResponseDto;
import com.efub.livin.review.repository.DormReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DormReviewService {

    private final DormReviewRepository dormReviewRepository;

    // 리뷰 생성
    @Transactional
    public long createDormReview(DormReviewCreateRequestDto request){
        DormReview review = request.toEntity();

        //이미지가 있다면 엔티티와 연결
        if(request.getImageUrls() != null) {
            request.getImageUrls().forEach(url -> {
                ReviewImage images = new ReviewImage(url);
                review.addImage(images);
            });
        }

        DormReview saved = dormReviewRepository.save(review);

        return saved.getId();
    }

    // 리뷰 전체 목록 조회
    @Transactional(readOnly = true)
    public List<DormReviewListResponseDto> getDormReviewList(String buildName,
                                                             String buildNum,
                                                             Integer minFinalRate){
        List<DormReview> reviews = dormReviewRepository.searchDormReviews(buildName, buildNum, minFinalRate);

        return reviews.stream()
                .map(DormReviewListResponseDto::from)
                .toList();
    }

    //리뷰 상세 조회
    @Transactional(readOnly = true)
    public DormReviewDetailResponseDto getDormReviewDetail(Long id){
        DormReview review = dormReviewRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.DORM_REVIEW_NOT_FOUND));

        return DormReviewDetailResponseDto.from(review);

    }

    //리뷰 삭제
    @Transactional
    public void deleteDormReview(Long id){
        DormReview review = dormReviewRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.DORM_REVIEW_NOT_FOUND));

        dormReviewRepository.delete(review);

    }
}
