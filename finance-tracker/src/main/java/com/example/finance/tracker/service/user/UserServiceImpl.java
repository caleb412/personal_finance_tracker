package com.example.finance.tracker.service.user;

import com.example.finance.tracker.dto.UserDTO;
import com.example.finance.tracker.entity.User;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.exception.UserNotFoundException;
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

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public UserDTO postUser(UserDTO userDTO) {
        com.example.finance.tracker.entity.User user = new com.example.finance.tracker.entity.User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        logger.info("Posting new user with ID:{}",userDTO.getId());
        return userRepository.save(user).getUserDto();
    }

    private User saveOrUpdateUser(User user, UserDTO userDTO) {
        logger.debug("Filling new DTO fields for UserDTO: {}",userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        return userRepository.save(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        logger.info("Getting all users...");
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getId).reversed())
                .map(User:: getUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByID(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            logger.info("Getting user with ID {}...", id);
            return optionalUser.get().getUserDto();
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> optionalIncome = userRepository.findById(id);
        logger.info("Updating user with ID {}...",userDTO.getId());
        if (optionalIncome.isPresent()) {
            return saveOrUpdateUser(optionalIncome.get(), userDTO).getUserDto();
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            logger.info("Deleting user with ID {}...",id);
            userRepository.deleteById(id);
        } else {
            logger.error("User with ID {} not found",id);
            throw new UserNotFoundException(id);
        }
    }

}
