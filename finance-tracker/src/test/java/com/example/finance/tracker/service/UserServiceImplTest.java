package com.example.finance.tracker.service;

import com.example.finance.tracker.dto.UserDTO;
import com.example.finance.tracker.entity.User;
import com.example.finance.tracker.exception.UserNotFoundException;
import com.example.finance.tracker.repository.UserRepository;
import com.example.finance.tracker.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void postUser_shouldSaveAndReturnUserDTO(){
        UserDTO dto = mock(UserDTO.class);
        User savedUser = mock(User.class);


        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(savedUser.getUserDto()).thenReturn(dto);

        UserDTO result = userService.postUser(dto);

        assertNotNull(result);
        assertEquals(dto.getUsername(), result.getUsername());
        assertEquals(dto.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void getAllUsers_shouldReturnListOfUserDTOs(){
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(user1.getUserDto()).thenReturn(mock(UserDTO.class));
        when(user2.getUserDto()).thenReturn(mock(UserDTO.class));

        List<UserDTO> result = userService.getAllUsers();
        assertEquals(2, result.size());
    }
    @Test
    void getUserById_shouldReturnUserDTO_whenUserExists(){
        User user = mock(User.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(user.getUserDto()).thenReturn(mock(UserDTO.class));

        UserDTO result = userService.getUserByID(1L);
        assertNotNull(result);
    }
    @Test
    void getUserByID_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByID(99L)
        );
    }
    @Test
    void updateUser_shouldSaveUpdatedUser_whenUserExists() {
        User existing = mock(User.class);

        UserDTO updatedDTO = mock(UserDTO.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(existing);
        when(existing.getUserDto()).thenReturn(updatedDTO);

        UserDTO result = userService.updateUser(1L, updatedDTO);

        assertNotNull(result);
    }
    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("test");
        dto.setEmail("test@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, dto));
    }
    @Test
    void deleteUser_shouldDelete_whenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
    }
}
