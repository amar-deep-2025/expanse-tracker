package com.amar.fullstack.expanse_tracker_backend.strategy;

import com.amar.fullstack.expanse_tracker_backend.dtos.MonthlyDto;
import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.List;

@Component
public class MonthlyReportStrategy implements ReportStrategy {

    private final DashboardService dashboardService;

    public MonthlyReportStrategy(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public ReportType getType() {
        return ReportType.MONTHLY;
    }

    @Override
    public byte[] generateExcel(Long userId) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("YEARLY Report");
            int rowNum = 0;

            int year = Year.now().getValue();
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);

            // Header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Monthly Financial Report - " + year);
            titleCell.setCellStyle(titleStyle);

            rowNum++;

            String[] columns = {"Month", "Income", "Expense"};

            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            List<MonthlyDto> data = dashboardService.getMonthly_ByUserId(userId, year);

            if (data.isEmpty()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue("No Data");
            } else {
                for (MonthlyDto dto : data) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(getMonthName(dto.getMonth()));
                    row.createCell(1).setCellValue(dto.getIncome());
                    row.createCell(2).setCellValue(dto.getExpense());
                }
            }
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public byte[] generatePdf(Long userId) {
        throw new UnsupportedOperationException("PDF not implemented yet");
    }


    private String getMonthName(int month) {
        return switch (month) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Unknown";
        };
    }
}