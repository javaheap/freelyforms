package com.utbm.da50.freelyform.model.validationFields;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import org.springframework.stereotype.Component;

@Component
public class DateFieldValidator implements ValidationField{
    @Override
    public void validate(Field field) throws ValidationFieldException {
        //...
    }

    @Override
    public TypeField getFieldType() {
        return TypeField.DATE;
    }
}
