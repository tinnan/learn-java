package com.example.demo.validation.validators;

public class BetweenValidatorForInteger extends AbstractBetweenValidator<Integer> {
    @Override
    protected boolean isBetween(Integer number) {
        return minValue <= number && number <= maxValue;
    }
}
