package com.example.finance.tracker.service.user;

import com.example.finance.tracker.dto.UserDTO;
import com.example.finance.tracker.entity.User;

import java.util.List;

public interface UserService {
    UserDTO postUser(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserByID(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}
