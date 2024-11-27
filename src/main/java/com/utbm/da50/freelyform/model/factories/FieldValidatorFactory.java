package com.utbm.da50.freelyform.model.factories;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.model.validationFields.ValidationField;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FieldValidatorFactory {

    private final Map<TypeField, ValidationField> validators = new HashMap<>();

    public FieldValidatorFactory(List<ValidationField> validatorList) {
        for (ValidationField validator : validatorList) {
            validators.put(validator.getFieldType(), validator);
        }
    }

    public ValidationField getValidator(TypeField fieldType) {
        ValidationField validator = validators.get(fieldType);
        if (validator == null) {
            throw new IllegalArgumentException("No validator found for field type: " + fieldType);
        }
        return validator;
    }
}