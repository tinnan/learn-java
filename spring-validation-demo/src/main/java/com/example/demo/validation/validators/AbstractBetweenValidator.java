package com.example.demo.validation.validators;

import com.example.demo.validation.constraints.Between;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public abstract class AbstractBetweenValidator<T> implements ConstraintValidator<Between, T> {

    protected long minValue;
    protected long maxValue;

    @Override
    public void initialize(Between constraintAnnotation) {
        this.minValue = constraintAnnotation.min();
        this.maxValue = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return isBetween(value);
    }

    protected abstract boolean isBetween(T number);
}
