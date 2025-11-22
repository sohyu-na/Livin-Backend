package com.efub.livin.bookmark.domain;

import com.efub.livin.house.domain.House;
import com.efub.livin.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.awt.print.Book;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "bookmark")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "houseId", nullable = false)
    private House house;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Bookmark (User user, House house){
        this.user = user;
        this.house = house;
    }
}
