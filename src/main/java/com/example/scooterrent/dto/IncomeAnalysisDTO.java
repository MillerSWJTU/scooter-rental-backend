package com.example.scooterrent.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class IncomeAnalysisDTO {
    private Map<String, BigDecimal> incomeByType;
    private Map<String, BigDecimal> dailyIncome;
    private Map<String, Map<String, BigDecimal>> dailyIncomeByType;

    public IncomeAnalysisDTO(Map<String, BigDecimal> incomeByType,
                           Map<String, BigDecimal> dailyIncome,
                           Map<String, Map<String, BigDecimal>> dailyIncomeByType) {
        this.incomeByType = incomeByType;
        this.dailyIncome = dailyIncome;
        this.dailyIncomeByType = dailyIncomeByType;
    }
} 