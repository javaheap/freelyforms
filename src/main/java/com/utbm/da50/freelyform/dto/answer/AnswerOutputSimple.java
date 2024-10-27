package com.utbm.da50.freelyform.dto.answer;

import com.utbm.da50.freelyform.model.AnswerUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerOutputSimple {
    private String id;
    private AnswerUser user;
    private LocalDateTime createdAt;
}