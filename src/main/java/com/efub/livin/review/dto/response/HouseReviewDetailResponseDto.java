package com.efub.livin.review.dto.response;

import com.efub.livin.review.domain.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class HouseReviewDetailResponseDto {

    private Long id;
    private String houseName;
    private Integer finalRate;
    private LocalDateTime createdAt;
    private FacilityRate facilityRate;
    private SoundRate soundRate;
    private BugRate bugRate;
    private AccessRate accessRate;
    private String review;
    private List<String> imageUrls;

    public static HouseReviewDetailResponseDto from(HouseReview entity) {
        return HouseReviewDetailResponseDto.builder()
                .id(entity.getId())

                // ⭐ Review → House 엔티티 통해 건물 정보 가져오기
                .houseName(entity.getHouse().getBuildingName())

                .finalRate(entity.getFinalRate())
                .createdAt(entity.getCreatedAt())

                .facilityRate(entity.getFacilityRate())
                .accessRate(entity.getAccessRate())
                .soundRate(entity.getSoundRate())
                .bugRate(entity.getBugRate())

                .review(entity.getReview())

                .imageUrls(
                        entity.getImages().stream()
                                .map(ReviewImage::getImagePath)
                                .toList()
                )

                .build();
}
}
