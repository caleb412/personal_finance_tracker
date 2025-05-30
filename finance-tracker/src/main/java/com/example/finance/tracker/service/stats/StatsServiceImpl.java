package com.example.finance.tracker.service.stats;

import com.example.finance.tracker.dto.StatsDTO;
import com.example.finance.tracker.entity.Expense;
import com.example.finance.tracker.entity.Income;
import com.example.finance.tracker.entity.User;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.repository.ExpenseRepository;
import com.example.finance.tracker.repository.IncomeRepository;
import com.example.finance.tracker.repository.UserRepository;
import com.example.finance.tracker.service.income.IncomeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private static final Logger logger = LoggerFactory.getLogger(IncomeServiceImpl.class);

    @Override
    public StatsDTO getUserFinanceStats (Long userId){
        logger.info("Getting User Stats...");
        User user  = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException(userId));
        List<Income> incomes = incomeRepository.findAllByUserId(userId);
        List<Expense> expenses = expenseRepository.findAllByUserId(userId);

        double totalIncome = incomes.stream()
                .mapToDouble(Income::getAmount).sum();
        double totalExpenses = expenses.stream()
                .mapToDouble(Expense::getAmount).sum();
        double netBalance = totalIncome - totalExpenses;

        Map<String, Double> expenseByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Map<String, Double> incomeBySource = incomes.stream()
                .collect(Collectors.groupingBy(
                        Income::getSource,
                        Collectors.summingDouble(Income::getAmount)
                ));

        Map<String, Double> monthlyExpenses = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().toString().substring(0, 7),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Map<String, Double> monthlyIncome = incomes.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getDate().toString().substring(0, 7),
                        Collectors.summingDouble(Income::getAmount)
                ));

        return StatsDTO.builder()
                .userId(userId)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .expenseByCategory(expenseByCategory)
                .incomeBySource(incomeBySource)
                .monthlyExpenses(monthlyExpenses)
                .monthlyIncome(monthlyIncome)
                .build();
    };
}
