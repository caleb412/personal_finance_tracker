package com.example.finance.tracker.service;

import com.example.finance.tracker.dto.IncomeDTO;
import com.example.finance.tracker.entity.Income;
import com.example.finance.tracker.entity.User;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.exception.UserNotFoundException;
import com.example.finance.tracker.repository.IncomeRepository;
import com.example.finance.tracker.repository.UserRepository;
import com.example.finance.tracker.service.income.IncomeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncomeServiceImplTest {
    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    @Test
    void postIncome_shouldSaveIncome(){
        IncomeDTO dto = new IncomeDTO();
        dto.setId(1L);
        dto.setSource("Salary");
        dto.setDescription("Monthly");
        dto.setDate(LocalDate.of(2025, 1,1));
        dto.setAmount(5000.00);
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);


        Income income = Mockito.mock(Income.class);
        when(income.getIncomeDto()).thenReturn(dto);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(incomeRepository.save(any(Income.class))).thenReturn(income);

        IncomeDTO result = incomeService.postIncome(dto);

        assertNotNull(result);
        assertEquals(dto.getSource(), result.getSource());
        assertEquals(dto.getAmount(), result.getAmount());
        verify(incomeRepository).save(any(Income.class));
    }
    @Test
    void postIncome_throwsUserNotFoundException_whenUserNotFound(){
        IncomeDTO dto = new IncomeDTO();
        dto.setId(1L);
        dto.setSource("Salary");
        dto.setDescription("Monthly");
        dto.setDate(LocalDate.of(2025,1,1));
        dto.setAmount(5000.00);
        dto.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                ()-> incomeService.postIncome(dto),
                "Expected postIncome() to throw UserNotFoundException"
        );
    }
    @Test
    void postIncome_throwsIllegalArgumentException(){
        IncomeDTO dto = new IncomeDTO();
        dto.setId(1L);
        dto.setSource(null);
        dto.setDescription("Negative amount");
        dto.setDate(LocalDate.of(2025,1,1));
        dto.setAmount(-5000.00);
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                ()-> incomeService.postIncome(dto),
                "Expected Income to throw IllegalArgumentException"
        );
        String message = exception.getMessage();
        assertTrue(message.contains("invalid") || message.contains("negative"));
    }
    @Test
    void getAllIncomesByUser_shouldReturnListOfDTOs(){
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);


        Income income1 = mock(Income.class);
        Income income2 = mock(Income.class);

        when(income1.getDate()).thenReturn(LocalDate.of(2025,1,1));
        when(income2.getDate()).thenReturn(LocalDate.of(2025,1,2));

        IncomeDTO dto1 = new IncomeDTO();
        dto1.setId(1L);
        dto1.setSource("Freelance");
        dto1.setAmount(2000.00);
        dto1.setDate(LocalDate.of(2025,1,1));

        IncomeDTO dto2 = new IncomeDTO();
        dto2.setId(2L);
        dto2.setSource("Job");
        dto2.setAmount(5000.00);
        dto2.setDate(LocalDate.of(2025,1,2));

        when(income1.getIncomeDto()).thenReturn(dto1);
        when(income2.getIncomeDto()).thenReturn(dto2);

        List<Income> incomes = Arrays.asList(income1, income2);
        when(incomeRepository.findAllByUserId(userId)).thenReturn(incomes);

        List<IncomeDTO> result = incomeService.getAllIncomeByUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Job", result.get(0).getSource());
        assertEquals("Freelance", result.get(1).getSource());

        verify(userRepository).existsById(userId);
        verify(incomeRepository).findAllByUserId(userId);
    }
    @Test
    void getIncomeById_shouldReturnDTO(){
        Long incomeId = 10L;
        Income income = mock(Income.class);
        IncomeDTO dto = new IncomeDTO();

        dto.setId(incomeId);
        dto.setSource("Bonus");
        dto.setAmount(1500.00);
        dto.setDate(LocalDate.of(2025,3,1));
        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(income));
        when(income.getIncomeDto()).thenReturn(dto);
        IncomeDTO result = incomeService.getIncomeById(incomeId);

        assertNotNull(result);
        assertEquals(incomeId, result.getId());
        assertEquals("Bonus", result.getSource());
        assertEquals(1500.00, result.getAmount());

        verify(incomeRepository).findById(incomeId);
        verify(income).getIncomeDto();
    }
    @Test
    void getIncomeById_shouldReturnResourceNotFoundException_whenIncomeNotFound(){
        Long incomeId = 190123120L;

        when(incomeRepository.findById(incomeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> incomeService.getIncomeById(incomeId)
                );
        verify(incomeRepository).findById(incomeId);
    }
    @Test
    void deleteIncome_shouldThrowResourceNotFoundException_whenNotFound(){
        Long incomeId = 42L;

        when(incomeRepository.findById(incomeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> incomeService.deleteIncome(incomeId));
        verify(incomeRepository, never()).delete(any());
    }
    @Test
    void deleteIncome_shouldDeleteIncome(){
        Long incomeId = 1L;
        Income income = mock(Income.class);
        income.setId(incomeId);

        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(income));

        incomeService.deleteIncome(incomeId);

        verify(incomeRepository).findById(incomeId);
        verify(incomeRepository).deleteById(incomeId);
    }
    @Test
    void getTotalIncomeByUser_shouldReturnTotal(){
        Long userId = 1L;
        Double totalIncome = 15000.0;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(incomeRepository.getTotalIncomeByUserId(userId)).thenReturn(totalIncome);

        Double result = incomeService.getTotalIncomeByUser(userId);

        assertEquals(totalIncome, result);
        verify(userRepository).existsById(userId);
        verify(incomeRepository).getTotalIncomeByUserId(userId);
    }
    @Test
    void getTotalIncomeByUser_shouldReturnZero_whenIncomeIsNull() {
        Long userId = 2L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(incomeRepository.getTotalIncomeByUserId(userId)).thenReturn(null); // simulate no income

        Double result = incomeService.getTotalIncomeByUser(userId);

        assertEquals(0.0, result);
        verify(userRepository).existsById(userId);
        verify(incomeRepository).getTotalIncomeByUserId(userId);
    }
    @Test
    void getTotalIncomeByUser_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long userId = 3L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> incomeService.getTotalIncomeByUser(userId));

        verify(userRepository).existsById(userId);
        verifyNoInteractions(incomeRepository);
    }

}
