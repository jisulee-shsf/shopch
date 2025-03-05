package com.app.global.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NumberClassifierTest {

    private final NumberClassifier classifier = new NumberClassifier();

    @Test
    public void classifyNumber() {
        assertThat(classifier.classifyNumber(1)).isEqualTo("PositiveOrZero");
    }
}
