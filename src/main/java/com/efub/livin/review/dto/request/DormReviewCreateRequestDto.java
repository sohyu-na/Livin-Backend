package com.efub.livin.review.dto.request;

import com.efub.livin.review.domain.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DormReviewCreateRequestDto {
    private String buildName;
    private String buildNum;
    private String roomPeople;

    private FacilityRate facilityRate;
    private AccessRate accessRate;
    private SoundRate soundRate;
    private BugRate bugRate;

    private Integer finalRate;
    private String review;
    private Boolean anonym;

    // ★ 이미지 URL 여러 개 받기
    private List<String> imageUrls;

    public DormReview toEntity(){
        return DormReview.builder()
            .buildName(buildName)
                .buildNum(buildNum)
                .roomPeople(roomPeople)
                .facilityRate(facilityRate)
                .accessRate(accessRate)
                .soundRate(soundRate)
                .bugRate(bugRate)
                .finalRate(finalRate)
                .review(review)
                .anonym(anonym)
                .build();
    }
}
