package com.efub.livin.comment.repository;

import com.efub.livin.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 리뷰 id로 댓글 조회
    List<Comment> findAllByDormReviewId(Long dormReviewId);
    List<Comment> findAllByHouseReviewId(Long houseReviewId);

    // 작성자의 댓글 조회
    List<Comment> findAllByUserUserId(Long userId);

}
