package com.utbm.da50.freelyform.dto.answer;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.utbm.da50.freelyform.model.AnswerGroup;
import com.utbm.da50.freelyform.model.AnswerSubGroup;
import lombok.*;

import java.util.List;

@JsonPropertyOrder({ "id", "answers" })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerInput {
    private List<AnswerSubGroup> answers;

    public AnswerGroup toAnswer() {
        return AnswerGroup.builder()
                .answers(answers)
                .build();
    }
}