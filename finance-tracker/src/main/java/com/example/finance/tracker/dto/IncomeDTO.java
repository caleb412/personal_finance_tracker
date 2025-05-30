package com.example.finance.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeDTO {
    private Long id;
    @NotBlank(message = "Source is required")
    private String source;
    private String description;
    @NotNull(message = "Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Date should not be in the future!")
    private LocalDate date;
    @Column(nullable = false)
    @NotNull
    private Double amount;
    @NotNull
    private Long userId;
}
