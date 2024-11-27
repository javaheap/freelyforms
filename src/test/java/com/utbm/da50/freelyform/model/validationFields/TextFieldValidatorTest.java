package com.utbm.da50.freelyform.model.validationFields;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("unchecked")
public class TextFieldValidatorTest {

    private TextFieldValidator validator;
    private Field field;

    @BeforeEach
    public void setUp() {
        validator = new TextFieldValidator();
        field = new Field();
        field.setId("testField");
        field.setType(TypeField.TEXT);
    }

    @Test
    public void validate_Success_WithMinMaxRules() {
        Rule minLengthRule = new Rule(TypeRule.MIN_LENGTH, "5");
        Rule maxLengthRule = new Rule(TypeRule.MAX_LENGTH, "10");
        field.setValidationRules(Arrays.asList(minLengthRule, maxLengthRule));

        assertThat(field.getValidationRules(), is(notNullValue()));
        assertThat(field.getValidationRules().size(), is(equalTo(2)));

        // Should not throw an exception
        validator.validate(field);
    }

    @Test
    public void validate_Success_WithOnlyMinLengthRule() {
        Rule minLengthRule = new Rule(TypeRule.MIN_LENGTH, "3");
        field.setValidationRules(Collections.singletonList(minLengthRule));

        validator.validate(field);

        assertThat(field.getValidationRules().size(), is(equalTo(1)));
    }

    @Test
    public void validate_Success_WithOnlyMaxLengthRule() {
        Rule maxLengthRule = new Rule(TypeRule.MAX_LENGTH, "15");
        field.setValidationRules(Collections.singletonList(maxLengthRule));

        validator.validate(field);

        assertThat(field.getValidationRules().size(), is(equalTo(1)));
    }

    @Test
    public void validate_Failure_WithMinLengthGreaterThanMaxLength() {
        Rule minLengthRule = new Rule(TypeRule.MIN_LENGTH, "10");
        Rule maxLengthRule = new Rule(TypeRule.MAX_LENGTH, "5");
        field.setValidationRules(Arrays.asList(minLengthRule, maxLengthRule));

        ValidationFieldException exception = assertThrows(ValidationFieldException.class, () -> {
            validator.validate(field);
        });

        assertThat(exception.getMessage(), containsString("min_length rule must be less than or equal to the max_length rule in the field"));
    }

    @Test
    public void validate_Success_NoValidationRules() {
        field.setValidationRules(Collections.emptyList());

        validator.validate(field);

        assertThat(field.getValidationRules().isEmpty(), is(true));
    }

    @Test
    public void getFieldType_ShouldReturnTextTypeField() {
        TypeField fieldType = validator.getFieldType();

        assertThat(fieldType, is(TypeField.TEXT));
    }
}
