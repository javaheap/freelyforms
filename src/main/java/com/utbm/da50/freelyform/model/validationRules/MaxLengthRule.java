package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

@Component
public class MaxLengthRule implements ValidationRule {

    @Override
    public void validate(Object userInput, Field field, Rule rule) throws ValidationRuleException {
        int maxLength = Integer.parseInt(rule.getValue());

        if (((String)userInput).length() > maxLength) {
            throw new ValidationRuleException("Field " + field.getLabel() + " must have a maximum length of " + maxLength);
        }
    }

    @Override
    public TypeRule getRuleType() {
        return TypeRule.MAX_LENGTH;
    }
}