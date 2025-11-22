package com.efub.livin.house.domain;

import com.efub.livin.global.domain.BaseEntity;
import com.efub.livin.house.dto.request.HouseCreateRequest;
import com.efub.livin.review.domain.HouseReview;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@Table(name = "house")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class House extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long houseId;

    private String buildingName;

    @Column(unique = true)
    private String address; // 중복된 건물이 없도록

    private String phone; // 대표 번호
    private String lon; // 경도
    private String lat; // 위도
    private String place_url; // 상세 페이지 URL
    private String docId; // 지도 자체 장소 id
    private Integer floor;
    private Boolean elevator; // 엘베
    private Boolean parking;
    private String options;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // 가격대

    @Enumerated(EnumType.STRING)
    private HouseType type; // 자취방 PRIVATE, 하숙 BOARDING

    // 리뷰 관련
    @ColumnDefault("0")
    private float rate = 0;

    // 북마크 개수
    @ColumnDefault("0")
    private int bookmarkCnt = 0;

    // 리뷰 관계 코드
    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HouseReview> reviews = new ArrayList<>();

    // csv 데이터로 필드 업데이트
    public void updateSystemInfo(int floor, boolean elevator, boolean parking) {
        this.floor = floor;
        this.elevator = elevator;
        this.parking = parking;
    }

}
