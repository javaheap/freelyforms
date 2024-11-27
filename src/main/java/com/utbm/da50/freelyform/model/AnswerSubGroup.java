package com.utbm.da50.freelyform.model;

import java.util.List;
import com.mongodb.lang.NonNull;
import lombok.Data;

/**
 * Represents a subgroup of answers within an answer group, containing a group identifier
 * and a list of questions associated with that group.
 */
@Data
public class AnswerSubGroup {

    /**
     * Identifier for the answer subgroup.
     * This field is required and cannot be null.
     */
    @NonNull
    private String group;

    /**
     * List of questions associated with this answer subgroup.
     */
    private List<AnswerQuestion> questions;

    /**
     * Constructs an AnswerSubGroup with the specified group identifier and list of questions.
     *
     * @param group     the identifier for the answer subgroup, cannot be null.
     * @param questions a list of questions associated with this answer subgroup, cannot be null.
     */
    public AnswerSubGroup(@NonNull String group, @NonNull List<AnswerQuestion> questions) {
        this.group = group;
        this.questions = questions;
    }
}
