package com.efub.livin.house.domain;

import com.efub.livin.house.dto.request.HouseCreateRequest;
import com.efub.livin.review.domain.HouseReview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "house")
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long houseId;

    private String buildingName;
    private String address;
    private String phone; // 대표 번호
    private String lon; // 경도
    private String lat; // 위도
    private String place_url; // 상세 페이지 URL
    private String docId; // 지도 자체 장소 id
    private Integer floor;
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

    // Document dto -> House 엔티티로 변환
    public static House from(Document dto, HouseType type, String imageUrl){
        House house = new House();
        house.buildingName = dto.getPlace_name();
        house.address = dto.getAddress_name();
        house.phone = dto.getPhone();
        house.lon = dto.getX();
        house.lat = dto.getY();
        house.docId = dto.getId();
        house.type = type;
        house.imageUrl = imageUrl;
        return house;
    }

    // 새 자취/하숙 데이터 저장용
    public static House create(HouseCreateRequest request, String lon, String lat){
        House house = new House();
        house.type = request.getType();
        house.buildingName = request.getBuildingName();
        house.address = request.getAddress();
        house.options = request.getOptions();
        house.parking = request.getParking();
        house.imageUrl = request.getImageUrl();
        house.lon = lon;
        house.lat = lat;
        return house;
    }

}
