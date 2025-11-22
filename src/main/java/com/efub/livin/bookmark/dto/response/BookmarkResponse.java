package com.efub.livin.bookmark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkResponse {
    private boolean bookmarked; // true면 북마크 됨
    private Long bookmarkId;
}
