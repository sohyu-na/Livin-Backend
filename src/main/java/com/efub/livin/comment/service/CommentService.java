package com.efub.livin.comment.service;

import com.efub.livin.comment.domain.Comment;
import com.efub.livin.comment.dto.CommentCreateRequest;
import com.efub.livin.comment.dto.CommentResponse;
import com.efub.livin.comment.repository.CommentRepository;
import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.review.domain.DormReview;
import com.efub.livin.review.domain.HouseReview;
import com.efub.livin.review.repository.DormReviewRepository;
import com.efub.livin.review.repository.HouseReviewRepository;
import com.efub.livin.user.domain.User;
import com.efub.livin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_NOT_FOUND));

            comment = request.toEntity(user, houseReview);
        }

        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    // 댓글 삭제
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
