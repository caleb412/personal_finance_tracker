package com.example.finance.tracker.service;

import com.example.finance.tracker.dto.StatsDTO;
import com.example.finance.tracker.entity.Expense;
import com.example.finance.tracker.entity.Income;
import com.example.finance.tracker.entity.User;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.repository.ExpenseRepository;
import com.example.finance.tracker.repository.IncomeRepository;
import com.example.finance.tracker.repository.UserRepository;
import com.example.finance.tracker.service.income.IncomeServiceImpl;
import com.example.finance.tracker.service.stats.StatsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatsServiceImplTest {
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StatsServiceImpl statsService;
    private IncomeServiceImpl incomeService;
    @Test
    void getUserFinanceStats_shouldReturnCorrectStats(){
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Income income1 = new Income();
        income1.setId(1L);
        income1.setSource("Salary");
        income1.setDescription("Monthly");
        income1.setDate(LocalDate.of(2025,1,1));
        income1.setAmount(3000.00);
        income1.setUser(user);
        Income income2 = new Income();
        income2.setId(2L);
        income2.setSource("Freelance");
        income2.setDescription("Side gig");
        income2.setDate(LocalDate.of(2025,1,15));
        income2.setAmount(1000.00);
        income2.setUser(user);

        Expense expense1 = new Expense();
        expense1.setId(1L);
        expense1.setTitle("Food");
        expense1.setDescription("bought groceries");
        expense1.setCategory("Groceries");
        expense1.setDate(LocalDate.of(2025,1,1));
        expense1.setAmount(500.0);
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setTitle("Rent");
        expense2.setDescription("paidRent");
        expense2.setCategory("Rent");
        expense2.setDate(LocalDate.of(2025,1,1));
        expense2.setAmount(1000.0);
        expense2.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(incomeRepository.findAllByUserId(userId)).thenReturn(List.of(income1, income2));
        when(expenseRepository.findAllByUserId(userId)).thenReturn(List.of(expense1, expense2));


        StatsDTO stats = statsService.getUserFinanceStats(userId);

        assertEquals(userId, stats.getUserId());
        assertEquals(4000.0, stats.getTotalIncome());
        assertEquals(1500.0, stats.getTotalExpenses());
        assertEquals(2500.0, stats.getNetBalance());

        assertEquals(2, stats.getIncomeBySource().size());
        assertEquals(2, stats.getExpenseByCategory().size());
        assertEquals(1, stats.getMonthlyIncome().size());
        assertEquals(1, stats.getMonthlyExpenses().size());

        verify(userRepository).findById(userId);
        verify(incomeRepository).findAllByUserId(userId);
        verify(expenseRepository).findAllByUserId(userId);


    }
    @Test
    void getUserFinanceStats_shouldThrowResourceNotFound_whenUserDoesNotExist() {

        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            statsService.getUserFinanceStats(userId);
        });

        verify(userRepository).findById(userId);
        verifyNoInteractions(incomeRepository);
        verifyNoInteractions(expenseRepository);
    }
}
