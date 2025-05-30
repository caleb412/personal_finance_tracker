package com.example.finance.tracker.service.income;

import com.example.finance.tracker.dto.IncomeDTO;
import com.example.finance.tracker.entity.Income;

import java.util.List;

public interface IncomeService {
    IncomeDTO postIncome(IncomeDTO incomeDTO);
    IncomeDTO updateIncome(Long id, IncomeDTO incomeDTO);
    List<IncomeDTO> getAllIncome();
    IncomeDTO getIncomeById(Long id);
    void deleteIncome(Long id);
    List<IncomeDTO> getAllIncomeByUser(Long userId);
    Double getTotalIncomeByUser(Long userId);

}
