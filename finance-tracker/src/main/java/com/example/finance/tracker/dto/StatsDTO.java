package com.example.finance.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StatsDTO {
    private Long userId;
    private Double totalIncome;
    private Double totalExpenses;
    private Double netBalance;
    private Map<String,Double> expenseByCategory;
    private Map<String,Double> incomeBySource;
    private Map<String,Double> monthlyIncome;
    private Map<String,Double> monthlyExpenses;
}

