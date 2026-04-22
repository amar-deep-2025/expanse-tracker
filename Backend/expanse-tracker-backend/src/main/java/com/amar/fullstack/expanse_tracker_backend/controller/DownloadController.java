package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.ReportService;
import com.amar.fullstack.expanse_tracker_backend.strategy.ReportStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.IOException;

@RestController
@RequestMapping("/api/report/download")
public class DownloadController {

    private final ReportService reportService;

    public DownloadController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<byte[]> downloadReport(
            Authentication auth,
            @RequestParam String format        // excel / pdf// SUMMARY / FULL
    ) throws IOException {

        User user = (User) auth.getPrincipal();
        Long userId = user.getId();

        byte[] data = reportService.generateReport(format, ReportType.SUMMARY, userId);

        String summaryFileName = format.equalsIgnoreCase("excel")
                ? "summary_report.xlsx"
                : "summary_report.pdf";


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + summaryFileName)
                .header(HttpHeaders.CONTENT_TYPE,
                        format.equalsIgnoreCase("excel")
                                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                : "application/pdf")
                .body(data);

    }
    @GetMapping("/category")
    public ResponseEntity<byte[]> downloadCategoryReport(Authentication auth,
                                                         @RequestParam String format) throws IOException
    {
        User user = (User) auth.getPrincipal();
        Long userId = user.getId();

        byte[] data = reportService.generateReport(format, ReportType.CATEGORY, userId);

        String categoryFileName = format.equalsIgnoreCase("excel")
                ? "category_report.xlsx"
                : "category_report.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+categoryFileName)
                .header(HttpHeaders.CONTENT_TYPE,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }

    @GetMapping("/full")
    public ResponseEntity<byte[]> downloadFullReport(
            Authentication auth,
            @RequestParam String format) throws IOException {

        User user = (User) auth.getPrincipal();
        Long userId = user.getId();

        byte[] data = reportService.generateReport(
                format,
                ReportType.FULL,
                userId
        );

        String fileName = format.equalsIgnoreCase("excel")
                ? "full_report.xlsx"
                : "full_report.pdf";

        String contentType = format.equalsIgnoreCase("excel")
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "application/pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }

    @GetMapping("/YEARLY")
    public ResponseEntity<byte[]> downloadMonthlyReport(
            Authentication auth,
            @RequestParam String format,
            @RequestParam int year) throws IOException {

        User user = (User) auth.getPrincipal();
        Long userId = user.getId();
        byte[] data = reportService.generateReport(
                format,
                ReportType.YEARLY,
                userId
        );

        String fileName = format.equalsIgnoreCase("excel")
                ? "YEARLY_report.xlsx"
                : "YEARLY_report.pdf";

        String contentType = format.equalsIgnoreCase("excel")
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "application/pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }

    @GetMapping("/yearly")
    public ResponseEntity<byte[]> downloadYearlyReport(
            Authentication auth,
            @RequestParam String format,
            @RequestParam int year) throws IOException {

        User user = (User) auth.getPrincipal();
        Long userId = user.getId();
        byte[] data = reportService.generateReport(
                format,
                ReportType.MONTHLY,
                userId
        );

        String fileName = format.equalsIgnoreCase("excel")
                ? "yearly_report.xlsx"
                : "yearly_report.pdf";

        String contentType = format.equalsIgnoreCase("excel")
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "application/pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }

    @GetMapping("/budget")
    public ResponseEntity<byte[]> downloadBudgetReport(
            @RequestParam String format,
            Authentication auth) throws IOException {

        Long userId = ((User) auth.getPrincipal()).getId();

        byte[] data = reportService.generateReport(
                format,
                ReportType.BUDGET,
                userId
        );

        String fileName = format.equalsIgnoreCase("excel")
                ? "budget_report.xlsx"
                : "budget_report.pdf";

        String contentType = format.equalsIgnoreCase("excel")
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "application/pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }

}