package com.efub.livin.user.domain;

import com.efub.livin.global.domain.BaseEntity;
import com.efub.livin.global.exception.CustomException;
import com.efub.livin.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String school;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    public void changeNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new CustomException(ErrorCode.NICKNAME_BLANK);
        }
        this.nickname = nickname;
    }
}
