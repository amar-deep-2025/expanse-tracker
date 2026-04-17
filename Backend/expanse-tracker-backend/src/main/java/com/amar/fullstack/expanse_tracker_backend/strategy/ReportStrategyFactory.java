package com.amar.fullstack.expanse_tracker_backend.strategy;

import com.amar.fullstack.expanse_tracker_backend.entity.ReportType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportStrategyFactory {

    private final Map<ReportType, ReportStrategy> strategyMap=new HashMap<>();

    public ReportStrategyFactory(List<ReportStrategy> strategies){
        for (ReportStrategy strategy:strategies){
            strategyMap.put(strategy.getType(),strategy);
            System.out.println("Type "+strategy.getType().name());
        }
    }

    public ReportStrategy getStrategy(ReportType type){
        ReportStrategy strategy= strategyMap.get(type);

        if (strategy==null){
            throw new RuntimeException("No strategy found for "+type);
        }
        return strategy;


    }
}
