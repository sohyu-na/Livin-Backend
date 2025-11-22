package com.efub.livin.comment.repository;

import com.efub.livin.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
