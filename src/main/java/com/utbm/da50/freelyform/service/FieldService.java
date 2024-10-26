package com.utbm.da50.freelyform.service;

import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.exceptions.ValidationRuleException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import com.utbm.da50.freelyform.model.factories.FieldValidatorFactory;
import com.utbm.da50.freelyform.model.factories.ValidationRuleFactory;
import com.utbm.da50.freelyform.model.validationFields.ValidationField;
import com.utbm.da50.freelyform.model.validationRules.ValidationRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldService {
    private final FieldValidatorFactory validatorFactory;
    private final ValidationRuleFactory ruleFactory;

    public FieldService(FieldValidatorFactory validatorFactory, ValidationRuleFactory ruleFactory) {
        this.validatorFactory = validatorFactory;
        this.ruleFactory = ruleFactory;
    }

    public void validateFields(List<Field> fields) throws ValidationFieldException {
        for (Field field : fields) {
            ValidationField validator = validatorFactory.getValidator(field.getType());
            validator.validate(field);
        }
    }

    public void validateFieldsRules(Field field, Object answer) throws ValidationRuleException {
        for(Rule rule : field.getValidationRules()) {
            ValidationRule validator = ruleFactory.getValidator(rule.getType());
            validator.validate(answer,field,rule);
        }
    }
}
