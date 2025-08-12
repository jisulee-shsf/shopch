package com.app.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.Arrays;

public class EnumValueValidator implements ConstraintValidator<ValueOfEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValueOfEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (hasNoText(value)) {
            return true;
        }
        return isEnumConstant(value);
    }

    private boolean hasNoText(String value) {
        return !StringUtils.hasText(value);
    }

    private boolean isEnumConstant(String value) {
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumConstant.name().equalsIgnoreCase(value));
    }
}
