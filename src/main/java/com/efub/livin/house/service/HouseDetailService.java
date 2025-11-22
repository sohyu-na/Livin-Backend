package com.efub.livin.house.service;

import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.house.domain.House;
import com.efub.livin.house.repository.HouseSaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HouseDetailService {

    private final HouseSaveRepository houseRepository;

    // 환경변수 여러 파일 경로
    @Value("${house.data.csv-paths}")
    private List<String> csvPaths;

    // House의 상세 정보(엘베, 주차 여부, 층 수) 받아오기
    @Transactional
    public void updateDetailsFromCsv() {
        // DB에 저장된 집들을 다 가져옴
        List<House> houses = houseRepository.findAll();

        // 검색 속도를 위해 Map으로 변환 (Key: 정제된 주소)
        Map<String, House> houseMap = mapHousesByAddress(houses);

        log.info("상세 정보 업데이트 대상: {}건", houseMap.size());

        // 파일 목록 순회
        for (String path : csvPaths) {
            processSingleCsvFile(path.trim(), houseMap);
        }

        log.info("모든 CSV 상세 정보 업데이트 완료");
    }

    // 파일 처리하며 자취/하숙 검색
    private void processSingleCsvFile(String filePath, Map<String, House> houseMap) {
        log.info("파일 처리 시작: {}", filePath);

        try (BufferedReader br = openCsvReader(filePath)) {
            String line;
            br.readLine(); // 첫 줄 스킵

            while ((line = br.readLine()) != null) {
                processCsvLine(line, houseMap);
            }
        } catch (IOException e) {
            log.error("파일 처리 실패: {}", filePath);
            throw new CustomException(ErrorCode.FILE_READ_ERROR);
        }
    }

    // 각 줄에서 찾기
    private void processCsvLine(String line, Map<String, House> houseMap) {
        try {

            // 첫 번째 쉼표 위치 찾기
            int firstCommaIndex = line.indexOf(',');
            if (firstCommaIndex == -1) return;

            // 주소만 빼내기
            String rawAddress = line.substring(0, firstCommaIndex);

            // 주소 정제 (따옴표 제거 + 정규화)
            String addressKey = normalizeAddress(removeQuotes(rawAddress));

            // DB에 없는 주소면 버림
            if (!houseMap.containsKey(addressKey)) {
                return;
            }

            // 따옴표 안의 쉼표는 무시하고 자름
            String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // 데이터 유효성 검사
            if (data.length < 50) return;

            // 주소 매칭
            House target = houseMap.get(addressKey);

            // 데이터 파싱
            // 43번(지상층수, AR), 45번(승용승강기), 46번(비상용승강기), 50~56번(주차장)
            int floor = parse(data[43]);

            // 엘리베이터 = 승용 + 비상용
            boolean elevator = (parse(data[45]) + parse(data[46])) > 0;

            // 주차 = 옥내/옥외 기계식 + 옥내/옥외 자주식
            boolean parking = (parse(data[50]) + parse(data[52]) +
                    parse(data[54]) + parse(data[56])) > 0;

            // 엔티티 업데이트
            target.updateSystemInfo(floor, elevator, parking);

        } catch (Exception e) {
            // 한 줄 건너뛰고 계속 진행
            log.debug("라인 파싱 실패, 건너뜀: {}", line);
        }
    }

    // csv reader 정의
    private BufferedReader openCsvReader(String filePath) {
        try {
            // 공공데이터는 EUC-KR 인코딩 사용
            return new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    // 주소 정규화
    private String normalizeAddress(String address) {
        if (address == null) return "";
        return address.replaceAll("특별시", "")
                .replaceAll("광역시", "")
                .replaceAll("경기도", "")
                .replaceAll(" ", "")    // 공백 제거
                .replaceAll("번지", "")  // 번지 제거
                .trim();
    }

    // DB 데이터를 Map으로 변환
    private Map<String, House> mapHousesByAddress(List<House> houses) {
        Map<String, House> map = new HashMap<>();
        for (House h : houses) {
            map.put(normalizeAddress(h.getAddress()), h);
        }
        return map;
    }

    // 공백이나 에러 시 0 반환
    private int parse(String val) {
        try {
            if (val == null || val.trim().isEmpty()) return 0;
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 앞뒤에 붙은 쌍따옴표 제거
    private String removeQuotes(String str) {
        if (str == null) return "";

        // 문자열 시작이 " 이면 제거
        // 문자열 끝이 " 이면 제거
        return str.replaceAll("^\"|\"$", "").trim();
    }
}
