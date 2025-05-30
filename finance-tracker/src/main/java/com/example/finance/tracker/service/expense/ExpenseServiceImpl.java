package com.example.finance.tracker.service.expense;

import com.example.finance.tracker.dto.ExpenseDTO;
import com.example.finance.tracker.entity.Expense;
import com.example.finance.tracker.entity.User;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.exception.UserNotFoundException;
import com.example.finance.tracker.repository.ExpenseRepository;
import com.example.finance.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService{
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ExpenseServiceImpl.class);
    @Override
    public ExpenseDTO postExpense(ExpenseDTO expenseDTO){
        logger.info("Posting new expense for user ID:{}",expenseDTO.getUserId());
        Expense savedExpense = saveOrUpdateExpense(new Expense(), expenseDTO);
        logger.info("New expense posted with ID:{}",savedExpense.getId());
        return savedExpense.getExpenseDto();
    }
    private Expense saveOrUpdateExpense(Expense expense, ExpenseDTO expenseDTO){
        logger.debug("Filling new DTO fields for ExpenseDTO: {}",expenseDTO.getId());
        if (expenseDTO.getDate() == null || expenseDTO.getAmount() < 0) {
            throw new IllegalArgumentException("Invalid input: source must not be null and amount must be non-negative.");
        }
        expense.setTitle(expenseDTO.getTitle());
        expense.setDate(expenseDTO.getDate());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDescription(expenseDTO.getDescription());
        expense.setCategory(expenseDTO.getCategory());
        User user = userRepository.findById(expenseDTO.getUserId())
                .orElseThrow(() -> {
                    logger.error("User with ID {}  not found",expenseDTO.getUserId());
                    return new UserNotFoundException(expenseDTO.getUserId());
                });
        expense.setUser(user);
        logger.debug("Saving expense to repository...");
        return expenseRepository.save(expense);
    }
    @Override
    public List<ExpenseDTO> getAllExpenses(){
        logger.info("Getting all expenses...");
        return expenseRepository.findAll().stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .map(Expense::getExpenseDto)
                .collect(Collectors.toList());
    }
    @Override
    public ExpenseDTO getExpenseById(Long id){

        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if(optionalExpense.isPresent()){
            logger.info("Getting expense with ID {}...",id);
            return optionalExpense.get().getExpenseDto();
        }else{
            throw new ResourceNotFoundException(id);
        }
    }
    @Override
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO){
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        logger.info("Updating expense with ID {}...",expenseDTO.getId());
        if(optionalExpense.isPresent()){
            return saveOrUpdateExpense(optionalExpense.get(),expenseDTO).getExpenseDto();
        }else{
            throw new ResourceNotFoundException(id);
        }
    }
    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            logger.error("Expense with ID {} not found",id);
            throw new ResourceNotFoundException(id);
        }
        logger.info("Deleting expense with ID {}...",id);
        expenseRepository.deleteById(id);
    }
    @Override
    public List<ExpenseDTO> getAllExpensesByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            logger.error("Cannot get all expenses for User with ID {}", userId);
            throw new UserNotFoundException(userId);
        }
        logger.info("Getting expenses for user with ID {}",userId);
        return expenseRepository.findAllByUserId(userId).stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .map(Expense::getExpenseDto)
                .collect(Collectors.toList());
    }
    @Override
    public Double getTotalExpenseByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            logger.error("Cannot get total expenses. User with ID {} was not found", userId);
            throw new UserNotFoundException(userId);
        }
        logger.info("Getting total expenses for user with ID {}",userId);
        return Optional.ofNullable(expenseRepository.getTotalExpenseByUserId(userId)).orElse(0.0);
    }

}
