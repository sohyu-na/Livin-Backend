package com.efub.livin.comment.service;

import com.efub.livin.comment.domain.Comment;
import com.efub.livin.comment.dto.request.CommentCreateRequest;
import com.efub.livin.comment.dto.response.CommentListResponse;
import com.efub.livin.comment.dto.response.CommentResponse;
import com.efub.livin.comment.repository.CommentRepository;
import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.house.dto.response.HouseResponse;
import com.efub.livin.review.domain.DormReview;
import com.efub.livin.review.domain.HouseReview;
import com.efub.livin.review.repository.DormReviewRepository;
import com.efub.livin.review.repository.HouseReviewRepository;
import com.efub.livin.user.domain.User;
import com.efub.livin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final DormReviewRepository dormReviewRepository;
    private final HouseReviewRepository houseReviewRepository;
    private final CommentRepository commentRepository;

    // 기숙사 리뷰 시작 범위
    private static final Long DORM_REVIEW_START_ID = 10000L;

    // 댓글 달기
    @Transactional
    public CommentResponse registerComment(User user, Long reviewId, CommentCreateRequest request){

        // 유저 유효 검사
        Long userId = user.getUserId();
        if (!userRepository.existsById(user.getUserId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Comment comment;

        // 기숙사 리뷰의 댓글
        if (reviewId >= DORM_REVIEW_START_ID){
            DormReview dormReview = dormReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new CustomException(ErrorCode.DORM_REVIEW_NOT_FOUND));

            comment = request.toEntity(user, dormReview);
        } else {
            // 자취/하숙 리뷰의 댓글
            HouseReview houseReview = houseReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_REVIEW_NOT_FOUND));

            comment = request.toEntity(user, houseReview);
        }

        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    // 리뷰의 댓글 조회
    @Transactional(readOnly = true)
    public CommentListResponse getReviewComments(User user, Long reviewId){

        // 기숙사 리뷰에 대한 댓글
        List<Comment> comments;
        if (reviewId >= DORM_REVIEW_START_ID) {
            // 기숙사 리뷰 유효 검사
            if (!dormReviewRepository.existsById(reviewId)) {
                throw new CustomException(ErrorCode.DORM_REVIEW_NOT_FOUND);
            }

            comments = commentRepository.findAllByDormReviewId(reviewId);

        } else { // 자취방 리뷰에 대한 댓글
            // 자취방 리뷰 유효 검사
            if (!houseReviewRepository.existsById(reviewId)) {
                throw new CustomException(ErrorCode.HOUSE_REVIEW_NOT_FOUND);
            }

            comments = commentRepository.findAllByHouseReviewId(reviewId);
        }

        // 댓글 리스트 생성
        List<CommentResponse> responses = comments.stream()
                .map(CommentResponse::from)
                .toList();

        return CommentListResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public CommentListResponse getMyComments(User user){
        // 유저 유효 검사
        Long userId = user.getUserId();
        if (!userRepository.existsById(user.getUserId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 댓글 검색
        List<Comment> comments = commentRepository.findAllByUserUserId(userId);

        // 댓글 리스트 생성
        List<CommentResponse> responses = comments.stream()
                .map(CommentResponse::from)
                .toList();

        return CommentListResponse.from(responses);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(User user, Long commentId) {

        // 유저 유효 검사
        Long userId = user.getUserId();
        if (!userRepository.existsById(user.getUserId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 댓글 유효 검사
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 사용자가 작성한 댓글이 맞는지 검사
        if (!userId.equals(comment.getUser().getUserId())) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        commentRepository.delete(comment);

    }
}
