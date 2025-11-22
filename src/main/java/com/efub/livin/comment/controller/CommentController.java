package com.efub.livin.comment.controller;

import com.efub.livin.auth.domain.CustomUserDetails;
import com.efub.livin.comment.dto.CommentCreateRequest;
import com.efub.livin.comment.dto.CommentResponse;
import com.efub.livin.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping(value = "/review/{reviewId}/comment")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId,
            @RequestBody CommentCreateRequest request) {

        CommentResponse response = commentService.registerComment(userDetails.getUser(), reviewId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 댓글 삭제
    @DeleteMapping(value = "/comment/{commentId}")
    public ResponseEntity<Void> deleteComment (
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {

        commentService.deleteComment(userDetails.getUser(), commentId);

        return ResponseEntity.noContent().build();
    }
}
