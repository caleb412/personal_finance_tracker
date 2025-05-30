package com.example.finance.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;


@Data
@Schema(name = "ExpenseDTO", description = "Expense data transfer object")
public class ExpenseDTO {
    @Schema(description = "Expense ID", example = "1")
    private Long id;

    @Schema(description = "Expense title", example = "Groceries")
    @NotNull(message = "Title is required")
    private String title;

    @Schema(description = "Expense description", example = "Weekly grocery shopping")
    private String description;

    @NotNull(message = "Date is required")
    @Schema(description = "Expense date", example = "2023-10-15")
    @PastOrPresent(message = "Date should not be in the future!")
    private LocalDate date;

    @Schema(description = "Expense category", example = "Food")
    @Column(nullable = false)
    private String category;

    @Positive(message = "Amount must be positive")
    @Schema(description = "Expense amount", example = "50.00")
    @NotNull(message = "Amount cannot be blank")
    private Double amount;

    @Schema(description = "User ID associated with expense", example = "123")
    private Long userId;
}
