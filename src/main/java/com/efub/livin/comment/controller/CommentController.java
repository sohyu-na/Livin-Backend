package com.efub.livin.comment.controller;

import com.efub.livin.auth.domain.CustomUserDetails;
import com.efub.livin.comment.dto.request.CommentCreateRequest;
import com.efub.livin.comment.dto.response.CommentListResponse;
import com.efub.livin.comment.dto.response.CommentResponse;
import com.efub.livin.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 댓글 조회(리뷰에 대한)
    @GetMapping(value = "/review/{reviewId}/comment")
    public ResponseEntity<CommentListResponse> getComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {
        CommentListResponse response = commentService.getReviewComments(userDetails.getUser(), reviewId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 내가 쓴 댓글 조회
    @GetMapping(value = "/comment/me")
    public ResponseEntity<CommentListResponse> getMyComment(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentListResponse response = commentService.getMyComments(userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(response);
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
