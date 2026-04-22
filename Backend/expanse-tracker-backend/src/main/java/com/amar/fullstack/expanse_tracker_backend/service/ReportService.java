package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;
import com.amar.fullstack.expanse_tracker_backend.entity.*;
import com.amar.fullstack.expanse_tracker_backend.strategy.ReportStrategy;
import com.amar.fullstack.expanse_tracker_backend.strategy.ReportStrategyFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ReportService {

    private final ReportStrategyFactory factory;

    public ReportService(ReportStrategyFactory factory) {
        this.factory = factory;
    }

    public byte[] generateReport(String format, ReportType type, Long userId) throws IOException {

        ReportStrategy strategy = factory.getStrategy(type);

        if (format.equalsIgnoreCase("excel")) {
            return strategy.generateExcel(userId);
        } else if (format.equalsIgnoreCase("pdf")) {
            return strategy.generatePdf(userId);
        }

        throw new RuntimeException("Invalid format");
    }
}