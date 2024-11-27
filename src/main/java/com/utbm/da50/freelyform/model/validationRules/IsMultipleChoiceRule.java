package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IsMultipleChoiceRule implements ValidationRule {

    @Override
    public void validate(Object userInput, Field field, Rule rule) throws ValidationRuleException {
        if(!(userInput instanceof List<?> answers)){
            throw new ValidationRuleException("The answer must be a list of choices for the field " + field.getLabel());
        }
        for(Object answer : answers){
            if (!(answer instanceof String) || !field.getOptions().getChoices().contains(answer)) {
                throw new ValidationRuleException("The answer " + answer + " is not a valid choice for the field " + field.getLabel());
            }
        }
    }

    @Override
    public TypeRule getRuleType() {
        return TypeRule.IS_MULTIPLE_CHOICE;
    }
}
