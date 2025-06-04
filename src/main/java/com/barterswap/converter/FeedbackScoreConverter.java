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
        
        // Handle legacy values during migration
        if (value == -1) { // Old NEGATIVE
            return FeedbackScore.ONE_STAR;
        } else if (value == 0) { // Old NEUTRAL
            return FeedbackScore.THREE_STARS;
        } else if (value == 1) { // Old POSITIVE
            return FeedbackScore.FIVE_STARS;
        }
        
        // Handle new star rating values
        for (FeedbackScore score : FeedbackScore.values()) {
            if (score.getValue() == value) {
                return score;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
} 