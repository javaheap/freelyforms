package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

@Component
public class MinLengthRule implements ValidationRule {

    @Override
    public void validate(Object userInput, Field field, Rule rule) {
        int minLength = Integer.parseInt(rule.getValue());
        if (((String)userInput).length() < minLength) {
            throw new IllegalArgumentException("The field " + field.getLabel() + " must have at least " + minLength + " characters.");
        }
    }

    @Override
    public TypeRule getRuleType() {
        return TypeRule.MIN_LENGTH;
    }
}
