package com.utbm.da50.freelyform.dto.answer;

import com.utbm.da50.freelyform.model.AnswerUser;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerUserInput {
    @NonNull
    private String name;
    private String email;

    public AnswerUser toAnswerUser() {
        return new AnswerUser(name, email);
    }
}