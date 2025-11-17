package com.efub.livin.house.controller;

import com.efub.livin.house.service.HouseSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class HouseSaveController {

    private final HouseSaveService houseSaveService;

    @PostMapping(value = "/sync")
    public ResponseEntity<String> syncAndSave() {
        try {
            houseSaveService.syncAndSave();
            return ResponseEntity.ok("데이터 동기화 작업 성공");

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body("동기화 작업 중 오류 발생: " + e.getMessage());
        }

    }
}
