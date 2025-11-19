package com.efub.livin.review.dto.response;

import com.efub.livin.review.domain.*;
import com.efub.livin.review.service.HouseReviewService;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class HouseReviewListResponseDto {

    private Long id;
    private LocalDateTime createdAt;
    private Integer finalRate;
    private FacilityRate facilityRate;
    private SoundRate soundRate;
    private BugRate bugRate;
    private AccessRate accessRate;
    private List<String> imageUrls;

    public static HouseReviewListResponseDto from(HouseReview entity){
        return HouseReviewListResponseDto.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt()) // BaseEntity에 있다고 가정
                .finalRate(entity.getFinalRate())
                .facilityRate(entity.getFacilityRate())
                .accessRate(entity.getAccessRate())
                .soundRate(entity.getSoundRate())
                .bugRate(entity.getBugRate())
                .imageUrls(
                        entity.getImages().stream()
                                .map(ReviewImage::getImagePath)
                                .toList()
                )
                .build();
    }
}
