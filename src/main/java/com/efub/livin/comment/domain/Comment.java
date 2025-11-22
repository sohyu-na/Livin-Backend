package com.efub.livin.comment.domain;

import com.efub.livin.global.domain.BaseEntity;
import com.efub.livin.review.domain.DormReview;
import com.efub.livin.review.domain.HouseReview;
import com.efub.livin.user.domain.User;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import lombok.*;
import software.amazon.awssdk.services.s3.endpoints.internal.BooleanEqualsFn;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue
    private Long commentId;

    private String content;
    private Boolean anonymous; // true면 익명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_review_id", nullable = true)
    private HouseReview houseReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dorm_review_id", nullable = true)
    private DormReview dormReview;

}
