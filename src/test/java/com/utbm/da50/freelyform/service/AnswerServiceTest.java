package com.utbm.da50.freelyform.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.utbm.da50.freelyform.dto.answer.AnswerInput;
import com.utbm.da50.freelyform.exceptions.ResourceNotFoundException;
import com.utbm.da50.freelyform.model.*;
import com.utbm.da50.freelyform.repository.AnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private PrefabService prefabService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AnswerService answerService;

    private AnswerGroup mockAnswerGroup;
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn("user123");

        AnswerInput mockAnswerInput = mock(AnswerInput.class);
        when(mockAnswerInput.getAnswers()).thenReturn(List.of());

        Field mockField = new Field();
        mockField.setHidden(false);

        Group mockGroup = mock(Group.class);
        when(mockGroup.getId()).thenReturn("group123");
        when(mockGroup.getName()).thenReturn("group123");

        AnswerSubGroup mockSubGroup = mock(AnswerSubGroup.class);
        when(mockSubGroup.getGroup()).thenReturn("group123");

        Prefab mockPrefab = mock(Prefab.class);
        when(mockPrefab.getId()).thenReturn("prefab123");
        when(mockPrefab.getName()).thenReturn("Test Prefab");
        when(mockPrefab.getDescription()).thenReturn("Test Description");
        when(mockPrefab.getGroups()).thenReturn(List.of(mockGroup));
        when(mockPrefab.getUserId()).thenReturn("user123");
        when(mockPrefab.getIsActive()).thenReturn(true);

        mockAnswerGroup = mock(AnswerGroup.class);
        when(mockAnswerGroup.getUserId()).thenReturn("user123");
        when(mockAnswerGroup.getPrefabId()).thenReturn("prefab123");
        when(mockAnswerGroup.getAnswers()).thenReturn(List.of(mockSubGroup));

        when(userService.getUserById(anyString())).thenReturn(mockUser);
        when(prefabService.getPrefabById("prefab123", false)).thenReturn(mockPrefab);
    }

    @Test
    void processAnswer() {
        when(answerRepository.save(any(AnswerGroup.class))).thenReturn(mockAnswerGroup);

        AnswerGroup result = answerService.processAnswer("prefab123", "user123", mockAnswerGroup);

        assertNotNull(result);
        verify(answerRepository, times(1)).save(any(AnswerGroup.class));
    }

    @Test
    void processAnswer_prefabNotFound() {
        when(prefabService.getPrefabById("invalid_id", false)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () ->
                answerService.processAnswer("invalid_id", "user123", mockAnswerGroup));

        assertEquals("No prefab found for prefabId 'invalid_id'", exception.getMessage());
    }

    @Test
    void processAnswer_saveThrowsException() {
        when(answerRepository.save(any(AnswerGroup.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                answerService.processAnswer("prefab123", "user123", mockAnswerGroup));

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void getAnswerGroup() {
        when(prefabService.doesUserOwnPrefab("user123", "prefab123")).thenReturn(true);
        when(answerRepository.findByPrefabIdAndId("prefab123", "answer123")).thenReturn(
                Optional.of(mockAnswerGroup));

        Optional<AnswerGroup> result = Optional.ofNullable(answerService.getAnswerGroup("prefab123",
                "answer123", mockUser));

        assertTrue(result.isPresent());
        assertEquals(mockAnswerGroup, result.get());
        verify(answerRepository, times(1)).findByPrefabIdAndId(
                "prefab123", "answer123");
    }

    @Test
    void getAnswerGroup_userDoesNotOwnPrefab() {
        when(prefabService.doesUserOwnPrefab("user123", "invalid_id")).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                answerService.getAnswerGroup("invalid_id", "answer123", mockUser));

        assertEquals("The user 'user123' doesn't own this prefab 'invalid_id'", exception.getMessage());
    }

    @Test
    void getAnswerGroup_answerNotFound() {
        when(prefabService.doesUserOwnPrefab("user123", "prefab123")).thenReturn(true);
        when(answerRepository.findByPrefabIdAndId("prefab123", "invalid_id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                answerService.getAnswerGroup("prefab123", "invalid_id", mockUser));

        assertEquals("No response found for prefabId 'prefab123' and answerId 'invalid_id'", exception.getMessage());
    }

    @Test
    void getAnswerGroupByPrefabId() {
        when(answerRepository.findByPrefabId("prefab123")).thenReturn(Optional.of(List.of(mockAnswerGroup)));

        List<AnswerGroup> result = answerService.getAnswerGroupByPrefabId("prefab123",
                Optional.empty(), Optional.empty(), Optional.empty());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockAnswerGroup, result.getFirst());
        verify(answerRepository, times(1)).findByPrefabId("prefab123");
    }
}