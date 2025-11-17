package com.efub.livin.review.domain;

public enum BugRate {

    NONE("없어요"),
    SOMETIMES("가끔 나와요"),
    OFTEN("자주 나와요");

    private final String label;

    BugRate(String label) {
        this.label = label;
    }

    public String getLabel() {return label;}
}
