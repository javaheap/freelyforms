package com.utbm.da50.freelyform.service;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.factories.FieldValidatorFactory;
import com.utbm.da50.freelyform.model.validationFields.ValidationField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class FieldServiceTest {

    @Mock
    private FieldValidatorFactory validatorFactory;

    @Mock
    private ValidationField mockValidator;

    @InjectMocks
    private FieldService fieldService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateFields_Success_WithValidFields() throws ValidationFieldException {
        Field field1 = new Field();
        field1.setType(TypeField.TEXT);

        Field field2 = new Field();
        field2.setType(TypeField.NUMBER);

        List<Field> fields = Arrays.asList(field1, field2);

        // Mock the behavior of the factory
        when(validatorFactory.getValidator(field1.getType())).thenReturn(mockValidator);
        when(validatorFactory.getValidator(field2.getType())).thenReturn(mockValidator);

        fieldService.validateFields(fields);

        verify(validatorFactory, times(1)).getValidator(field1.getType());
        verify(validatorFactory, times(1)).getValidator(field2.getType());
        verify(mockValidator, times(2)).validate(any(Field.class));
    }

    @Test
    void validateFields_Success_WithEmptyFieldList() throws ValidationFieldException {
        List<Field> emptyFieldList = Collections.emptyList();

        fieldService.validateFields(emptyFieldList);

        verifyNoInteractions(validatorFactory);
    }

    @Test
    void validateFields_ThrowsValidationFieldException_WhenValidationFails() throws ValidationFieldException {
        Field field = new Field();
        field.setType(TypeField.TEXT);
        List<Field> fields = Collections.singletonList(field);

        when(validatorFactory.getValidator(field.getType())).thenReturn(mockValidator);
        doThrow(new ValidationFieldException("Validation failed")).when(mockValidator).validate(field);

        ValidationFieldException exception = assertThrows(ValidationFieldException.class, () -> {
            fieldService.validateFields(fields);
        });

        assertThat(exception.getMessage(), equalTo("Validation failed"));
        verify(validatorFactory, times(1)).getValidator(field.getType());
        verify(mockValidator, times(1)).validate(field);
    }

    @Test
    void validateFields_ThrowsValidationFieldException_ForEachField() throws ValidationFieldException {
        Field field1 = new Field();
        field1.setType(TypeField.TEXT);

        Field field2 = new Field();
        field2.setType(TypeField.NUMBER);

        List<Field> fields = Arrays.asList(field1, field2);

        // Mock behaviors
        when(validatorFactory.getValidator(field1.getType())).thenReturn(mockValidator);
        when(validatorFactory.getValidator(field2.getType())).thenReturn(mockValidator);

        doThrow(new ValidationFieldException("Validation failed")).when(mockValidator).validate(any(Field.class));

        ValidationFieldException exception = assertThrows(ValidationFieldException.class, () -> {
            fieldService.validateFields(fields);
        });

        assertThat(exception.getMessage(), equalTo("Validation failed"));
        verify(validatorFactory, times(1)).getValidator(field1.getType());
        verify(mockValidator, times(1)).validate(field1);
        verify(mockValidator, never()).validate(field2); // Should not reach field2 validation if exception is thrown
    }
}
