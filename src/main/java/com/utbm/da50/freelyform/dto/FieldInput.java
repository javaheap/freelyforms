package com.utbm.da50.freelyform.dto;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Option;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldInput {
    @NonNull
    private String id;
    private String label;
    @NonNull
    private TypeField type;
    private boolean optional;
    private boolean hidden;
    private List<ValidationRuleInput> validationRules;
    private Option options;

    public Field toField() {
        return new Field(
                id,
                label,
                type,
                optional,
                hidden,
                validationRules.stream()
                        .map(ValidationRuleInput::toValidationRule)
                        .collect(Collectors.toList()),
                options
        );
    }
}