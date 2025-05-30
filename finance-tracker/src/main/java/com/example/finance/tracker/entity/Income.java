package com.example.finance.tracker.entity;

import com.example.finance.tracker.dto.IncomeDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String source;
    private String description;
    @Column(nullable = false)
    @PastOrPresent(message = "Date should not be in the future!")
    private LocalDate date;
    @Column(nullable = false)
    @Positive
    private Double amount;

    public IncomeDTO getIncomeDto(){
        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setId(id);
        incomeDTO.setSource(source);
        incomeDTO.setDescription(description);
        incomeDTO.setAmount(amount);
        incomeDTO.setDate(date);
        incomeDTO.setUserId(user.getId());
        return incomeDTO;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
