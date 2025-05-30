package com.example.finance.tracker.entity;


import com.example.finance.tracker.dto.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String username;
    @Column(nullable = false,unique = true)
    @Email(message = "Email invalid")
    private String email;

    public UserDTO getUserDto(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setUsername(username);
        userDTO.setEmail(email);

        return userDTO;
    }

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Expense> expenses;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Income> incomes;
}
