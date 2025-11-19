package com.efub.livin.review.repository;

import com.efub.livin.review.domain.DormReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DormReviewRepository extends JpaRepository<DormReview, Long> {

    @Query("""
        SELECT d
        FROM DormReview d
        WHERE (:buildName IS NULL OR d.buildName LIKE CONCAT('%', :buildName, '%'))
          AND (:buildNum IS NULL OR d.buildNum = :buildNum)
          AND (:minFinalRate IS NULL OR d.finalRate >= :minFinalRate)
        ORDER BY d.createdAt DESC
    """)
    List<DormReview> searchDormReviews(
            @Param("buildName") String buildName,
            @Param("buildNum") String buildNum,
            @Param("minFinalRate") Integer minFinalRate
    );
}
