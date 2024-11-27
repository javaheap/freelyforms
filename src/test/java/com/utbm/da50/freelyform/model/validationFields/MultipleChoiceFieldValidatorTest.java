package com.utbm.da50.freelyform.model.validationFields;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unchecked")
public class MultipleChoiceFieldValidatorTest {

    private MultipleChoiceFieldValidator validator;
    private Field multipleChoiceField;

    @BeforeEach
    public void setUp() {
        validator = new MultipleChoiceFieldValidator();
        multipleChoiceField = new Field();
        multipleChoiceField.setLabel("Test Field");
        multipleChoiceField.setType(TypeField.MULTIPLE_CHOICE);
    }

    @Test
    public void testValidate_Success() {
        Option options = new Option();
        options.setChoices(Arrays.asList("Option 1", "Option 2"));
        multipleChoiceField.setOptions(options);

        assertThat("Options choices should not be empty", options.getChoices(), is(not(empty())));

        assertThat("Field should have type MULTIPLE_CHOICE", multipleChoiceField.getType(), is(equalTo(TypeField.MULTIPLE_CHOICE)));

        // No exception should be thrown here
        validator.validate(multipleChoiceField);
    }

    @Test
    public void testValidate_ThrowsExceptionWhenChoicesAreNull() {
        multipleChoiceField.setOptions(new Option());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(multipleChoiceField);
        });

        assertThat(exception.getMessage(), containsString("requires at least one choice option"));
    }

    @Test
    public void testValidate_ThrowsExceptionWhenOptionsIsNull() {
        multipleChoiceField.setOptions(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validate(multipleChoiceField);
        });

        assertThat(exception.getMessage(), containsString("requires at least one choice option"));
    }

    @Test
    public void testGetFieldType() {
        assertThat(validator.getFieldType(), is(TypeField.MULTIPLE_CHOICE));
    }
}
