package com.utbm.da50.freelyform.dto.answer;

import com.utbm.da50.freelyform.model.AnswerSubGroup;
import com.utbm.da50.freelyform.model.AnswerUser;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class AnswerOutputDetailled {
    private String id;
    private String prefabId;
    private AnswerUser user;
    private LocalDateTime createdAt;
    private List<AnswerSubGroup> answers;

    public AnswerOutputDetailled(String id, String prefabId, AnswerUser user,
                                 LocalDateTime createdAt, List<AnswerSubGroup> answers) {
        this.id = id;
        this.prefabId = prefabId;
        this.user = user;
        this.createdAt = createdAt;
        this.answers = answers;
    }
}