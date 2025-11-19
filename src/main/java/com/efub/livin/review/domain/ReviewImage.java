package com.efub.livin.review.domain;

import com.efub.livin.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //이미지 URL
    private String imagePath;

    // ★ 기숙사 리뷰 쪽 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dorm_review_id")
    private DormReview dormReview;

    // ★ 자취/하숙 리뷰 쪽 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_review_id")
    private HouseReview houseReview;

    @Builder
    public ReviewImage(String imagePath){
        this.imagePath = imagePath;
    }

    // 편의 메서드들
    public void setDormReview(DormReview dormReview) {
        this.dormReview = dormReview;
    }

    public void setHouseReview(HouseReview houseReview) {
        this.houseReview = houseReview;
    }

}
