package com.example.finance.tracker.service.expense;

import com.example.finance.tracker.dto.ExpenseDTO;
import com.example.finance.tracker.entity.Expense;

import java.util.List;


public interface ExpenseService {

    ExpenseDTO postExpense(ExpenseDTO expenseDTO);
    List<ExpenseDTO> getAllExpenses();
    ExpenseDTO getExpenseById(Long id);
    ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO);
    void deleteExpense(Long id);
    List<ExpenseDTO> getAllExpensesByUser(Long userId);
    Double getTotalExpenseByUser(Long userId);
}
