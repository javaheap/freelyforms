package com.utbm.da50.freelyform.service;

import com.utbm.da50.freelyform.exceptions.UniqueResponseException;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Group;
import com.utbm.da50.freelyform.model.Prefab;
import com.utbm.da50.freelyform.repository.PrefabRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PrefabServiceTest {

    @Mock
    private PrefabRepository prefabRepository;

    @Mock
    private FieldService fieldService;
    @Mock
    private AnswerService answerService;

    @InjectMocks
    private PrefabService prefabService;

    private Prefab mockPrefab;
    private Group mockGroup;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Field mockField = new Field();
        mockField.setHidden(false);

        mockGroup = new Group();
        mockGroup.setId("group123");
        mockGroup.setFields(List.of(mockField));

        mockPrefab = Prefab.builder()
                .id("prefab123")
                .name("Test Prefab")
                .description("Test Description")
                .groups(Collections.singletonList(mockGroup))
                .userId("user123")
                .build();
    }

    @Test
    public void testCreatePrefab_Success() throws ValidationFieldException {
        when(prefabRepository.save(any(Prefab.class))).thenReturn(mockPrefab);

        Prefab createdPrefab = prefabService.createPrefab(mockPrefab);

        assertThat(createdPrefab, is(notNullValue()));
        assertThat(createdPrefab.getId(), is("prefab123"));
        verify(fieldService, times(1)).validateFields(mockGroup.getFields());
        verify(prefabRepository, times(1)).save(any(Prefab.class));
    }

    @Test
    public void testCreatePrefab_ValidationException() throws ValidationFieldException {
        doThrow(new ValidationFieldException("Invalid field")).when(fieldService).validateFields(anyList());

        assertThrows(ValidationFieldException.class, () -> {
            prefabService.createPrefab(mockPrefab);
        });

        verify(fieldService, times(1)).validateFields(mockGroup.getFields());
        verify(prefabRepository, never()).save(any(Prefab.class));
    }

    @Test
    public void testUpdatePrefab_Success() throws NoSuchElementException, ValidationFieldException {
        Prefab updatedPrefab = Prefab.builder()
                .id("prefab123")
                .name("Updated Name")
                .description("Updated Description")
                .groups(Collections.singletonList(mockGroup))
                .userId("user123")
                .tags(new String[]{"tag1", "tag2"})
                .build();

        when(prefabRepository.findById("prefab123")).thenReturn(Optional.of(mockPrefab));
        when(prefabRepository.save(any(Prefab.class))).thenReturn(updatedPrefab);

        Prefab resultPrefab = prefabService.updatePrefab("prefab123", updatedPrefab);

        assertThat(resultPrefab, is(notNullValue()));
        assertThat(resultPrefab.getName(), is("Updated Name"));
        assertThat(resultPrefab.getDescription(), is("Updated Description"));
        verify(fieldService, times(1)).validateFields(mockGroup.getFields());
        verify(prefabRepository, times(1)).save(any(Prefab.class));
    }

    @Test
    public void testUpdatePrefab_NotFound() {
        when(prefabRepository.findById("invalid_id")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            prefabService.updatePrefab("invalid_id", mockPrefab);
        });

        verify(prefabRepository, never()).save(any(Prefab.class));
    }

    @Test
    public void testDeletePrefab_Success() {
        when(prefabRepository.findById("prefab123")).thenReturn(Optional.of(mockPrefab));

        Prefab deletedPrefab = prefabService.deletePrefab("prefab123");

        assertThat(deletedPrefab, is(notNullValue()));
        assertThat(deletedPrefab.getId(), is("prefab123"));
        verify(prefabRepository, times(1)).deleteById("prefab123");
    }

    @Test
    public void testDeletePrefab_NotFound() {
        when(prefabRepository.findById("invalid_id")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            prefabService.deletePrefab("invalid_id");
        });

        verify(prefabRepository, never()).deleteById(anyString());
    }

    @Test
    public void testGetPrefabById_Success() {
        when(prefabRepository.findById("prefab123")).thenReturn(Optional.of(mockPrefab));

        Prefab resultPrefab = prefabService.getPrefabById("prefab123");

        assertThat(resultPrefab, is(notNullValue()));
        assertThat(resultPrefab.getId(), is("prefab123"));
        verify(prefabRepository, times(1)).findById("prefab123");
    }

    @Test
    public void testGetPrefabById_NotFound() {
        when(prefabRepository.findById("invalid_id")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            prefabService.getPrefabById("invalid_id");
        });

        verify(prefabRepository, times(1)).findById("invalid_id");
    }

    @Test
    public void testGetPrefabById_WithoutHiddenFields() {
        Field hiddenField = new Field();
        hiddenField.setHidden(true);

        Field visibleField = new Field();
        visibleField.setHidden(false);

        Group groupWithHiddenField = new Group();
        groupWithHiddenField.setFields(Arrays.asList(hiddenField, visibleField));

        Prefab prefabWithHiddenField = Prefab.builder()
                .id("prefab123")
                .name("Test Prefab")
                .description("Test Description")
                .groups(List.of(groupWithHiddenField))
                .userId("user123")
                .build();

        when(prefabRepository.findById("prefab123")).thenReturn(Optional.of(prefabWithHiddenField));

        Prefab resultPrefab = prefabService.getPrefabById("prefab123", true);

        assertThat(resultPrefab.getGroups().get(0).getFields(), hasSize(2));
        assertThat(resultPrefab.getGroups().get(0).getFields().get(0).getHidden(), is(true));
        verify(prefabRepository, times(1)).findById("prefab123");
    }

    @Test
    public void testIsAlreadyAnswered() throws UniqueResponseException {
        // Test when groups are null
        mockPrefab.setGroups(null);
        Assertions.assertTrue(prefabService.isAlreadyAnswered(mockPrefab, "user123"));

        // Test when groups are empty
        mockPrefab.setGroups(Collections.emptyList());
        Assertions.assertTrue(prefabService.isAlreadyAnswered(mockPrefab, "user123"));

        // Test when groups contain empty fields
        Group groupWithEmptyFields = new Group();
        groupWithEmptyFields.setFields(Collections.emptyList());
        mockPrefab.setGroups(Collections.singletonList(groupWithEmptyFields));
        Assertions.assertTrue(prefabService.isAlreadyAnswered(mockPrefab, "user123"));

        // UniqueResponseException
        Group groupWithFields = new Group();
        groupWithFields.setFields(Collections.singletonList(new Field()));
        mockPrefab.setGroups(Collections.singletonList(groupWithFields));
        doThrow(new UniqueResponseException("User has already answered")).when(answerService).validateUniqueUserResponse("prefab123", "user123");
        Assertions.assertTrue(prefabService.isAlreadyAnswered(mockPrefab, "user123"));

        doNothing().when(answerService).validateUniqueUserResponse("prefab123", "user123");
        Assertions.assertFalse(prefabService.isAlreadyAnswered(mockPrefab, "user123"));
    }



    @Test
    public void testGetPrefabsByUser_Success() {
        when(prefabRepository.findByUserId("user123")).thenReturn(Collections.singletonList(mockPrefab));

        List<Prefab> prefabs = prefabService.getPrefabsByUser("user123");

        assertThat(prefabs, is(not(empty())));
        assertThat(prefabs, hasSize(1));
        assertThat(prefabs.getFirst().getUserId(), is("user123"));
        verify(prefabRepository, times(1)).findByUserId("user123");
    }
}

