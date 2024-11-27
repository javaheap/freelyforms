package com.utbm.da50.freelyform.dto;

import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.model.Rule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationRuleInput {
    private TypeRule type;
    private String value;

    public Rule toValidationRule() {
        return new Rule(
                type,
                value
        );
    }
}