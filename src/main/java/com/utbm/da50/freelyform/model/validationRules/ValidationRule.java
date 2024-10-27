package com.utbm.da50.freelyform.model.validationRules;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;

public interface ValidationRule {
    void validate(Object userInput, Field field, Rule rule) throws ValidationRuleException;
    TypeRule getRuleType();
}
