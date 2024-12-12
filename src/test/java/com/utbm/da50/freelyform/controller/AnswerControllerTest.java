package com.utbm.da50.freelyform.controller;


import com.utbm.da50.freelyform.dto.answer.AnswerInput;
import com.utbm.da50.freelyform.dto.answer.AnswerOutputDetailled;
import com.utbm.da50.freelyform.dto.answer.AnswerOutputSimple;
import com.utbm.da50.freelyform.model.*;
import com.utbm.da50.freelyform.service.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


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

        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn("user123");

        Group mockGroup = mock(Group.class);
        when(mockGroup.getId()).thenReturn("group123");
        when(mockGroup.getName()).thenReturn("group123");

        Prefab mockPrefab = mock(Prefab.class);
        when(mockPrefab.getId()).thenReturn("prefab123");
        when(mockPrefab.getName()).thenReturn("Test Prefab");
        when(mockPrefab.getDescription()).thenReturn("Test Description");
        when(mockPrefab.getGroups()).thenReturn(List.of(mockGroup));
        when(mockPrefab.getUserId()).thenReturn("user123");
        when(mockPrefab.getIsActive()).thenReturn(true);

        AnswerSubGroup mockSubGroup = mock(AnswerSubGroup.class);
        when(mockSubGroup.getGroup()).thenReturn("group123");

        mockAnswerInput = mock(AnswerInput.class);
        when(mockAnswerInput.getAnswers()).thenReturn(List.of(mockSubGroup));

        mockAnswerGroup = mock(AnswerGroup.class);
        when(mockAnswerGroup.getUserId()).thenReturn("user123");
        when(mockAnswerGroup.getPrefabId()).thenReturn("prefab123");
        when(mockAnswerGroup.getAnswers()).thenReturn(List.of(mockSubGroup));

        AnswerOutputDetailled mockAnswerOutput = mock(AnswerOutputDetailled.class);
        when(mockAnswerOutput.getId()).thenReturn("answer123");
        when(mockAnswerOutput.getAnswers()).thenReturn(List.of(mockSubGroup));
        when(mockAnswerOutput.getPrefabId()).thenReturn("prefab123");

        when(mockAnswerGroup.toRest()).thenReturn(mockAnswerOutput);
    }

    @Test
    void submitAnswer() {
        when(mockAnswerInput.toAnswer()).thenReturn(mockAnswerGroup);
        when(answerService.processAnswer("prefab123", "user123", mockAnswerGroup)).thenReturn(mockAnswerGroup);

        ResponseEntity<?> response = answerController.submitAnswer(mockUser, "prefab123", mockAnswerInput);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(notNullValue()));
        verify(answerService, times(1)).processAnswer("prefab123", "user123", mockAnswerGroup);
    }

    @Test
    void getSpecificAnswer() {
        when(answerService.getAnswerGroup("prefab123",
                "answer123", mockUser)).thenReturn(mockAnswerGroup);

        ResponseEntity<AnswerOutputDetailled> response = answerController.getSpecificAnswer("prefab123",
                "answer123", mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(answerService, times(1)).getAnswerGroup(anyString(), anyString(), any(User.class));
    }

    @Test
    void getSpecificAnswer_notFound() {
        when(answerService.getAnswerGroup("prefab123", "answer123", mockUser)).thenReturn(mockAnswerGroup);

        ResponseEntity<AnswerOutputDetailled> response = answerController.getSpecificAnswer("prefab123", "answer123", mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(answerService, times(1)).getAnswerGroup(anyString(), anyString(), any(User.class));
    }

    @Test
    void getAnswersByPrefabId() {
        when(answerService.getAnswerGroupByPrefabId("prefab123",Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(List.of(mockAnswerGroup));

        ResponseEntity<List<AnswerOutputSimple>> response = answerController.getAnswersByPrefabId("prefab123",
                mockUser, Optional.empty(), Optional.empty(), Optional.empty());

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(answerService, times(1)).getAnswerGroupByPrefabId("prefab123",Optional.empty(),
                Optional.empty(), Optional.empty());
    }

    @Test
    void getAnswersByPrefabId_emptyList() {
        when(answerService.getAnswerGroupByPrefabId("prefab123", Optional.empty(), Optional.empty(), Optional.empty())).thenReturn(List.of());

        ResponseEntity<List<AnswerOutputSimple>> response = answerController.getAnswersByPrefabId("prefab123", mockUser, Optional.empty(), Optional.empty(), Optional.empty());

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(Objects.requireNonNull(response.getBody()).isEmpty(), is(true));
    }
}