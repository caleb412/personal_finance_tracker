package com.example.finance.tracker.entity;

import com.example.finance.tracker.dto.ExpenseDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;
    @NotNull
    @PastOrPresent(message = "Date should not be in the future!")
    private LocalDate date;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String category;

    public ExpenseDTO getExpenseDto(){
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(id);
        expenseDTO.setTitle(title);
        expenseDTO.setDescription(description);
        expenseDTO.setAmount(amount);
        expenseDTO.setDate(date);
        expenseDTO.setCategory(category);
        expenseDTO.setUserId(user.getId());

        return expenseDTO;
    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;
}
