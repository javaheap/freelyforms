package com.utbm.da50.freelyform.model.validationFields;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
    public class TextFieldValidator implements ValidationField {

    @Override
    public void validate(Field field) throws ValidationFieldException {
        // ...
        // Test if the field specific rules like : min, max
        Optional<Rule> minLengthRule = field.getValidationRules().stream()
                .filter(rule -> rule.getType() == TypeRule.MIN_LENGTH)
                .findFirst();

        Optional<Rule> maxLengthRule = field.getValidationRules().stream()
                .filter(rule -> rule.getType() == TypeRule.MAX_LENGTH)
                .findFirst();

        if (minLengthRule.isPresent() && maxLengthRule.isPresent()) {
            int minValue = Integer.parseInt(minLengthRule.get().getValue());
            int maxValue = Integer.parseInt(maxLengthRule.get().getValue());

            if (minValue > maxValue) {
                throw new ValidationFieldException("The min_length rule must be less than or equal to the max_length rule in the field : " + field.getId());
            }
        }
    }

    @Override
    public TypeField getFieldType() {
        return TypeField.TEXT;
    }
}
