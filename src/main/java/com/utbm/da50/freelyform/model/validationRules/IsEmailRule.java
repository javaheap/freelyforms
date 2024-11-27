package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class IsEmailRule implements ValidationRule {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$"; // Email regex pattern

    @Override
    public void validate(Object userInput, Field field, Rule rule) throws ValidationRuleException {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        if (!pattern.matcher(userInput.toString()).matches()) {
            throw new ValidationRuleException("The field " + field.getLabel() + " must be a valid email address.");
        }
    }

    @Override
    public TypeRule getRuleType() {
        return TypeRule.IS_EMAIL;
    }
}
