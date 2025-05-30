package com.example.finance.tracker.controller;

import com.example.finance.tracker.dto.ExpenseDTO;
import com.example.finance.tracker.service.expense.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Expense Controller")

public class ExpenseController {
    Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    private final ExpenseService expenseService;

    @Operation(summary = "Post an expense")
    @PostMapping
    public ResponseEntity<ExpenseDTO> postExpense(@Valid @RequestBody ExpenseDTO expenseDTO){
        ExpenseDTO createdExpense = expenseService.postExpense(expenseDTO);
        logger.info("Reached POST /api/expense");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);

    }
    @Operation(summary = "Get all expenses")
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(){
        logger.info("Reached GET /api/expense");
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }
    @Operation(summary = "Get expense record by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long id){
        logger.info("Reached GET /api/expense/{}",id);
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }
    @Operation(summary = "Update expense")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseDTO expenseDTO){
        logger.info("Reached PUT /api/expense/{}",id);
        return ResponseEntity.ok(expenseService.updateExpense(id, expenseDTO));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense record")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id){
        logger.info("Reached DELETE/api/expense/{}",id);
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Get all expenses by User")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getAllExpensesByUser(@PathVariable Long userId) {
        logger.info("Requested all expenses for User ID: {userId}");
        return ResponseEntity.ok(expenseService.getAllExpensesByUser(userId));
    }
    @Operation(summary = "Get total expenses by user")
    @GetMapping("/user/{userId}/total")
    public ResponseEntity<Double> getTotalExpenseByUser(@PathVariable Long userId) {
        logger.info("Requested total expenses for User ID {userId}");
        return ResponseEntity.ok(expenseService.getTotalExpenseByUser(userId));
    }
}

