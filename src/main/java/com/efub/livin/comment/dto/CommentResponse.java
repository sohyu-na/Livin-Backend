package com.efub.livin.comment.dto;

import com.efub.livin.comment.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private Long userId;
    private String content;
    private String nickname;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        String nickname = comment.getUser().getNickname();
        Long userId = comment.getUser().getUserId();

        String disPlayNickname = comment.getAnonymous() ? "익명" : nickname;
        Long disPlayUserId = comment.getAnonymous() ? null : userId;

        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .userId(disPlayUserId)
                .content(comment.getContent())
                .nickname(disPlayNickname)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
