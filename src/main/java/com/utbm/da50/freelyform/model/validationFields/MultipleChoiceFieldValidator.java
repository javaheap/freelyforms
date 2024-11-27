package com.utbm.da50.freelyform.model.validationFields;


import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import org.springframework.stereotype.Component;

@Component
public class MultipleChoiceFieldValidator implements ValidationField {

    @Override
    public void validate(Field field) throws ValidationFieldException {
        if (field.getOptions() == null || field.getOptions().getChoices() == null || field.getOptions().getChoices().isEmpty()) {
            throw new IllegalArgumentException("Field " + field.getLabel() + " of type 'multiple_choice' requires at least one choice option.");
        }
    }

    @Override
    public TypeField getFieldType() {
        return TypeField.MULTIPLE_CHOICE;
    }
}