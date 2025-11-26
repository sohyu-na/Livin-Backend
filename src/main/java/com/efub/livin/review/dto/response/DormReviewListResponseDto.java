package com.efub.livin.review.dto.response;

import com.efub.livin.review.domain.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DormReviewListResponseDto {

    private Long id;
    private String buildName;
    private String buildNum;
    private String roomPeople;
    private Integer finalrate;
    private FacilityRate facilityRate;
    private SoundRate soundRate;
    private BugRate bugRate;
    private AccessRate accessRate;
    private List<String> imageUrls;
    private String nickname;

    public static DormReviewListResponseDto from(DormReview review){
        return DormReviewListResponseDto.builder()
                .id(review.getId())
                .buildName(review.getBuildName())
                .buildNum(review.getBuildNum())
                .roomPeople(review.getRoomPeople())
                .finalrate(review.getFinalRate())
                .facilityRate(review.getFacilityRate())
                .soundRate(review.getSoundRate())
                .bugRate(review.getBugRate())
                .accessRate(review.getAccessRate())
                .nickname(review.getAnonym() != null && review.getAnonym()
                        ? "익명"
                        : review.getUser().getNickname())
                .imageUrls(review.getImages().stream()
                        .map(img-> img.getImagePath())
                        .toList())
                .build();
    }

}
