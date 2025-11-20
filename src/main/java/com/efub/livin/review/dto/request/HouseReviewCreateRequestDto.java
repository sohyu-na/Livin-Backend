package com.efub.livin.review.dto.request;

import com.efub.livin.house.domain.House;
import com.efub.livin.review.domain.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class HouseReviewCreateRequestDto {

    private FacilityRate facilityRate;
    private SoundRate soundRate;
    private AccessRate accessRate;
    private BugRate bugRate;
    private Integer finalRate;
    private String review;
    private Boolean anonym;
    private List<String> imageUrls;

    public HouseReview toEntitiy(House house){
        return HouseReview.builder()
                .house(house)
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
