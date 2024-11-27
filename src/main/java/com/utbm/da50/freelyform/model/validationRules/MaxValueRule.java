package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MaxValueRule implements ValidationRule{
    @Override
    public void validate(Object userInput, Field field, Rule rule) throws ValidationRuleException {
        BigDecimal value;
        if (userInput instanceof String)
            value = new BigDecimal(userInput.toString());
        else
            value = new BigDecimal((int)userInput);
        BigDecimal maxValue = new BigDecimal(rule.getValue());
        if (value.compareTo(maxValue) > 0)
            throw new ValidationRuleException("The value of the field " + field.getLabel() + " must be less than " + maxValue);
    }

    @Override
    public TypeRule getRuleType() {
        return TypeRule.MAX_VALUE;
    }
}
