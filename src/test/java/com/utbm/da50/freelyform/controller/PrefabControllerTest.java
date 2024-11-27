package com.utbm.da50.freelyform.controller;

import com.utbm.da50.freelyform.dto.*;
import com.utbm.da50.freelyform.enums.UserRole;
import com.utbm.da50.freelyform.model.Prefab;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.service.ExcelExportService;
import com.utbm.da50.freelyform.service.PrefabService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PrefabControllerTest {

    @Mock
    private PrefabService prefabService;
    @Mock
    private ExcelExportService excelExportService;

    @InjectMocks
    private PrefabController prefabController;

    private User mockUser;
    private User adminUser;

    private Prefab mockPrefab;
    private PrefabInput prefabInput;
    private PrefabOutputDetailled prefabOutput;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = new User();
        adminUser.setId("admin123");
        adminUser.setRole(new HashSet<>(){{
            add(UserRole.USER);
            add(UserRole.ADMIN);
        }});

        mockUser = new User();
        mockUser.setId("user123");
        mockUser.setRole(new HashSet<>(){{
            add(UserRole.USER);
        }});

        mockPrefab = mock(Prefab.class);
        mockPrefab.setUserId(mockUser.getId());


        prefabInput = new PrefabInput();
        prefabInput.setName("Test Prefab");
        prefabInput.setDescription("Test Description");
        prefabInput.setGroups(List.of());
        prefabInput.setTags(Arrays.asList("tag1", "tag2").toArray(new String[0]));

        prefabOutput = mock(PrefabOutputDetailled.class);

        when(mockPrefab.toRest()).thenReturn(prefabOutput);
        when(mockPrefab.getId()).thenReturn("prefab123");
        when(mockPrefab.getUserId()).thenReturn(mockUser.getId());
    }

    @Test
    public void testGetAllPrefabs_Success() {
        List<Prefab> mockPrefabs = Collections.singletonList(mockPrefab);
        when(prefabService.getPrefabsByUser(mockUser.getId())).thenReturn(mockPrefabs);

        ResponseEntity<List<PrefabOutputSimple>> response = prefabController.getAllPrefabs(mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(prefabService, times(1)).getPrefabsByUser(mockUser.getId());
    }

    @Test
    public void testGetPrefabById_Success() {
        when(prefabService.getPrefabById(mockPrefab.getId(), false)).thenReturn(mockPrefab);
        when(mockPrefab.toRest(Boolean.FALSE)).thenReturn(prefabOutput);

        ResponseEntity<PrefabOutputDetailled> response = prefabController.getPrefabById(mockPrefab.getId(), false, null);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(prefabService, times(1)).getPrefabById(mockPrefab.getId(), false);
        assertThat(prefabOutput.getIsAlreadyAnswered(), is(Boolean.FALSE));
    }

    @Test
    public void testGetPrefabById_WithHiddenAndUser() {
        when(prefabService.getPrefabById(mockPrefab.getId(), true)).thenReturn(mockPrefab);
        when(prefabService.isAlreadyAnswered(mockPrefab, mockUser.getId())).thenReturn(true);
        when(mockPrefab.toRest(true)).thenReturn(prefabOutput);

        ResponseEntity<PrefabOutputDetailled> response = prefabController.getPrefabById(mockPrefab.getId(), true, mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(prefabService, times(1)).getPrefabById(mockPrefab.getId(), true);
        verify(prefabService, times(1)).isAlreadyAnswered(mockPrefab, mockUser.getId());
    }

    @Test
    public void testGetPrefabById_NotFound() {
        when(prefabService.getPrefabById(anyString(), anyBoolean())).thenThrow(new NoSuchElementException());

        ResponseEntity<PrefabOutputDetailled> response = prefabController.getPrefabById("invalid_id", false, null);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(prefabService, times(1)).getPrefabById("invalid_id", false);
    }

    @Test
    public void testCreatePrefab_Success() {
        when(prefabService.createPrefab(Mockito.any(Prefab.class))).thenReturn(mockPrefab);

        ResponseEntity<PrefabOutput> response = prefabController.createPrefab(mockUser, prefabInput);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(notNullValue()));
        verify(prefabService, times(1)).createPrefab(Mockito.any(Prefab.class));
    }

    @Test
    public void testUpdatePrefab_Success() {
        when(prefabService.getPrefabById(mockPrefab.getId())).thenReturn(mockPrefab);
        when(prefabService.updatePrefab(eq(mockPrefab.getId()), Mockito.any(Prefab.class))).thenReturn(mockPrefab);

        ResponseEntity<PrefabOutput> response = prefabController.updatePrefab(mockPrefab.getId(), mockUser, prefabInput);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        verify(prefabService, times(1)).updatePrefab(eq(mockPrefab.getId()), Mockito.any(Prefab.class));
    }

    @Test
    public void testUpdatePrefab_Forbidden() {
        Prefab prefabOwnedByAnotherUser = new Prefab();
        prefabOwnedByAnotherUser.setUserId("anotherUser");

        when(prefabService.getPrefabById(mockPrefab.getId())).thenReturn(prefabOwnedByAnotherUser);

        ResponseEntity<PrefabOutput> response = prefabController.updatePrefab(mockPrefab.getId(), mockUser, prefabInput);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        verify(prefabService, times(1)).getPrefabById(mockPrefab.getId());
        verify(prefabService, never()).updatePrefab(anyString(), Mockito.any(Prefab.class));
    }

    @Test
    public void testDeletePrefab_Success() {
        when(prefabService.getPrefabById(mockPrefab.getId())).thenReturn(mockPrefab);
        when(prefabService.deletePrefab(mockPrefab.getId())).thenReturn(mockPrefab);

        ResponseEntity<PrefabOutput> response = prefabController.deletePrefab(mockPrefab.getId(), mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
        verify(prefabService, times(1)).deletePrefab(mockPrefab.getId());
    }

    @Test
    public void testDeletePrefab_Forbidden() {
        Prefab prefabOwnedByAnotherUser = new Prefab();
        prefabOwnedByAnotherUser.setUserId("anotherUser");

        when(prefabService.getPrefabById(mockPrefab.getId())).thenReturn(prefabOwnedByAnotherUser);

        ResponseEntity<PrefabOutput> response = prefabController.deletePrefab(mockPrefab.getId(), mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        verify(prefabService, times(1)).getPrefabById(mockPrefab.getId());
        verify(prefabService, never()).deletePrefab(mockPrefab.getId());
    }

    @Test
    public void testDeletePrefab_NotFound() {
        when(prefabService.getPrefabById("invalid_id")).thenThrow(new NoSuchElementException());

        ResponseEntity<PrefabOutput> response = prefabController.deletePrefab("invalid_id", mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        verify(prefabService, times(1)).getPrefabById("invalid_id");
    }

    @Test
    public void testExportAnswers_Success() throws IOException {
        String prefabId = "prefab123";
        byte[] mockExcelFile = new byte[]{1, 2, 3, 4};

        when(excelExportService.generateExcelForPrefab(prefabId)).thenReturn(mockExcelFile);

        ResponseEntity<byte[]> response = prefabController.exportAnswers(prefabId, adminUser);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION), is("attachment; filename=\"answers-prefab123.xlsx\""));
        assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_OCTET_STREAM));
        verify(excelExportService, times(1)).generateExcelForPrefab(prefabId);
    }

    @Test
    public void testExportAnswers_Forbidden() throws IOException {
        String prefabId = "prefab123";

        ResponseEntity<byte[]> response = prefabController.exportAnswers(prefabId, mockUser);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        verify(excelExportService, never()).generateExcelForPrefab(anyString());
    }

    @Test
    public void testExportAnswers_NotFound() throws IOException {
        String prefabId = "prefab123";

        when(excelExportService.generateExcelForPrefab(prefabId)).thenThrow(new NoSuchElementException());

        ResponseEntity<byte[]> response = prefabController.exportAnswers(prefabId, adminUser);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        verify(excelExportService, times(1)).generateExcelForPrefab(prefabId);
    }

    @Test
    public void testExportAnswers_Unauthenticated() throws IOException {
        String prefabId = "prefab123";

        ResponseEntity<byte[]> response = prefabController.exportAnswers(prefabId, null);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        verify(excelExportService, never()).generateExcelForPrefab(anyString());
    }





}
