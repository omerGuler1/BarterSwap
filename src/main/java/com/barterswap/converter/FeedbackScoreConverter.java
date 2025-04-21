package com.barterswap.converter;

import com.barterswap.enums.FeedbackScore;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FeedbackScoreConverter implements AttributeConverter<FeedbackScore, Integer> {
    
    @Override
    public Integer convertToDatabaseColumn(FeedbackScore score) {
        if (score == null) {
            return null;
        }
        return score.getValue();
    }

    @Override
    public FeedbackScore convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        for (FeedbackScore score : FeedbackScore.values()) {
            if (score.getValue() == value) {
                return score;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
} 