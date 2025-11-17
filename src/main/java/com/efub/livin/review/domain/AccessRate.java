package com.efub.livin.review.domain;

public enum AccessRate {
        BAD("나빠요"),
        NORMAL("보통이에요"),
        GOOD("좋아요");

        private final String label;
        AccessRate(String label) { this.label = label; }
        public String getLabel() { return label; }
}
