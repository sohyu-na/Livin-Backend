package com.efub.livin.review.domain;

import com.efub.livin.global.domain.BaseEntity;
import com.efub.livin.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DormReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기숙사 이름
    @Column(nullable = false)
    private String buildName;

    //기숙사 동
    @Column(nullable = false)
    private String buildNum;

    //몇 인실
    @Column(nullable = false)
    private String roomPeople;

//    //FK : user_id
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", updatable = false, nullable = false)
//    private User user;

    // 시설 평가
    @Enumerated(EnumType.STRING)
    @Column(name = "facility_rate", nullable = false)
    private FacilityRate facilityRate;

    // 접근성 평가
    @Enumerated(EnumType.STRING)
    @Column(name = "access_rate", nullable = false)
    private AccessRate accessRate;

    // 방음 평가
    @Enumerated(EnumType.STRING)
    @Column(name = "sound_rate", nullable = false)
    private SoundRate soundRate;

    // 벌레 평가
    @Enumerated(EnumType.STRING)
    @Column(name = "bug_rate", nullable = false)
    private BugRate bugRate;

    // 종합 점수 (평균값 또는 별도 계산 값)
    @Column(name = "final_rate", nullable = false)
    private Integer finalRate;

    //후기
    @Column(nullable = false)
    private String review;

    // 익명 여부
    @Column(nullable = false)
    private Boolean anonym;

    //리뷰 이미지 리스트
    @OneToMany(mappedBy = "dormReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();

    @Builder
    public DormReview(
            String buildName,
            String buildNum,
            String roomPeople,
            FacilityRate facilityRate,
            AccessRate accessRate,
            SoundRate soundRate,
            BugRate bugRate,
            Integer finalRate,
            String review,
            Boolean anonym
    ) {
        this.buildName = buildName;
        this.buildNum = buildNum;
        this.roomPeople = roomPeople;
        this.facilityRate = facilityRate;
        this.accessRate = accessRate;
        this.soundRate = soundRate;
        this.bugRate = bugRate;
        this.finalRate = finalRate;
        this.review = review;
        this.anonym = anonym;
    }

    //이미지 추가하는 메서드
    public void addImage(ReviewImage image) {
        images.add(image);
        image.setDormReview(this);
    }

}
