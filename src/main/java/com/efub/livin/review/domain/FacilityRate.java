package com.efub.livin.review.domain;

public enum FacilityRate {
    DIRTY("더러워요"),
    NORMAL("보통이에요"),
    CLEAN("깨끗해요");

    private final String label;

    FacilityRate(String label) { this.label = label; }

    public String getLabel() { return label; }
}
