package com.utbm.da50.freelyform.model.factories;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.model.validationRules.ValidationRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class ValidationRuleFactory {

    private final Map<TypeRule, ValidationRule> ruleValidators = new HashMap<>();

    public ValidationRuleFactory(List<ValidationRule> ruleValidatorList) {
        for (ValidationRule validator : ruleValidatorList) {
            ruleValidators.put(validator.getRuleType(), validator);
        }
    }

    public ValidationRule getValidator(TypeRule ruleType) {
        ValidationRule validator = ruleValidators.get(ruleType);
        if (validator == null) {
            throw new IllegalArgumentException("No validator found for rule type: " + ruleType);
        }
        return validator;
    }
}
