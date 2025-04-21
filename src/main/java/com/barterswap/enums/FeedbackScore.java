package com.barterswap.enums;

public enum FeedbackScore {
    NEGATIVE(-1),
    NEUTRAL(0),
    POSITIVE(1);

    private final int value;

    FeedbackScore(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
} 