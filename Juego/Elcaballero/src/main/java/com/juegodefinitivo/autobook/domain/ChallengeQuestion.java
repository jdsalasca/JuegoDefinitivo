package com.juegodefinitivo.autobook.domain;

import java.util.List;

public record ChallengeQuestion(String prompt, List<String> options, int correctOptionIndex) {

    public boolean isCorrect(int optionIndex) {
        return optionIndex == correctOptionIndex;
    }
}
