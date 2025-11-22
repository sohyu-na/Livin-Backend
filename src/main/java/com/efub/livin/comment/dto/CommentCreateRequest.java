package com.efub.livin.comment.dto;

import com.efub.livin.comment.domain.Comment;
import com.efub.livin.review.domain.DormReview;
import com.efub.livin.review.domain.HouseReview;
import com.efub.livin.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    private String content;
    private Boolean anonymous;

    // 기숙사일 경우
    public Comment toEntity(User user, DormReview dormReview){
        return Comment.builder()
                .content(this.content)
                .anonymous(this.anonymous)
                .user(user)
                .dormReview(dormReview)
                .build();
    }

    // 자취/하숙일 경우
    public Comment toEntity(User user, HouseReview houseReview){
        return Comment.builder()
                .content(this.content)
                .anonymous(this.anonymous)
                .user(user)
                .houseReview(houseReview)
                .build();
    }
}
