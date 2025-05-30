package com.example.finance.tracker.service;

import com.example.finance.tracker.dto.ExpenseDTO;
import com.example.finance.tracker.entity.Expense;
import com.example.finance.tracker.entity.User;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.exception.UserNotFoundException;
import com.example.finance.tracker.repository.ExpenseRepository;
import com.example.finance.tracker.repository.UserRepository;
import com.example.finance.tracker.service.expense.ExpenseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceImplTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;
    @Test
    void postExpenses_shouldSaveExpense(){
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(1L);
        dto.setCategory("Food");
        dto.setDescription("Lunch");
        dto.setDate(Date.valueOf("2025-01-01").toLocalDate());
        dto.setAmount(250.00);
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);

        Expense savedExpense = mock(Expense.class); // Mocking Expense
        ExpenseDTO expectedDto = new ExpenseDTO();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);
        when(savedExpense.getExpenseDto()).thenReturn(expectedDto); // Mock DTO conversion


        ExpenseDTO result = expenseService.postExpense(dto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userRepository).findById(1L);
        verify(expenseRepository).save(any(Expense.class));
        verify(savedExpense).getExpenseDto();
    }
    @Test
    void postExpense_shouldThrowUserNotFoundException_whenUserNotFound(){
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(1L);
        dto.setCategory("Food");
        dto.setTitle("Bought food");
        dto.setDescription("Bought food on campus");
        dto.setDate(Date.valueOf("2025-01-01").toLocalDate());
        dto.setAmount(5000.00);
        dto.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                ()-> expenseService.postExpense(dto),
                "Expected postIncome() to throw UserNotFoundException"
        );

    }
    @Test
    void getExpenseById_shouldThrowResourceNotFoundException_whenExpenseNotFound() {
        Long expenseId = 999L;

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                expenseService.getExpenseById(expenseId)
        );
    }
    @Test
    void getExpenseById_shouldReturnDTO() {
        Long expenseId = 1L;
        Expense expense = mock(Expense.class);
        ExpenseDTO dto = new ExpenseDTO();

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(expense.getExpenseDto()).thenReturn(dto);

        ExpenseDTO result = expenseService.getExpenseById(expenseId);

        assertEquals(dto, result);
        verify(expenseRepository).findById(expenseId);
    }
    @Test
    void getAllExpenseByUser_shouldReturnListOfDTOs(){
        Long userId = 1L;

        Expense expense1 = mock(Expense.class);
        Expense expense2 = mock(Expense.class);
        ExpenseDTO dto1 = new ExpenseDTO();
        ExpenseDTO dto2 = new ExpenseDTO();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(expenseRepository.findAllByUserId(userId)).thenReturn(List.of(expense1, expense2));
        when(expense1.getDate()).thenReturn(Date.valueOf("2025-05-01").toLocalDate());
        when(expense2.getDate()).thenReturn(Date.valueOf("2025-01-01").toLocalDate());
        when(expense1.getExpenseDto()).thenReturn(dto1);
        when(expense2.getExpenseDto()).thenReturn(dto2);

        List<ExpenseDTO> result = expenseService.getAllExpensesByUser(userId);

        assertEquals(2, result.size());
        verify(expenseRepository).findAllByUserId(userId);
    }

    @Test
    void getAllExpenseByUser_shouldReturnUserNotFoundException_whenUserNotFound(){
        Long userId = 99999L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                ()-> expenseService.getTotalExpenseByUser(userId));

        verifyNoInteractions(expenseRepository);

    }
    @Test
    void deleteExpense_shouldDeleteExpense(){
        Long expenseId = 1L;
        when(expenseRepository.existsById(expenseId)).thenReturn(true);

        expenseService.deleteExpense(expenseId);

        verify(expenseRepository).existsById(expenseId);
        verify(expenseRepository).deleteById(expenseId);
    }
    @Test
    void updateExpense_shouldUpdateAndReturnDTO_whenExpenseExists(){
        Long expenseId = 1L;
        Long userId = 1L;
        User user = mock(User.class);
        user.setId(userId);
        ExpenseDTO inputDto = mock(ExpenseDTO.class);
        when(inputDto.getId()).thenReturn(expenseId);
        when(inputDto.getUserId()).thenReturn(userId);
        when(inputDto.getAmount()).thenReturn(100.0);
        when(inputDto.getCategory()).thenReturn("Food");
        when(inputDto.getDate()).thenReturn(LocalDate.of(2025, 5, 25));

        Expense existingExpense = mock(Expense.class);
        Expense savedExpense = mock(Expense.class);

        ExpenseDTO returnedDto = new ExpenseDTO();
        returnedDto.setId(expenseId);
        returnedDto.setUserId(userId);
        returnedDto.setAmount(100.0);
        returnedDto.setCategory("Food");
        returnedDto.setDate(LocalDate.of(2025, 5, 25));

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);
        when(savedExpense.getExpenseDto()).thenReturn(returnedDto);

        ExpenseDTO result = expenseService.updateExpense(expenseId, inputDto);

        assertNotNull(result);
        assertEquals(100.0, result.getAmount());
        assertEquals("Food", result.getCategory());

        verify(expenseRepository).findById(expenseId);
        verify(expenseRepository).save(any(Expense.class));
        verify(savedExpense).getExpenseDto();
    }
    @Test
    void updateExpense_shouldThrowException_whenExpenseNotFound() {
        Long expenseId = 99L;
        ExpenseDTO inputDto = mock(ExpenseDTO.class);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.updateExpense(expenseId, inputDto);
        });

        verify(expenseRepository).findById(expenseId);
        verify(expenseRepository, never()).save(any());
    }
    @Test
    void getTotalExpenseByUser_shouldReturnTotal(){
        Long userId = 1L;
        Double totalExpense = 1500.0;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(expenseRepository.getTotalExpenseByUserId(userId)).thenReturn(totalExpense);
        Double result = expenseService.getTotalExpenseByUser(userId);

        assertEquals(totalExpense, result);
        verify(userRepository).existsById(userId);
        verify(expenseRepository).getTotalExpenseByUserId(userId);
    }
    @Test
    void getTotalIncomeByUser_shouldReturnTotal(){
        Long userId = 1L;
        Double totalExpense = 15000.0;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(expenseRepository.getTotalExpenseByUserId(userId)).thenReturn(totalExpense);

        Double result = expenseService.getTotalExpenseByUser(userId);

        assertEquals(totalExpense, result);
        verify(userRepository).existsById(userId);
        verify(expenseRepository).getTotalExpenseByUserId(userId);
    }
    @Test
    void getTotalIncomeByUser_shouldReturnZero_whenIncomeIsNull() {
        Long userId = 2L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(expenseRepository.getTotalExpenseByUserId(userId)).thenReturn(null); // simulate no income

        Double result = expenseService.getTotalExpenseByUser(userId);

        assertEquals(0.0, result);
        verify(userRepository).existsById(userId);
        verify(expenseRepository).getTotalExpenseByUserId(userId);
    }
    @Test
    void getTotalIncomeByUser_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long userId = 3L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> expenseService.getTotalExpenseByUser(userId));

        verify(userRepository).existsById(userId);
        verifyNoInteractions(expenseRepository);
    }



}

