package com.efub.livin.house.service;

import com.efub.livin.bookmark.repository.BookmarkRepository;
import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.house.dto.response.Document;
import com.efub.livin.house.domain.House;
import com.efub.livin.house.dto.request.HouseCreateRequest;
import com.efub.livin.house.dto.response.HouseResponse;
import com.efub.livin.house.dto.response.KakaoResponseDto;
import com.efub.livin.house.repository.HouseRepository;
import com.efub.livin.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HouseServiceTest {

    @InjectMocks
    private HouseService houseService;

    // 가짜 객체들
    @Mock
    private HouseRepository houseRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private KakaoApiClient kakaoApiClient;


    // 테스트용 상수
    private static final Long HOUSE_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String ADDRESS = "서울 서대문구 이화여대길 52";
    private static final String X = "126.94";
    private static final String Y = "37.56";

    @Nested
    @DisplayName("자취/하숙 등록")
    class AddHouse {

        @Test
        @DisplayName("성공 - 주소를 좌표로 변환 후 저장")
        void success() {
            // given
            User user = createUser();
            HouseCreateRequest request = createRequest();

            // 카카오 API Mocking
            KakaoResponseDto mockKakaoResponse = createMockKakaoResponse();
            given(kakaoApiClient.searchAddress(anyString())).willReturn(mockKakaoResponse);

            // Repository Mocking
            House savedHouse = createHouse(); // ID가 있는 저장된 상태의 객체
            given(houseRepository.save(any(House.class))).willReturn(savedHouse);

            // when
            HouseResponse response = houseService.addHouse(request, user);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAddress()).isEqualTo(ADDRESS);

            // 카카오 API랑 db save가 호출되었는지
            verify(kakaoApiClient).searchAddress(request.getAddress());
            verify(houseRepository).save(any(House.class));
        }
    }

    @Nested
    @DisplayName("하우스 상세 정보 조회")
    class GetHouse {

        @Test
        @DisplayName("성공 - 존재하는 하우스 조회 (북마크 여부 포함)")
        void success_withBookmark() {
            // given
            User user = createUser();
            House house = createHouse();

            // Mocking
            given(houseRepository.findById(HOUSE_ID)).willReturn(Optional.of(house));
            // isBookmarked 메서드 내부 로직에 따라 Mocking
            given(bookmarkRepository.existsByUserAndHouse(user, house)).willReturn(true);

            // when
            HouseResponse response = houseService.getHouse(HOUSE_ID, user);

            // then
            assertThat(response.getHouseId()).isEqualTo(HOUSE_ID);
            assertThat(response.isBookmarked()).isTrue(); // 북마크 체크 확인
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 하우스 ID")
        void fail_houseNotFound() {
            // given
            User user = createUser();
            given(houseRepository.findById(HOUSE_ID)).willReturn(Optional.empty()); // 찾는 데이터 없음

            // when & then
            assertThatThrownBy(() -> houseService.getHouse(HOUSE_ID, user))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.HOUSE_NOT_FOUND);
        }
    }


    // 테스트용 데이터 생성
    private User createUser() {
        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "userId", USER_ID);
        return user;
    }

    private House createHouse() {
        House house = House.builder()
                .buildingName("이화하우스")
                .address(ADDRESS)
                .lon(X)
                .lat(Y)
                .build();
        ReflectionTestUtils.setField(house, "houseId", HOUSE_ID);
        return house;
    }

    private HouseCreateRequest createRequest() {
        return HouseCreateRequest.builder()
                .buildingName("이화하우스")
                .address(ADDRESS)
                .build();
    }

    // 카카오 API 응답 가짜 데이터 생성
    private KakaoResponseDto createMockKakaoResponse() {
        Document doc = Document.builder()
                .x(X)
                .y(Y)
                .place_name("이화하우스")
                .address_name(ADDRESS)
                .build();

        return KakaoResponseDto.builder()
                .documents(List.of(doc)) // 리스트에 담기
                .build();
    }

}