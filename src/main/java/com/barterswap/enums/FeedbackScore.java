package com.barterswap.enums;

public enum FeedbackScore {
    VERY_BAD(-1),
    NEUTRAL(0),
    GOOD(1);

    private final int value;
    FeedbackScore(int value) { this.value = value; }
    public int getValue() { return value; }
} 