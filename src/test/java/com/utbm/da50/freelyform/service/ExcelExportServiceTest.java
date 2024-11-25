package com.utbm.da50.freelyform.service;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcelExportServiceTest {

    @Mock
    private AnswerService answerService;

    @Mock
    private PrefabService prefabService;

    @InjectMocks
    private ExcelExportService excelExportService;

    private Prefab mockPrefab;
    private List<AnswerGroup> mockAnswers;
    private static final String PREFAB_ID = "test-prefab-id";

    @BeforeEach
    void setUp() {
        // Create mock field
        Field textField = new Field();
        textField.setLabel("Test Field");
        textField.setType(TypeField.TEXT);
        textField.setOptional(false);

        // Create mock group
        Group group = new Group();
        group.setName("Test Group");
        group.setFields(Collections.singletonList(textField));

        // Create mock prefab
        mockPrefab = Prefab.builder().id(PREFAB_ID).build();
        mockPrefab.setGroups(Collections.singletonList(group));

        // Create mock user
        User user = new User();
        user.setFirstName("Test User");
        user.setLastName("Test User");
        user.setEmail("test@example.com");

        // Create mock answers
        AnswerQuestion answerQuestion = new AnswerQuestion("question","xd");
        answerQuestion.setQuestion("Test Field");
        answerQuestion.setAnswer("Test Answer");

        AnswerSubGroup answerSubGroup = new AnswerSubGroup("Test Group", List.of(answerQuestion));
        answerSubGroup.setGroup("Test Group");
        answerSubGroup.setQuestions(Collections.singletonList(answerQuestion));

        AnswerUser answerUser = new AnswerUser();
        answerUser.setName("Test User");
        answerUser.setEmail("test@example.com");

        AnswerGroup answerGroup = new AnswerGroup();
        answerGroup.setUser(answerUser);
        answerGroup.setCreatedAt(LocalDateTime.now());
        answerGroup.setAnswers(Collections.singletonList(answerSubGroup));

        mockAnswers = Collections.singletonList(answerGroup);
    }

    @Test
    void generateExcelForPrefab_ShouldCreateValidExcelFile() throws IOException {
        // Arrange
        when(prefabService.getPrefabById(PREFAB_ID)).thenReturn(mockPrefab);
        when(answerService.getAnswerGroupByPrefabId(PREFAB_ID)).thenReturn(mockAnswers);

        // Act
        byte[] excelFile = excelExportService.generateExcelForPrefab(PREFAB_ID);

        // Assert
        assertNotNull(excelFile);
        assertTrue(excelFile.length > 0);

        // Verify Excel content
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelFile))) {
            assertEquals(1, workbook.getNumberOfSheets());

            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Test Field", sheet.getSheetName());

            // Verify metadata section
            Row groupRow = sheet.getRow(2);
            assertEquals("Group:", groupRow.getCell(0).getStringCellValue());
            assertEquals("Test Group", groupRow.getCell(1).getStringCellValue());

            Row typeRow = sheet.getRow(3);
            assertEquals("Field Type:", typeRow.getCell(0).getStringCellValue());
            assertEquals("TEXT", typeRow.getCell(1).getStringCellValue());

            // Verify headers
            Row headerRow = sheet.getRow(8);
            assertEquals("User Name", headerRow.getCell(0).getStringCellValue());
            assertEquals("User Email", headerRow.getCell(1).getStringCellValue());
            assertEquals("Submission Date", headerRow.getCell(2).getStringCellValue());
            assertEquals("Answer", headerRow.getCell(3).getStringCellValue());

            // Verify data
            Row dataRow = sheet.getRow(9);
            assertEquals("Test User", dataRow.getCell(0).getStringCellValue());
            assertEquals("test@example.com", dataRow.getCell(1).getStringCellValue());
            assertNotNull(dataRow.getCell(2).getStringCellValue()); // DateTime
            assertEquals("Test Answer", dataRow.getCell(3).getStringCellValue());
        }
    }

    @Test
    void generateExcelForPrefab_WithSpecialFieldTypes() throws IOException {
        // Create fields with different types
        Field numberField = createField("Number Field", TypeField.NUMBER);
        Field multipleChoiceField = createField("MultipleChoice", TypeField.MULTIPLE_CHOICE); // Simplified name
        Field geolocationField = createField("Geolocation", TypeField.GEOLOCATION); // Simplified name
        List<Field> fields = new ArrayList<>(Arrays.asList(numberField, multipleChoiceField, geolocationField));

        // Update mock group with new fields
        Group group = mockPrefab.getGroups().getFirst();
        group.setFields(fields);

        // Create corresponding answers
        List<AnswerQuestion> answers = new ArrayList<>();
        answers.add(createAnswer("Number Field", 42.5));
        answers.add(createAnswer("MultipleChoice", Arrays.asList("Option1", "Option2")));
        answers.add(createAnswer("Geolocation", Map.of("lat", 45.5, "lng", -73.5)));

        mockAnswers.getFirst().getAnswers().getFirst().setQuestions(answers);

        // Arrange
        when(prefabService.getPrefabById(PREFAB_ID)).thenReturn(mockPrefab);
        when(answerService.getAnswerGroupByPrefabId(PREFAB_ID)).thenReturn(mockAnswers);

        // Act
        byte[] excelFile = excelExportService.generateExcelForPrefab(PREFAB_ID);

        // Assert
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelFile))) {
            assertEquals(3, workbook.getNumberOfSheets());

            // Verify Number sheet
            Sheet numberSheet = workbook.getSheet("Number Field");
            assertNotNull(numberSheet, "Number Field sheet should exist");

            // Verify Multiple Choice sheet
            Sheet multipleChoiceSheet = workbook.getSheet("MultipleChoice");
            assertNotNull(multipleChoiceSheet, "MultipleChoice sheet should exist");

            // Verify Geolocation sheet
            Sheet geoSheet = workbook.getSheet("Geolocation");
            assertNotNull(geoSheet, "Geolocation sheet should exist");
        }
    }

    @Test
    void generateExcelForPrefab_WithMissingPrefab() {
        when(prefabService.getPrefabById(PREFAB_ID))
                .thenThrow(new NoSuchElementException("Prefab not found"));

        assertThrows(NoSuchElementException.class,
                () -> excelExportService.generateExcelForPrefab(PREFAB_ID));
    }

    private Field createField(String label, TypeField type) {
        Field field = new Field();
        field.setLabel(label);
        field.setType(type);
        field.setOptional(false);
        return field;
    }

    private AnswerQuestion createAnswer(String question, Object answer) {
        AnswerQuestion aq = new AnswerQuestion("Do you know da way ?", "yes");
        aq.setQuestion(question);
        aq.setAnswer(answer);
        return aq;
    }
}