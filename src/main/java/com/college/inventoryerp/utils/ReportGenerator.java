package com.college.inventoryerp.utils;

//package com.college.inventory.utils;

import com.college.inventoryerp.model.Equipment;
import com.college.inventoryerp.model.IssueRecord;
import com.college.inventoryerp.model.*;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void exportEquipmentToExcel(ObservableList<Equipment> equipment, Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Equipment Report");
        fileChooser.setInitialFileName("equipment_report_" + LocalDate.now().format(DATE_FORMATTER) + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Equipment Report");

                // Create header style
                CellStyle headerStyle = createHeaderStyle(workbook);

                // Create header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"ID", "Name", "Model", "Serial Number", "Purchase Date", "Status", "Category", "Brand", "Description"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Create data rows
                int rowNum = 1;
                for (Equipment eq : equipment) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(eq.getEquipmentId());
                    row.createCell(1).setCellValue(eq.getName());
                    row.createCell(2).setCellValue(eq.getModel());
                    row.createCell(3).setCellValue(eq.getSerialNumber());
                    row.createCell(4).setCellValue(
                            eq.getPurchaseDate() != null ? eq.getPurchaseDate().format(DATE_FORMATTER) : ""
                    );
                    row.createCell(5).setCellValue(eq.getStatus());
                    row.createCell(6).setCellValue(eq.getCategory());
//                    row.createCell(7).setCellValue(eq.getBrand());
                    row.createCell(8).setCellValue(eq.getDescription());
                }

                // Auto-size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write to file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                ValidationUtils.showSuccess("Equipment report exported successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                ValidationUtils.showError("Error exporting equipment report: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void exportIssueRecordsToExcel(ObservableList<IssueRecord> records, Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Issue Records Report");
        fileChooser.setInitialFileName("issue_records_" + LocalDate.now().format(DATE_FORMATTER) + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Issue Records");

                // Create header style
                CellStyle headerStyle = createHeaderStyle(workbook);

                // Create header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Record ID", "Equipment", "Faculty", "Issued By", "Issue Date", "Return Date", "Status", "Notes"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Create data rows
                int rowNum = 1;
                for (IssueRecord record : records) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(record.getRecordId());
                    row.createCell(1).setCellValue(record.getEquipmentName());
                    row.createCell(2).setCellValue(record.getFacultyName());
                    row.createCell(3).setCellValue(record.getEmployeeName());
                    row.createCell(4).setCellValue(
                            record.getIssueDate() != null ? record.getIssueDate().format(DATE_FORMATTER) : ""
                    );
                    row.createCell(5).setCellValue(
                            record.getReturnDate() != null ? record.getReturnDate().format(DATE_FORMATTER) : ""
                    );
                    row.createCell(6).setCellValue(record.getStatus());
                    row.createCell(7).setCellValue(record.getNotes());
                }

                // Auto-size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write to file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                ValidationUtils.showSuccess("Issue records report exported successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                ValidationUtils.showError("Error exporting issue records report: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void exportToCSV(ObservableList<Equipment> equipment, Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Equipment CSV");
        fileChooser.setInitialFileName("equipment_" + LocalDate.now().format(DATE_FORMATTER) + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("ID,Name,Model,Serial Number,Purchase Date,Status,Category,Brand,Description");

                // Write data
                for (Equipment eq : equipment) {
                    writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            eq.getEquipmentId(),
                            eq.getName(),
                            eq.getModel(),
                            eq.getSerialNumber(),
                            eq.getPurchaseDate() != null ? eq.getPurchaseDate().format(DATE_FORMATTER) : "",
                            eq.getStatus(),
                            eq.getCategory(),
//                            eq.getBrand(),
                            eq.getDescription()
                    );
                }

                ValidationUtils.showSuccess("Equipment data exported successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                ValidationUtils.showError("Error exporting CSV: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
}

