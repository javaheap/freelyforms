package com.utbm.da50.freelyform.service;

import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ExcelExportService {

    private final AnswerService answerService;
    private final PrefabService prefabService;

    private static final int METADATA_START_ROW = 1;
    private static final int METADATA_END_ROW = 6;
    private static final int DATA_START_ROW = 8;

    public ExcelExportService(AnswerService answerService, PrefabService prefabService) {
        this.answerService = answerService;
        this.prefabService = prefabService;
    }

    /**
     * Generates an Excel file containing all answers for a specific prefab
     */
    public byte[] generateExcelForPrefab(String prefabId) throws NoSuchElementException, IOException {
        // Get the prefab and all its answers
        Prefab prefab = prefabService.getPrefabById(prefabId);
        List<AnswerGroup> answers = answerService.getAnswerGroupByPrefabId(prefabId);

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create sheets for each field
            // The idea is to have a sheet for each field, with the user name, email, submission date, and answer
            for (Group group : prefab.getGroups()) {
                for (Field field : group.getFields()) {
                    String sheetName = sanitizeSheetName(field.getLabel());
                    Sheet sheet = workbook.createSheet(sheetName);
                    populateFieldSheet(sheet, group, field, answers);
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void populateFieldSheet(Sheet sheet, Group group, Field field, List<AnswerGroup> answers) {
        // Create metadata section first (at the top) with field information like group, type, optional, etc.
        createMetadataSection(sheet, group, field);

        // Add empty rows for spacing
        for (int i = METADATA_END_ROW; i < DATA_START_ROW; i++) {
            sheet.createRow(i);
        }

        // Create header row for data columns
        var headerRow = sheet.createRow(DATA_START_ROW);
        createHeaderCell(headerRow, 0, "User Name");
        createHeaderCell(headerRow, 1, "User Email");
        createHeaderCell(headerRow, 2, "Submission Date");
        createHeaderCell(headerRow, 3, "Answer");

        // Add answers
        int rowNum = DATA_START_ROW + 1;
        for (AnswerGroup answer : answers) {
            Object fieldAnswer = findFieldAnswer(answer, group.getName(), field.getLabel());
            if (fieldAnswer != null) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(answer.getUser().getName());
                row.createCell(1).setCellValue(answer.getUser().getEmail().isEmpty()? "N/A" : answer.getUser().getEmail());
                row.createCell(2).setCellValue(answer.getCreatedAt().toString());
                setCellValue(row.createCell(3), fieldAnswer, field.getType());
            }
        }

        // Set column widths
        for (int i = 0; i < 4; i++) {
            sheet.setColumnWidth(i, 256 * 25); // Made columns slightly wider
        }
    }

    /**
     * Creates a header cell with a specific style
     */
    private void createHeaderCell(Row row, int column, String value) {
        var cell = row.createCell(column);
        cell.setCellValue(value);

        CellStyle headerStyle = row.getSheet().getWorkbook().createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(headerStyle);
    }

    private void createMetadataSection(Sheet sheet, Group group, Field field) {
        // Title row
        var titleRow = sheet.createRow(METADATA_START_ROW);
        var titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Field Information");
        CellStyle titleStyle = sheet.getWorkbook().createCellStyle();
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleCell.setCellStyle(titleStyle);

        // Metadata rows
        var groupRow = sheet.createRow(METADATA_START_ROW + 1);
        groupRow.createCell(0).setCellValue("Group:");
        groupRow.createCell(1).setCellValue(group.getName());

        var typeRow = sheet.createRow(METADATA_START_ROW + 2);
        typeRow.createCell(0).setCellValue("Field Type:");
        typeRow.createCell(1).setCellValue(field.getType().toString());

        var optionalRow = sheet.createRow(METADATA_START_ROW + 3);
        optionalRow.createCell(0).setCellValue("Optional:");
        optionalRow.createCell(1).setCellValue(field.getOptional() != null ? field.getOptional().toString() : "N/A");

        // description if available
        var descriptionRow = sheet.createRow(METADATA_START_ROW + 4);
        descriptionRow.createCell(0).setCellValue("Description:");
        descriptionRow.createCell(1).setCellValue(field.getLabel());
    }

    private Object findFieldAnswer(AnswerGroup answer, String groupName, String fieldLabel) {
        return answer.getAnswers().stream()
                .filter(subGroup -> groupName.equals(subGroup.getGroup()))
                .flatMap(subGroup -> subGroup.getQuestions().stream())
                .filter(question -> fieldLabel.equals(question.getQuestion()))
                .map(AnswerQuestion::getAnswer)
                .findFirst()
                .orElse(null);
    }

    private void setCellValue(Cell cell, Object value, TypeField type) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        switch (type) {
            case NUMBER:
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    try {
                        cell.setCellValue(Double.parseDouble(value.toString()));
                    } catch (NumberFormatException e) {
                        cell.setCellValue(value.toString());
                    }
                }
                break;
            case MULTIPLE_CHOICE:
                if (value instanceof String[]) {
                    cell.setCellValue(String.join(", ", (String[]) value));
                } else if (value instanceof List) {
                    cell.setCellValue(String.join(", ", ((List<?>) value).stream()
                            .map(Object::toString)
                            .toArray(String[]::new)));
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
            default:
                cell.setCellValue(value.toString());
        }
    }

    private String sanitizeSheetName(String name) {
        // Excel sheet names cannot contain: \ / ? * [ ] :
        return name.replaceAll("[\\\\/?*\\[\\]:]", "_")
                .substring(0, Math.min(name.length(), 31)); // Excel limits sheet to 31 characters
    }
}