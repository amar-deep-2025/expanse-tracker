package com.amar.fullstack.expanse_tracker_backend.strategy;

import com.amar.fullstack.expanse_tracker_backend.dtos.CategoryDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.RecentExpanseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;
import com.amar.fullstack.expanse_tracker_backend.service.BudgetService;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class FullReportStrategy implements  ReportStrategy{

    private static final Logger logger= LoggerFactory.getLogger(FullReportStrategy.class);
    private final BudgetService budgetService;
    private final DashboardService dashboardService;


    public FullReportStrategy(BudgetService budgetService, DashboardService dashboardService){
        this.budgetService= budgetService;
        this.dashboardService= dashboardService;
    }

    @Override
    public ReportType getType() {
        return ReportType.FULL;
    }

    @Override
    public byte[] generateExcel(Long userId) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Full Report");
            int rowNum = 0;

            // ===== Fetch Data =====
            double income = dashboardService.getTotalIncome(userId);
            double expense = dashboardService.getTotalExpense(userId);
            double budget = budgetService.getTotalBudget(userId);
            double balance = income - expense;

            // ===== Styles =====

            // Title Style
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);

            // Header Style (Blue Background)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Label Style
            CellStyle labelStyle = workbook.createCellStyle();
            Font labelFont = workbook.createFont();
            labelFont.setBold(true);
            labelStyle.setFont(labelFont);

            // ===== TITLE =====
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Comprehensive Financial Report");
            titleCell.setCellStyle(titleStyle);

            rowNum++;

            // ===== SUMMARY =====
            String[] labels = {
                    "Total Income",
                    "Total Expense",
                    "Total Budget",
                    "Balance"
            };

            double[] values = {
                    income, expense, budget, balance
            };

            for (int i = 0; i < labels.length; i++) {
                Row row = sheet.createRow(rowNum++);
                Cell labelCell = row.createCell(0);
                labelCell.setCellValue(labels[i]);
                labelCell.setCellStyle(labelStyle);

                row.createCell(1).setCellValue(values[i]);
            }

            rowNum += 2;

            // ===== CATEGORY SECTION =====
            Row catHeader = sheet.createRow(rowNum++);
            Cell c1 = catHeader.createCell(0);
            Cell c2 = catHeader.createCell(1);

            c1.setCellValue("Category");
            c2.setCellValue("Total Amount");

            c1.setCellStyle(headerStyle);
            c2.setCellStyle(headerStyle);

            LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.now();

            List<CategoryDto> categories =
                    dashboardService.getCategorySummary(userId, start, end);

            if (categories.isEmpty()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue("No Data");
                row.createCell(1).setCellValue(0);
            } else {
                for (CategoryDto dto : categories) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(dto.getCategory());
                    row.createCell(1).setCellValue(dto.getAmount());
                }
            }

            rowNum += 2;

            // ===== RECENT TRANSACTIONS =====
            Row recentHeader = sheet.createRow(rowNum++);

            String[] recentCols = {"Name", "Amount", "Category", "Type"};

            for (int i = 0; i < recentCols.length; i++) {
                Cell cell = recentHeader.createCell(i);
                cell.setCellValue(recentCols[i]);
                cell.setCellStyle(headerStyle);
            }

            List<RecentExpanseDto> recentList =
                    dashboardService.getRecentExpensesByUserId(userId);

            if (recentList.isEmpty()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue("No Data");
            } else {
                for (RecentExpanseDto dto : recentList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(dto.getName());
                    row.createCell(1).setCellValue(dto.getAmount());
                    row.createCell(2).setCellValue(dto.getCategory());
                    row.createCell(3).setCellValue(dto.getType());
                }
            }

            // ===== Auto Size =====
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public byte[] generatePdf(Long userId){
        // For simplicity, we can return a placeholder PDF content
        String pdfContent = "PDF report generation is not implemented yet.";
        return pdfContent.getBytes();
    }
}
