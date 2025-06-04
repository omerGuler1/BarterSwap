package com.barterswap.enums;

public enum FeedbackScore {
    ONE_STAR(1),
    TWO_STARS(2),
    THREE_STARS(3),
    FOUR_STARS(4),
    FIVE_STARS(5);

    private final int value;

    FeedbackScore(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
} 