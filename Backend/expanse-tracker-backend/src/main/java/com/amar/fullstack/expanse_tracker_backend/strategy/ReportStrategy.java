package com.amar.fullstack.expanse_tracker_backend.strategy;

import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;

import java.io.IOException;

public interface ReportStrategy{


    ReportType getType();
    byte[] generateExcel(Long userId) throws IOException;
    byte[] generatePdf(Long userId) throws IOException;
}
