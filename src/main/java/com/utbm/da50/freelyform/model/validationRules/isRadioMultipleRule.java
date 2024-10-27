package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

@Component
public class isRadioMultipleRule implements ValidationRule{
    @Override
    public void validate(Object userInput, Field field, Rule rule) throws ValidationRuleException {
        if (!(userInput instanceof String answer))
            throw new ValidationRuleException("User input is not a valid string");
        if (answer.isEmpty())
            throw new ValidationRuleException("User input is empty");
        if (!field.getOptions().getChoices().contains(answer))
            throw new ValidationRuleException("User input is not a valid option");
    }

    @Override
    public TypeRule getRuleType() {
        return TypeRule.IS_RADIO;
    }
}
