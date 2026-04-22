package com.amar.fullstack.expanse_tracker_backend.strategy;


import com.amar.fullstack.expanse_tracker_backend.dtos.CategoryDto;
import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import com.amar.fullstack.expanse_tracker_backend.service.ExpanseService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.weaver.Lint;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CategoryReportStrategy implements  ReportStrategy{


    private final DashboardService dashboardService;
    public CategoryReportStrategy(ExpanseService expanseService,DashboardService dashboardService){
        this.dashboardService= dashboardService;
    }

    @Override
    public ReportType getType() {
        return ReportType.CATEGORY;
    }

    @Override
    public byte[] generateExcel(Long userId) throws IOException {
        try(Workbook workbook=new XSSFWorkbook();
            ByteArrayOutputStream out=new ByteArrayOutputStream()
        ){
            Sheet sheet=workbook.createSheet("Category Report");

            Row header=sheet.createRow(0);
            header.createCell(0).setCellValue("Category");
            header.createCell(1).setCellValue("Total Expanse");

            LocalDateTime start=LocalDateTime.of(2000,1,1,0,0);
            LocalDateTime end=LocalDateTime.now();

            List<CategoryDto> data=dashboardService.getCategorySummary(userId,start,end);

            int rowIdx=1;

            if (data.isEmpty()){
                Row row=sheet.createRow(rowIdx);
                row.createCell(0).setCellValue("No data");
                row.createCell(1).setCellValue(0);
            }else{
                for (CategoryDto dto:data){
                    Row row=sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(dto.getCategory());
                    row.createCell(1).setCellValue(dto.getAmount());
                }
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(out);
            return out.toByteArray();

        }catch (IOException e){
            throw new RuntimeException("Error generating Excel report", e);
        }
    }

    @Override
    public byte[] generatePdf(Long userId) throws IOException {
        // For simplicity, we return an empty PDF. Implementing PDF generation is more complex and would require a library like iText or Apache PDFBox.
        throw new UnsupportedOperationException("PDF not implemented Yet");
    }
}
