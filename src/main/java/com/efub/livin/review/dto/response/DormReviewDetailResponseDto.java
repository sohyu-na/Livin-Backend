package com.efub.livin.review.dto.response;

import com.efub.livin.review.domain.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DormReviewDetailResponseDto {

    private Long id;
    private String buildName;
    private String buildNum;
    private String roomPeople;
    private String review;
    private Integer finalRate;
    private FacilityRate facilityRate;
    private SoundRate soundRate;
    private BugRate bugRate;
    private AccessRate accessRate;
    private List<String> imageUrls;

    public static DormReviewDetailResponseDto from(DormReview review) {
        return DormReviewDetailResponseDto.builder()
                .id(review.getId())
                .buildName(review.getBuildName())
                .buildNum(review.getBuildNum())
                .roomPeople(review.getRoomPeople())
                .review(review.getReview())
                .finalRate(review.getFinalRate())
                .facilityRate(review.getFacilityRate())
                .accessRate(review.getAccessRate())
                .soundRate(review.getSoundRate())
                .bugRate(review.getBugRate())
                .imageUrls(
                        review.getImages().stream()
                                .map(ReviewImage::getImagePath)
                                .toList()
                )
                .build();
    }
}
