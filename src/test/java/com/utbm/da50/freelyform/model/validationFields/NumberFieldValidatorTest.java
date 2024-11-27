package com.utbm.da50.freelyform.model.validationFields;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unchecked")
public class NumberFieldValidatorTest {

    private NumberFieldValidator validator;
    private Field numberField;

    @BeforeEach
    public void setUp() {
        validator = new NumberFieldValidator();
        numberField = new Field();
        numberField.setId("field123");
        numberField.setType(TypeField.NUMBER);
    }

    @Test
    public void testValidate_Success() {
        Rule minValueRule = new Rule(TypeRule.MIN_VALUE, "10");
        Rule maxValueRule = new Rule(TypeRule.MAX_VALUE, "20");
        numberField.setValidationRules(Arrays.asList(minValueRule, maxValueRule));

        assertThat("Field should have type NUMBER", numberField.getType(), is(TypeField.NUMBER));
        assertThat("Min value rule should be 10", minValueRule.getValue(), is("10"));
        assertThat("Max value rule should be 20", maxValueRule.getValue(), is("20"));

        // No exception should be thrown here
        validator.validate(numberField);
    }

    @Test
    public void testValidate_ThrowsExceptionWhenMinGreaterThanMax() {
        Rule minValueRule = new Rule(TypeRule.MIN_VALUE, "30");
        Rule maxValueRule = new Rule(TypeRule.MAX_VALUE, "20");
        numberField.setValidationRules(Arrays.asList(minValueRule, maxValueRule));

        ValidationFieldException exception = assertThrows(ValidationFieldException.class, () -> {
            validator.validate(numberField);
        });

        assertThat(exception.getMessage(), containsString("min_value rule must be less than or equal to the max_value rule in the field : " + numberField.getId()));
    }

    @Test
    public void testValidate_NoExceptionWhenOnlyMinValue() {
        Rule minValueRule = new Rule(TypeRule.MIN_VALUE, "10");
        numberField.setValidationRules(List.of(minValueRule));

        validator.validate(numberField);
    }

    @Test
    public void testValidate_NoExceptionWhenOnlyMaxValue() {
        Rule maxValueRule = new Rule(TypeRule.MAX_VALUE, "20");
        numberField.setValidationRules(List.of(maxValueRule));

        validator.validate(numberField);
    }

    @Test
    public void testGetFieldType() {
        assertThat(validator.getFieldType(), is(TypeField.NUMBER));
    }
}
