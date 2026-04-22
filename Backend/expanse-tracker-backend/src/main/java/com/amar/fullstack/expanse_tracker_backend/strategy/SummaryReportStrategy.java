package com.amar.fullstack.expanse_tracker_backend.strategy;

import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;
import com.amar.fullstack.expanse_tracker_backend.service.BudgetService;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class SummaryReportStrategy implements ReportStrategy {

    private final DashboardService dashboardService;
    private final BudgetService budgetService;

    public SummaryReportStrategy(DashboardService dashboardService,
                                 BudgetService budgetService) {
        this.dashboardService = dashboardService;
        this.budgetService = budgetService;
    }

    @Override
    public ReportType getType() {
        return ReportType.SUMMARY;
    }

    @Override
    public byte[] generateExcel(Long userId) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Summary");

            double income = dashboardService.getTotalIncome(userId);
            double expense = dashboardService.getTotalExpense(userId);
            double budget = budgetService.getTotalBudget(userId);
            double balance = income - expense;

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            int rowNum = 0;

            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Dashboard Summary");
            titleCell.setCellStyle(headerStyle);

            String[] labels = {
                    "Total Income",
                    "Total Expense",
                    "Total Budget",
                    "Balance"
            };

            double[] values = {
                    income,
                    expense,
                    budget,
                    balance
            };

            for (int i = 0; i < labels.length; i++) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(labels[i]);
                row.createCell(1).setCellValue(values[i]);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(out);
            return out.toByteArray();
        }catch (Exception e){
            throw new IOException("Failed to generate Excel report", e);
        }
    }

    @Override
    public byte[] generatePdf(Long userId) {
        throw new UnsupportedOperationException("PDF not implemented yet");
    }
}