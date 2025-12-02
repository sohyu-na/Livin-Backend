package com.efub.livin.house.service;

import com.efub.livin.house.domain.House;
import com.efub.livin.house.dto.response.Document;
import com.efub.livin.house.dto.response.Item;
import com.efub.livin.house.dto.response.KakaoResponseDto;
import com.efub.livin.house.dto.response.NaverImageResponseDto;
import com.efub.livin.house.repository.HouseSaveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseSyncServiceTest {

    @InjectMocks
    private HouseSyncService houseSyncService;

    // 가짜 객체
    @Mock
    private KakaoApiClient kakaoApiClient;

    @Mock
    private NaverApiClient naverApiClient;

    @Mock
    private HouseSaveRepository houseSaveRepository;


    @Test
    @DisplayName("성공 - 키워드별로 API를 호출하고, 중복이 아닌 데이터는 DB에 저장")
    void syncAndSave_Success() {
        // given
        // 카카오 API Mock 데이터
        Document mockDoc = Document.builder()
                .place_name("테스트 고시원")
                .address_name("서울 서대문구 대현동")
                .x("126.94").y("37.56")
                .id("123456")
                .phone("02-123-4567")
                .build();

        KakaoResponseDto kakaoResponse = KakaoResponseDto.builder()
                .documents(List.of(mockDoc))
                .build();

        // 네이버 API Mock 데이터 (이미지 1개)
        Item mockItem = Item.builder()
                .thumbnail("http://test-image.jpg")
                .build();

        NaverImageResponseDto naverResponse = NaverImageResponseDto.builder()
                .items(List.of(mockItem))
                .build();

        // Mocking 동작
        // 어떤 키워드로 검색하든 위에서 만든 카카오 응답을 리턴
        given(kakaoApiClient.searchByKeyword(anyString())).willReturn(kakaoResponse);

        // 주소 중복 검사 (db에 없는 주소로 가정)
        given(houseSaveRepository.existsByAddress(anyString())).willReturn(false);

        // 네이버 이미지 검색 시 위에서 만든 네이버 응답 리턴
        given(naverApiClient.searchImage(anyString())).willReturn(naverResponse);

        // when
        houseSyncService.syncAndSave();

        // then
        // 저장 메서드가 총 몇 번 호출되었는지 검증
        // keywords 리스트 크기만큼(5개) 저장돼야 함
        verify(houseSaveRepository, times(5)).save(any(House.class));

        // 저장된 데이터의 내용 검증
        ArgumentCaptor<House> houseCaptor = ArgumentCaptor.forClass(House.class);
        verify(houseSaveRepository, atLeastOnce()).save(houseCaptor.capture());

        House savedHouse = houseCaptor.getValue(); // 마지막에 캡처된 객체 확인
        assertThat(savedHouse.getBuildingName()).isEqualTo("테스트 고시원");
        assertThat(savedHouse.getImageUrl()).isEqualTo("http://test-image.jpg");
    }

    @Test
    @DisplayName("성공 - 이미 DB에 존재하는 주소라면 저장을 건너뜀")
    void syncAndSave_SkipDuplicate() {
        // given
        Document mockDoc = Document.builder()
                .place_name("이미 있는 고시원")
                .address_name("서울 마포구")
                .build();

        KakaoResponseDto kakaoResponse = KakaoResponseDto.builder()
                .documents(List.of(mockDoc))
                .build();

        given(kakaoApiClient.searchByKeyword(anyString())).willReturn(kakaoResponse);

        // 이미 존재하는 주소라고 설정
        given(houseSaveRepository.existsByAddress(anyString())).willReturn(true);

        // when
        houseSyncService.syncAndSave();

        // then
        // save가 단 한 번도 호출되지 않아야 함
        verify(houseSaveRepository, never()).save(any(House.class));

        // 네이버 api도 호출할 필요가 없으니 호출 안 됐는지 확인
        verify(naverApiClient, never()).searchImage(anyString());
    }

    @Test
    @DisplayName("성공 - 카카오 검색 결과가 없으면 건너뜀")
    void syncAndSave_EmptyResponse() {
        // given
        // 빈 리스트 리턴
        KakaoResponseDto emptyResponse = KakaoResponseDto.builder()
                .documents(Collections.emptyList())
                .build();

        given(kakaoApiClient.searchByKeyword(anyString())).willReturn(emptyResponse);

        // when
        houseSyncService.syncAndSave();

        // then
        verify(houseSaveRepository, never()).save(any(House.class));
    }
}