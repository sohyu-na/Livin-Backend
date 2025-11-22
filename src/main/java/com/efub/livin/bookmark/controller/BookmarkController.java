package com.efub.livin.bookmark.controller;

import com.efub.livin.auth.domain.CustomUserDetails;
import com.efub.livin.bookmark.service.BookmarkService;
import com.efub.livin.bookmark.dto.response.BookmarkResponse;
import com.efub.livin.house.dto.response.HouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 북마크 토글 컨트롤러
    // POST /bookmark/{houseId}
    @PostMapping(value = "/{houseId}")
    public ResponseEntity<BookmarkResponse> toggleBookmark(@PathVariable Long houseId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails){
        BookmarkResponse response = bookmarkService.toggleBookmark(houseId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    // 내 북마크 리스트 조회 컨트롤러
    // GET /bookmark/my
    @GetMapping(value = "/my")
    public ResponseEntity<List<HouseResponse>> getMyBookmark(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<HouseResponse> response = bookmarkService.getMyBookmark(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

