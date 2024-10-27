package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MinValueRule implements ValidationRule{
    @Override
    public void validate(Object userInput, Field field, Rule rule) throws ValidationRuleException {
        BigDecimal value;
        if (userInput instanceof String)
            value = new BigDecimal(userInput.toString());
        else
            value = new BigDecimal((int)userInput);

        BigDecimal ruleValue = new BigDecimal(rule.getValue());

        if(value.compareTo(ruleValue) < 0)
            throw new ValidationRuleException("The value must be greater than or equal to " + ruleValue + " for the field " + field.getLabel());
    }

    @Override
    public TypeRule getRuleType() {
        return TypeRule.MIN_VALUE;
    }
}
