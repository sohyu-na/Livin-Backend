package com.efub.livin.house.service;

import com.efub.livin.house.domain.HouseType;
import com.efub.livin.house.dto.response.NaverImageResponseDto;
import lombok.RequiredArgsConstructor;
import com.efub.livin.house.domain.Document;
import com.efub.livin.house.domain.House;
import com.efub.livin.house.dto.response.KakaoResponseDto;
import com.efub.livin.house.repository.HouseSaveRepository;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.efub.livin.house.domain.HouseType.BOARDING;
import static com.efub.livin.house.domain.HouseType.PRIVATE;

@Service
@RequiredArgsConstructor
public class HouseSyncService {

    private final KakaoApiClient kakaoApiClient;
    private final NaverApiClient naverApiClient;
    private final HouseSaveRepository houseSaveRepository;

    private static final List<String> KEYWORDS = List.of("자취", "고시원", "고시텔", "쉐어하우스", "하숙");

    public void syncAndSave(){

        // 키워드별로 데이터 저장
        for(String keyword : KEYWORDS){

            // 카카오 키워드별 검색 호출
            KakaoResponseDto response = kakaoApiClient.searchByKeyword(keyword);
            List<Document> documents = response.getDocuments();

            if (documents == null || documents.isEmpty()) {
                continue;
            }

            for(Document document : documents){

                // 이미 존재하는 집이면 넘김
                if (houseSaveRepository.existsByAddress(document.getAddress_name())){
                    continue;
                }
                HouseType type;
                String imageUrl = null;

                // 네이버 이미지 검색 호출
                NaverImageResponseDto naverResponse = naverApiClient.searchImage(document.getPlace_name());
                // Naver api 속도제한용 딜레이
                try {
                    Thread.sleep(100); // 0.1초 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // Naver 응답이 정상이면, thumbnail URL 추출
                if (naverResponse != null && naverResponse.getItems() != null && !naverResponse.getItems().isEmpty()) {
                    imageUrl = naverResponse.getItems().get(0).getThumbnail();
                }

                if(keyword.equals("자취") || keyword.equals("고시원") || keyword.equals("고시텔")){
                    type = PRIVATE;
                } else { // 하숙
                    type = BOARDING;
                }
                // House 엔티티로 변환
                House house = House.builder()
                        .buildingName(document.getPlace_name())
                        .address(document.getAddress_name())
                        .phone(document.getPhone())
                        .lon(document.getX())
                        .lat(document.getY())
                        .docId(document.getId())
                        .type(type)
                        .imageUrl(imageUrl) // 네이버에서 가져온 이미지
                        .build();

                // DB에 저장
                houseSaveRepository.save(house);
            }


        }

    }


}
