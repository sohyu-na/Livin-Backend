package com.efub.livin.comment.service;

import com.efub.livin.comment.domain.Comment;
import com.efub.livin.comment.dto.request.CommentCreateRequest;
import com.efub.livin.comment.dto.response.CommentListResponse;
import com.efub.livin.comment.dto.response.CommentResponse;
import com.efub.livin.comment.repository.CommentRepository;
import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import com.efub.livin.house.domain.House;
import com.efub.livin.review.domain.DormReview;
import com.efub.livin.review.domain.HouseReview;
import com.efub.livin.review.repository.DormReviewRepository;
import com.efub.livin.review.repository.HouseReviewRepository;
import com.efub.livin.user.domain.User;
import com.efub.livin.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    // 테스트 대상
    @InjectMocks
    private CommentService commentService;

    // 가짜 객체
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DormReviewRepository dormReviewRepository;

    @Mock
    private HouseReviewRepository houseReviewRepository;

    // 테스트용 상수
    private static final Long USER_ID = 1L;
    private static final Long HOUSE_REVIEW_ID = 1L;
    private static final Long DORM_REVIEW_ID = 10001L;
    private static final Long COMMENT_ID = 50L;

    @Nested
    @DisplayName("댓글 작성 테스트")
    class RegisterCommentTest {

        @Test
        @DisplayName("성공 - 자취방 리뷰에 댓글 작성")
        void success_houseReviewComment(){
            // given
            User user = createUser(USER_ID);
            HouseReview houseReview = createHouseReview();
            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", false);
            Comment comment = request.toEntity(user, houseReview);

            // Mocking
            given(userRepository.existsById(USER_ID)).willReturn(true);
            given(houseReviewRepository.findById(HOUSE_REVIEW_ID)).willReturn(Optional.of(houseReview));
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            CommentResponse response = commentService.registerComment(user, HOUSE_REVIEW_ID, request);

            // then
            assertThat(response.getContent()).isEqualTo("댓글 내용");
            verify(commentRepository).save(any(Comment.class)); // save 호출 여부
        }

        @Test
        @DisplayName("성공 - 기숙사 리뷰에 댓글 작성. id >= 10000임")
        void success_dormReviewComment(){
            // given
            User user = createUser(USER_ID);
            DormReview dormReview = createDormReview();
            CommentCreateRequest request = new CommentCreateRequest("기숙사 댓글", false);
            Comment comment = request.toEntity(user, dormReview);

            given(userRepository.existsById(USER_ID)).willReturn(true);
            given(dormReviewRepository.findById(DORM_REVIEW_ID)).willReturn(Optional.of(dormReview));
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            CommentResponse response = commentService.registerComment(user, DORM_REVIEW_ID, request);

            // then
            assertThat(response.getContent()).isEqualTo("기숙사 댓글");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void fail_userNotFound(){
            // given
            User user = createUser(USER_ID);
            CommentCreateRequest request = new CommentCreateRequest("내용", false);
            given(userRepository.existsById(USER_ID)).willReturn(false); // 유저 없음

            // when & then
            assertThatThrownBy(() -> commentService.registerComment(user, HOUSE_REVIEW_ID, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }


    @Nested
    @DisplayName("댓글 조회 테스트")
    class GetComments {

        @Test
        @DisplayName("성공 - 특정 자취방 리뷰의 댓글 목록 조회")
        void success_getReviewComments() {
            // given
            User user = createUser(USER_ID);
            HouseReview houseReview = createHouseReview();

            // 테스트용 댓글 리스트 생성
            Comment comment1 = createComment(user, "댓글1");
            Comment comment2 = createComment(user, "댓글2");
            List<Comment> commentList = List.of(comment1, comment2);

            given(houseReviewRepository.existsById(HOUSE_REVIEW_ID)).willReturn(true);
            given(commentRepository.findAllByHouseReviewId(HOUSE_REVIEW_ID)).willReturn(commentList);

            // when
            CommentListResponse response = commentService.getReviewComments(user, HOUSE_REVIEW_ID);

            // then
            assertThat(response.getCount()).isEqualTo(2);
            assertThat(response.getComments().get(0).getContent()).isEqualTo("댓글1");
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteComment {

        @Test
        @DisplayName("성공 - 본인이 작성한 댓글 삭제")
        void success_delete() {
            // given
            User user = createUser(USER_ID);
            Comment comment = createComment(user, "내 댓글"); // 작성자가 user인 댓글

            given(userRepository.existsById(USER_ID)).willReturn(true);
            given(commentRepository.findById(COMMENT_ID)).willReturn(Optional.of(comment));

            // when
            commentService.deleteComment(user, COMMENT_ID);

            // then
            verify(commentRepository).delete(comment); // delete 메서드가 실행되었는지 검증
        }

        @Test
        @DisplayName("실패 - 다른 사람의 댓글 삭제 시도 (권한 없음)")
        void fail_invalidPermission() {
            // given
            User me = createUser(1L); // 나
            User otherUser = createUser(2L); // 남

            // 남이 쓴 댓글
            Comment othersComment = createComment(otherUser, "남의 댓글");

            given(userRepository.existsById(me.getUserId())).willReturn(true);
            given(commentRepository.findById(COMMENT_ID)).willReturn(Optional.of(othersComment));

            // when & then
            assertThatThrownBy(() -> commentService.deleteComment(me, COMMENT_ID))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PERMISSION);
        }
    }



    // 테스트 데이터 생성용 메서드들
    private User createUser(Long id) {
        User user = User.builder().build(); // 실제로는 Builder 패턴 사용 권장
        ReflectionTestUtils.setField(user, "userId", id);
        return user;
    }

    private HouseReview createHouseReview() {
        HouseReview review = HouseReview.builder().build();
        ReflectionTestUtils.setField(review, "id", HOUSE_REVIEW_ID);
        return review;
    }

    private DormReview createDormReview() {
        DormReview review = DormReview.builder().build();
        ReflectionTestUtils.setField(review, "id", DORM_REVIEW_ID);
        return review;
    }

    private Comment createComment(User user, String content) {
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .anonymous(false)
                .build();
        return comment;
    }


}