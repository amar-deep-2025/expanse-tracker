package com.amar.fullstack.expanse_tracker_backend.strategy;
import com.amar.fullstack.expanse_tracker_backend.dtos.BudgetResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;
import com.amar.fullstack.expanse_tracker_backend.service.BudgetService;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class BudgetReportStrategy implements ReportStrategy {

    private final BudgetService budgetService;
    private final DashboardService dashboardService;

    public BudgetReportStrategy(BudgetService budgetService,
                                DashboardService dashboardService) {
        this.budgetService = budgetService;
        this.dashboardService = dashboardService;
    }

    @Override
    public ReportType getType() {
        return ReportType.BUDGET;
    }

    @Override
    public byte[] generateExcel(Long userId) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Budget Report");
            int rowNum = 0;

            // ========= STYLES =========
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // ========= SUMMARY =========
            BigDecimal totalBudget = BigDecimal.valueOf(budgetService.getTotalBudget(userId));
            BigDecimal totalExpense = BigDecimal.valueOf(dashboardService.getTotalExpense(userId));

            BigDecimal remaining = totalBudget.subtract(totalExpense);

            BigDecimal usage = totalBudget.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : totalExpense.multiply(BigDecimal.valueOf(100))
                    .divide(totalBudget, 2, RoundingMode.HALF_UP);

            // ========= TITLE =========
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Budget Report");
            titleCell.setCellStyle(titleStyle);

            rowNum++;

            // ========= SUMMARY TABLE =========
            String[] labels = {
                    "Total Budget",
                    "Total Expense",
                    "Remaining Budget",
                    "Usage %"
            };

            BigDecimal[] values = {
                    totalBudget,
                    totalExpense,
                    remaining,
                    usage
            };

            for (int i = 0; i < labels.length; i++) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(labels[i]);
                row.createCell(1).setCellValue(values[i].doubleValue());
            }

            rowNum += 2;

            // ========= HEADER =========
            Row header = sheet.createRow(rowNum++);
            String[] headers = {
                    "Category",
                    "Budget",
                    "Expense",
                    "Remaining",
                    "Usage %",
                    "Status"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ========= DATA =========
            List<BudgetResponseDto> budgets = budgetService.getAllBudgets(userId);

            if (budgets.isEmpty()) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue("No data");
            } else {

                for (BudgetResponseDto b : budgets) {

                    BigDecimal budget = b.getBudget();

                    // ⚠️ IMPORTANT FIX
                    // अभी तुम total expense use कर रहे हो — गलत है CATEGORY के लिए
                    BigDecimal expense;

                    if (b.getType().name().equals("CATEGORY")) {
                        // फिलहाल fallback (future improvement needed)
                        expense = BigDecimal.valueOf(
                                dashboardService.getTotalExpense(userId)
                        );
                    } else {
                        expense = totalExpense;
                    }

                    BigDecimal rem = budget.subtract(expense);

                    BigDecimal use = budget.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : expense.multiply(BigDecimal.valueOf(100))
                            .divide(budget, 2, RoundingMode.HALF_UP);

                    String status;
                    if (budget.compareTo(BigDecimal.ZERO) == 0) {
                        status = "No Budget";
                    } else if (use.compareTo(BigDecimal.valueOf(100)) > 0) {
                        status = "Exceeded ❌";
                    } else if (use.compareTo(BigDecimal.valueOf(80)) >= 0) {
                        status = "Warning ⚠️";
                    } else {
                        status = "Safe ✅";
                    }

                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(
                            b.getCategoryName() != null ? b.getCategoryName() : "Overall"
                    );

                    row.createCell(1).setCellValue(budget.doubleValue());
                    row.createCell(2).setCellValue(expense.doubleValue());
                    row.createCell(3).setCellValue(rem.doubleValue());
                    row.createCell(4).setCellValue(use.doubleValue());
                    row.createCell(5).setCellValue(status);
                }
            }

            // ========= AUTO SIZE =========
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new IOException("Error generating Budget report", e);
        }
    }

    @Override
    public byte[] generatePdf(Long userId) {
        throw new UnsupportedOperationException("PDF not implemented yet");
    }
}

