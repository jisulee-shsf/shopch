package com.app.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {

    private List<String> enumValues;

    @Override
    public void initialize(ValueOfEnum constraintAnnotation) {
        enumValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.hasText(value)) {
            return enumValues.contains(value.toUpperCase());
        }
        return true;
    }
}
