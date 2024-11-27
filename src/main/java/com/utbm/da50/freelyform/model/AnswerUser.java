package com.utbm.da50.freelyform.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user in answerOutput
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerUser {
    private String name;
    private String email;

    public AnswerUser toRest() {
        return new AnswerUser(name, email);
    }
}