package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegexMatchRule implements ValidationRule {

    @Override
    public void validate(Object userInput, Field field, Rule rule) {
        String regexPattern = rule.getValue();

        Pattern pattern = Pattern.compile(regexPattern);
        if (!pattern.matcher(userInput.toString()).matches()) {
            throw new ValidationRuleException("The field " + field.getLabel() + " does not match the regex pattern: " + regexPattern);
        }
    }

    @Override
    public TypeRule getRuleType() {
        return TypeRule.REGEX_MATCH;
    }
}

