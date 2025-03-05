package com.app.global.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NumberClassifier {

    public String classifyNumber(int num) {
        return num >= 0 ? "PositiveOrZero" : "Negative";
    }
}
