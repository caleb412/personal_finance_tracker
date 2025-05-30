package com.example.finance.tracker.controller;

import com.example.finance.tracker.dto.IncomeDTO;
import com.example.finance.tracker.service.income.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Income Controller")
public class IncomeController {
    Logger logger = LoggerFactory.getLogger(IncomeController.class);
    private final IncomeService incomeService;

    @PostMapping
    @Operation(summary = "Post income")
    public ResponseEntity<IncomeDTO> postIncome(@Valid @RequestBody IncomeDTO incomeDTO){
        IncomeDTO createdIncome = incomeService.postIncome(incomeDTO);
        logger.info("Reached POST /api/income");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncome);
    }
    @GetMapping()
    @Operation(summary = "Get all income")
    public ResponseEntity<List<IncomeDTO>> getAllIncome(){
        logger.info("Reached GET /api/income");
        return ResponseEntity.ok(incomeService.getAllIncome());
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update income")
    public ResponseEntity<IncomeDTO> updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeDTO incomeDTO) {
        logger.info("Reached PUT /api/income/{}",id);
        return ResponseEntity.ok(incomeService.updateIncome(id, incomeDTO));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get income record by ID")
    public ResponseEntity<IncomeDTO> getIncomeById(@PathVariable Long id){
        logger.info("Reached GET /api/income/{}",id);
        return ResponseEntity.ok(incomeService.getIncomeById(id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete income record")
    public ResponseEntity<?> deleteIncome(@PathVariable Long id){
        logger.info("Reached DELETE/api/income/{}",id);
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();

    }
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all income by User")
    public ResponseEntity<List<IncomeDTO>> getAllIncomeByUser(@PathVariable Long userId) {
        logger.info("Requested all income for User ID: {userId}");
        return ResponseEntity.ok(incomeService.getAllIncomeByUser(userId));
    }
    @GetMapping("/user/{userId}/total")
    @Operation(summary = "Get total income by user")
    public ResponseEntity<Double> getTotalIncomeByUser(@PathVariable Long userId) {
        logger.info("Requested total income for User ID {userId}");
        return ResponseEntity.ok(incomeService.getTotalIncomeByUser(userId));
    }
}
