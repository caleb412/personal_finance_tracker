package com.example.finance.tracker.controller;

import com.example.finance.tracker.dto.ExpenseDTO;
import com.example.finance.tracker.dto.IncomeDTO;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.exception.UserNotFoundException;
import com.example.finance.tracker.service.expense.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @Test
    void postExpense_shouldReturnCreatedIncomeDTO() throws Exception{

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(1L);
        expenseDTO.setUserId(1L);
        expenseDTO.setTitle("Paid for internet");
        expenseDTO.setDescription("Internet bill");
        expenseDTO.setCategory("Bill");
        expenseDTO.setAmount(500.00);
        expenseDTO.setDate(LocalDate.of(2025,1,15));

        ExpenseDTO savedDTO = new ExpenseDTO();
        savedDTO.setId(1L);
        savedDTO.setUserId(1L);
        savedDTO.setTitle("Paid for internet");
        savedDTO.setDescription("Internet bill");
        savedDTO.setCategory("Bill");
        savedDTO.setAmount(500.00);
        savedDTO.setDate(LocalDate.of(2025,1,15));

        when(expenseService.postExpense(expenseDTO)).thenReturn(savedDTO);

        mockMvc.perform(post("/api/expense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.title").value("Paid for internet"));

    }
    @Test
    void postExpense_shouldReturnBadRequest_whenAmountIsMissing() throws Exception{
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setUserId(1L);
        expenseDTO.setTitle("Test expense");
        expenseDTO.setDate(LocalDate.of(2025,4,1));
        expenseDTO.setAmount(null);

        mockMvc.perform(post("/api/expense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseDTO)))
                .andExpect(status().isBadRequest());

    }
    @Test
    void getAllExpensesByUser_shouldReturnUserNotFound_whenUserDoesNotExist()throws Exception{
        Long userId = 999L;

        when(expenseService.getAllExpensesByUser(userId)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/api/expense/user/{userId}",userId))
                .andExpect(status().isNotFound());

    }
    @Test
    void getAllExpenses_shouldReturnListOfExpenseDTOs() throws Exception{
        ExpenseDTO expense1 = mock(ExpenseDTO.class);
        ExpenseDTO expense2 = mock(ExpenseDTO.class);

        List<ExpenseDTO> expenseDTOList = List.of(expense1,expense2);

        when(expenseService.getAllExpenses()).thenReturn(expenseDTOList);

        mockMvc.perform(get("/api/expense"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

    }
    @Test
    void getExpenseById_shouldReturnExpenseDTO() throws Exception{
        Long expenseId = 1L;
        ExpenseDTO dto = new ExpenseDTO();

        dto.setId(expenseId);
        dto.setUserId(1L);
        dto.setTitle("testExpense");
        dto.setDescription("expense Description");
        dto.setCategory("Other");
        dto.setAmount(5000.00);
        dto.setDate(LocalDate.of(2025,1,1));

        when(expenseService.getExpenseById(expenseId)).thenReturn(dto);

        mockMvc.perform(get("/api/expense/{id}",expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expenseId));

    }
    @Test
    void getExpenseById_throwsResourceNotFound_whenIncomeNotFound() throws Exception{
        Long expenseId = 999L;

        when(expenseService.getExpenseById(expenseId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/expense/{id}",expenseId))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteExpense_shouldReturnVoid_whenExpenseExists() throws Exception{
        Long expenseId = 1L;

        doNothing().when(expenseService).deleteExpense(expenseId);

        mockMvc.perform(delete("/api/expense/{id}",expenseId))
                .andExpect(status().isNoContent());
    }

}
