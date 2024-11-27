package com.utbm.da50.freelyform.model;

import com.utbm.da50.freelyform.dto.ValidationRuleInput;
import com.utbm.da50.freelyform.enums.TypeRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document
@Builder
@NoArgsConstructor
public class Rule {
    private TypeRule type;
    private String value;

    public ValidationRuleInput toRest() {
        return new ValidationRuleInput(type, value);
    }
}
