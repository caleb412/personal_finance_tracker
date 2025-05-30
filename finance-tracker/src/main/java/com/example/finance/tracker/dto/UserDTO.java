package com.example.finance.tracker.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    @Column(nullable = false,unique = true)
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @Column(nullable = false,unique = true)
    @NotBlank(message ="Email must not be empty")
    @Email(message = "Incorrect email format")
    private String email;
}
