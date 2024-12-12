package com.utbm.da50.freelyform.model;

import com.utbm.da50.freelyform.dto.answer.AnswerOutputDetailled;
import com.utbm.da50.freelyform.dto.answer.AnswerOutputSimple;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a group of answers submitted by a user for a specific prefab.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Make the all-args constructor private
@NoArgsConstructor
@Document(collection = "answers")
@Builder
public class AnswerGroup {
    @Id
    private String id;

    @Setter
    private String prefabId;

    @Setter
    private AnswerUser user;

    @Setter
    private String userId;

    @CreatedDate
    @Setter
    private LocalDateTime createdAt;

    @Setter
    private List<AnswerSubGroup> answers;

    public AnswerOutputDetailled toRest() {
        return new AnswerOutputDetailled(
                id,
                prefabId,
                user.toRest(),
                createdAt,
                answers
        );
    }

    public AnswerOutputSimple toRestSimple() {
        return new AnswerOutputSimple(
                id,
                user,
                createdAt
        );
    }
}