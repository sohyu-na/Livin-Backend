package com.efub.livin.house.service;

import com.efub.livin.house.domain.House;
import com.efub.livin.house.repository.HouseSaveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HouseDetailServiceTest {

    @InjectMocks
    private HouseDetailService houseDetailService;

    @Mock
    private HouseSaveRepository houseRepository;

    // 테스트 끝나면 삭제되는 임시 폴더
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("성공 - CSV 파일을 읽어 층수, 승강기, 주차 정보를 업데이트")
    void updateDetailsFromCsv_Success() throws IOException {
        // given
        String address = "서울 서대문구 대현동 11-1";
        House house = createHouse(address);

        // Repository가 위 집을 리턴하도록 Mocking
        given(houseRepository.findAll()).willReturn(List.of(house));

        // 가짜 CSV 파일 생성
        File csvFile = tempDir.resolve("test_data.csv").toFile();
        createMockCsvFile(csvFile, address);

        // 파일 경로
        ReflectionTestUtils.setField(houseDetailService, "csvPaths", List.of(csvFile.getAbsolutePath()));

        // when
        houseDetailService.updateDetailsFromCsv();

        // then
        // 값이 CSV 데이터대로 잘 들어갔는지 확인
        assertThat(house.getFloor()).isEqualTo(5);
        assertThat(house.getElevator()).isTrue();
        assertThat(house.getParking()).isTrue();
    }


    // 테스트용 데이터 생성
    private House createHouse(String address) {
        House house = House.builder()
                .address(address)
                .buildingName("테스트 빌라")
                .build();

        return house;
    }

    // 가짜 CSV 파일 만들기
    private void createMockCsvFile(File file, String address) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // 헤더
            writer.write("순번,대지위치,...\n");

            // 데이터 작성
            StringBuilder sb = new StringBuilder();
            sb.append(address).append(","); // [0] 주소

            // 1 ~ 42 까지 빈 값 채우기
            for (int i = 1; i < 43; i++) sb.append(",");

            sb.append("5,"); // 43 - AR: 지상층수 (5층)
            sb.append(",");  // 44
            sb.append("1,"); // 45 - AT: 승용승강기 (1대)
            sb.append("0,"); // 46 - AU: 비상용승강기

            // 47 ~ 49 빈 값
            sb.append(",,,");

            sb.append("1,"); // 50 - AY: 옥내기계식 (1대)

            // 51 ~ 56 나머지 주차 필드 (빈 값)
            for (int i = 51; i <= 60; i++) sb.append(",");

            sb.append("\n");

            writer.write(sb.toString());
        }
    }
}