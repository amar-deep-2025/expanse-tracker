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
import java.time.Year;
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

            // ================= STYLES =================

            CellStyle titleStyle = workbook.createCellStyle();

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);

            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            headerStyle.setFont(headerFont);

            // ================= SUMMARY =================

            BigDecimal totalBudget =
                    BigDecimal.valueOf(budgetService.getTotalBudget(userId));

            BigDecimal totalExpense =
                    BigDecimal.valueOf(dashboardService.getTotalExpense(userId));

            BigDecimal remaining =
                    totalBudget.subtract(totalExpense);

            BigDecimal usage =
                    totalBudget.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : totalExpense
                            .multiply(BigDecimal.valueOf(100))
                            .divide(
                                    totalBudget,
                                    2,
                                    RoundingMode.HALF_UP
                            );

            // ================= TITLE =================

            Row titleRow = sheet.createRow(rowNum++);

            Cell titleCell = titleRow.createCell(0);

            int year = Year.now().getValue();

            titleCell.setCellValue(
                    "Budget Report - " + year
            );

            titleCell.setCellStyle(titleStyle);

            rowNum++;

            // ================= SUMMARY TABLE =================

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

                row.createCell(0)
                        .setCellValue(labels[i]);

                row.createCell(1)
                        .setCellValue(values[i].doubleValue());
            }

            rowNum += 2;

            // ================= DETAIL HEADER =================

            Row header = sheet.createRow(rowNum++);

            String[] headers = {
                    "Month",
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

            // ================= BUDGET DATA =================

            List<BudgetResponseDto> budgets =
                    budgetService.getAllBudgets(userId);

            if (budgets.isEmpty()) {

                Row row = sheet.createRow(rowNum);

                row.createCell(0)
                        .setCellValue("No data");

            } else {

                for (BudgetResponseDto b : budgets) {

                    BigDecimal budget = b.getBudget();

                    // ================= MONTH-WISE EXPENSE =================

                    BigDecimal expense;

                    if (b.getMonth() != null && b.getYear() != null) {

                        expense =
                                dashboardService.getTotalExpenseByMonth(
                                        userId,
                                        b.getYear(),
                                        b.getMonth()
                                );

                    } else {

                        expense = totalExpense;
                    }

                    // ================= REMAINING =================

                    BigDecimal rem =
                            budget.subtract(expense);

                    // ================= USAGE =================

                    BigDecimal use =
                            budget.compareTo(BigDecimal.ZERO) == 0
                                    ? BigDecimal.ZERO
                                    : expense
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(
                                            budget,
                                            2,
                                            RoundingMode.HALF_UP
                                    );

                    // ================= STATUS =================

                    String status;

                    if (budget.compareTo(BigDecimal.ZERO) == 0) {

                        status = "No Budget";

                    } else if (
                            use.compareTo(
                                    BigDecimal.valueOf(100)
                            ) > 0
                    ) {

                        status = "Exceeded ❌";

                    } else if (
                            use.compareTo(
                                    BigDecimal.valueOf(80)
                            ) >= 0
                    ) {

                        status = "Warning ⚠️";

                    } else {

                        status = "Safe ✅";
                    }

                    // ================= CREATE ROW =================

                    Row row = sheet.createRow(rowNum++);

                    // Month

                    row.createCell(0)
                            .setCellValue(
                                    b.getMonth() != null
                                            ? getMonthName(b.getMonth())
                                            : "Overall"
                            );

                    // Category

                    row.createCell(1)
                            .setCellValue(
                                    b.getCategoryName() != null
                                            ? b.getCategoryName()
                                            : "Overall"
                            );

                    // Budget

                    row.createCell(2)
                            .setCellValue(
                                    budget.doubleValue()
                            );

                    // Expense

                    row.createCell(3)
                            .setCellValue(
                                    expense.doubleValue()
                            );

                    // Remaining

                    row.createCell(4)
                            .setCellValue(
                                    rem.doubleValue()
                            );

                    // Usage %

                    row.createCell(5)
                            .setCellValue(
                                    use.doubleValue()
                            );

                    // Status

                    row.createCell(6)
                            .setCellValue(status);
                }
            }

            // ================= AUTO SIZE =================

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            // ================= WRITE EXCEL =================

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {

            throw new IOException(
                    "Error generating Budget report",
                    e
            );
        }
    }

    // ================= PDF =================

    @Override
    public byte[] generatePdf(Long userId) {

        throw new UnsupportedOperationException(
                "PDF not implemented yet"
        );
    }

    // ================= MONTH NAME =================

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