package com.utbm.da50.freelyform.model.validationFields;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NumberFieldValidator implements ValidationField {

    @Override
    public void validate(Field field) throws ValidationFieldException {
        //...
        // Test if the field specific rules like : min, max
        Optional<Rule> minValueRule = field.getValidationRules().stream()
                .filter(rule -> rule.getType() == TypeRule.MIN_VALUE)
                .findFirst();

        Optional<Rule> maxValueRule = field.getValidationRules().stream()
                .filter(rule -> rule.getType() == TypeRule.MAX_VALUE)
                .findFirst();

        if (minValueRule.isPresent() && maxValueRule.isPresent()) {
            int minValue = Integer.parseInt(minValueRule.get().getValue());
            int maxValue = Integer.parseInt(maxValueRule.get().getValue());

            if (minValue > maxValue) {
                throw new ValidationFieldException("The min_value rule must be less than or equal to the max_value rule in the field : " + field.getId());
            }
        }
    }

    @Override
    public TypeField getFieldType() {
        return TypeField.NUMBER;
    }
}
