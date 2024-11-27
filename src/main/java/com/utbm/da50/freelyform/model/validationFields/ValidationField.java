package com.utbm.da50.freelyform.model.validationFields;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;

public interface ValidationField {
    void validate(Field field) throws ValidationFieldException;
    TypeField getFieldType();
}
