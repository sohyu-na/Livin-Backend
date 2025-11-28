package com.efub.livin.comment.dto.response;

import com.efub.livin.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CommentListResponse {

    private int count;
    private List<CommentResponse> comments;

    public static CommentListResponse from(List<CommentResponse> comments) {
        return CommentListResponse.builder()
                .count(comments.size())
                .comments(comments)
                .build();
    }
}
