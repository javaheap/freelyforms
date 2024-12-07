package com.utbm.da50.freelyform.controller;


import com.utbm.da50.freelyform.dto.answer.AnswerOutputDetailled;
import com.utbm.da50.freelyform.dto.answer.AnswerOutputSimple;
import com.utbm.da50.freelyform.model.AnswerGroup;
import com.utbm.da50.freelyform.service.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import com.utbm.da50.freelyform.dto.answer.AnswerInput;
import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


class AnswerControllerTest {

    @Mock
    private AnswerService answerService;

    @InjectMocks
    private AnswerController answerController;

    private AnswerGroup mockAnswerGroup;

    private User mockUser;
    private AnswerInput mockAnswerInput;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockAnswerGroup = mock(AnswerGroup.class);
        mockAnswerGroup.setUserId("user123");
        mockAnswerGroup.setPrefabId("prefab123");
        mockAnswerGroup.setAnswers(List.of());

        mockUser = mock(User.class);
        mockUser.setId("user123");
        mockUser.setRole(new HashSet<>(){{
            add(UserRole.USER);
        }});

        mockAnswerInput = mock(AnswerInput.class);
        mockAnswerInput.setAnswers(List.of());
    }

    @Test
    void submitAnswer() {
        when(answerService.processAnswer(anyString(), anyString(), any(AnswerGroup.class))).thenReturn(mockAnswerGroup);

        ResponseEntity<?> response = answerController.submitAnswer(mockUser, "prefab123", mockAnswerInput);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(notNullValue()));
        verify(answerService, times(1)).processAnswer(anyString(), "user123", any(AnswerGroup.class));
    }

    @Test
    void getSpecificAnswer() {
        when(answerService.getAnswerGroup(anyString(), anyString(), any(User.class))).thenReturn(mockAnswerGroup);

        ResponseEntity<AnswerOutputDetailled> response = answerController.getSpecificAnswer("prefab123", "answer123", mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(answerService, times(1)).getAnswerGroup(anyString(), anyString(), any(User.class));
    }

    @Test
    void getAnswersByPrefabId() {
        when(answerService.getAnswerGroupByPrefabId(anyString(), any(), any(), any())).thenReturn(List.of(mockAnswerGroup));

        ResponseEntity<List<AnswerOutputSimple>> response = answerController.getAnswersByPrefabId("prefab123", mockUser, Optional.empty(), Optional.empty(), Optional.empty());

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(answerService, times(1)).getAnswerGroupByPrefabId(anyString(), any(), any(), any());
    }
}