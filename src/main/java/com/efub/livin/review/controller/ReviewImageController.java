package com.efub.livin.review.controller;

import com.efub.livin.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.IOException;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewImageController {

    private final S3Service s3Service;

    //리뷰 이미지 업로드
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadReviewImages(
            @RequestPart("images") List<MultipartFile> images
    ){
        List<String> imageUrls = images.stream()
                .map(file -> {
                    try {
                        return s3Service.upload(file, "review-images");
                    } catch (IOException e) {
                        throw new RuntimeException("이미지 업로드 실패", e);
                    }
                })
                .toList();

        return ResponseEntity.ok(imageUrls);
    }
}
