package com.example.finance.tracker.controller;


import com.example.finance.tracker.dto.IncomeDTO;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.exception.UserNotFoundException;
import com.example.finance.tracker.service.income.IncomeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncomeController.class)
public class IncomeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IncomeService incomeService;


    @Test
    void postIncome_shouldReturnCreatedIncomeDTO() throws Exception{

        IncomeDTO inputDTO = new IncomeDTO();
        inputDTO.setUserId(1L);
        inputDTO.setSource("Salary");
        inputDTO.setDescription("Monthly pay");
        inputDTO.setAmount(59000.00);
        inputDTO.setDate(LocalDate.of(2025,1,1));

        IncomeDTO savedDTO = new IncomeDTO();
        savedDTO.setId(100L);
        savedDTO.setUserId(1L);
        savedDTO.setSource("Salary");
        savedDTO.setDescription("Monthly pay");
        savedDTO.setAmount(59000.00);
        savedDTO.setDate(LocalDate.of(2025,1,1));

        when(incomeService.postIncome(inputDTO)).thenReturn(savedDTO);

        mockMvc.perform(post("/api/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.source").value("Salary"))
                .andExpect(jsonPath("$.description").value("Monthly pay"))
                .andExpect(jsonPath("$.date").value("2025-01-01"))
                .andExpect(jsonPath("$.amount").value(59000.00));

    }
    @Test
    void postIncome_shouldReturnBadRequest_whenAmountIsMissing() throws Exception{
        IncomeDTO inputDTO = new IncomeDTO();
        inputDTO.setUserId(1L);
        inputDTO.setSource("Bad Income");
        inputDTO.setDate(LocalDate.of(2025,4,1));
        inputDTO.setAmount(null);

        mockMvc.perform(post("/api/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());

    }
    @Test
    void getAllIncomeByUser_shouldReturnIncomeList() throws Exception{
        Long userId = 1L;

        IncomeDTO income1 = new IncomeDTO();
        income1.setId(1L);
        income1.setUserId(userId);
        income1.setSource("Salary");
        income1.setDescription("Monthly pay");
        income1.setAmount(59000.00);
        income1.setDate(LocalDate.of(2025,1,1));

        IncomeDTO income2 = new IncomeDTO();
        income2.setId(2L);
        income2.setUserId(userId);
        income2.setSource("Salary");
        income2.setDescription("Monthly pay");
        income2.setAmount(59000.00);
        income2.setDate(LocalDate.of(2025,2,1));

        List<IncomeDTO> incomes = Arrays.asList(income1,income2);

        when(incomeService.getAllIncomeByUser(userId)).thenReturn(incomes);

        mockMvc.perform(get("/api/income/user/{userId}",userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

    }
    @Test
    void getAllIncomeByUser_shouldReturnUserNotFound_whenUserDoesNotExist()throws Exception{
        Long userId = 999L;

        when(incomeService.getAllIncomeByUser(userId)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/api/income/user/{userId}",userId))
                .andExpect(status().isNotFound());

    }
    @Test
    void getAllIncome_shouldReturnListOfIncomeDTOs() throws Exception{
        IncomeDTO income1 = mock(IncomeDTO.class);
        IncomeDTO income2 = mock(IncomeDTO.class);

        List<IncomeDTO> incomeList = List.of(income1,income2);

        when(incomeService.getAllIncome()).thenReturn(incomeList);

        mockMvc.perform(get("/api/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

    }
    @Test
    void getAllIncome_shouldReturnEmptyListWhenNoIncomeFound() throws Exception{
        when(incomeService.getAllIncome()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
    @Test
    void getIncomeById_shouldReturnIncomeDTO() throws Exception{
        Long incomeId = 1L;
        IncomeDTO dto = new IncomeDTO();

        dto.setId(incomeId);
        dto.setUserId(1L);
        dto.setSource("testIncome");
        dto.setDescription("income Description");
        dto.setAmount(5000.00);
        dto.setDate(LocalDate.of(2025,1,1));

        when(incomeService.getIncomeById(incomeId)).thenReturn(dto);

        mockMvc.perform(get("/api/income/{id}",incomeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(incomeId));

    }
    @Test
    void getIncomeById_throwsResourceNotFound_whenIncomeNotFound() throws Exception{
        Long incomeId = 999L;

        when(incomeService.getIncomeById(incomeId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/income/{id}",incomeId))
                .andExpect(status().isNotFound());
    }
    @Test
    void getTotalIncomeByUser_shouldReturnTotalIncome() throws Exception{
        Double incomeTotal = 50000.00;
        Long userId = 1L;

        when(incomeService.getTotalIncomeByUser(userId)).thenReturn(incomeTotal);

        mockMvc.perform(get("/api/income/user/{userId}/total",userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(incomeTotal));
    }
    @Test
    void getTotalIncomeByUser_shouldReturnZero_whenNoIncomeIsFound() throws Exception{
        Long userId = 1L;

        when(incomeService.getTotalIncomeByUser(userId)).thenReturn(0.0);

        mockMvc.perform(get("/api/income/user/{userId}/total",userId))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
        verify(incomeService).getTotalIncomeByUser(userId);
    }
    @Test
    void getTotalIncomeByUser_shouldReturnUserNotFound_whenUserNotFound() throws Exception{
        Long userId = 999L;

        when(incomeService.getTotalIncomeByUser(userId)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/api/income/user/{userId}/total",userId))
                .andExpect(status().isNotFound());
    }
    @Test
    void updateIncome_shouldReturnSavedDTO() throws Exception{
        Long incomeID = 1L;
        Long userId = 10L;

        IncomeDTO dto = new IncomeDTO();
        dto.setId(incomeID);
        dto.setUserId(userId);
        dto.setSource("Salary");
        dto.setDescription("Monthly pay");
        dto.setAmount(59000.00);
        dto.setDate(LocalDate.of(2025,1,1));

        IncomeDTO returnedDto = new IncomeDTO();
        returnedDto.setId(2L);
        returnedDto.setUserId(userId);
        returnedDto.setSource("Salary");
        returnedDto.setDescription("Monthly pay");
        returnedDto.setAmount(59000.00);
        returnedDto.setDate(LocalDate.of(2025,1,1));

        when(incomeService.updateIncome(eq(incomeID), any(IncomeDTO.class))).thenReturn(returnedDto);

        mockMvc.perform(put("/api/income/{id}",incomeID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));


    }
    @Test
    void updateIncome_shouldReturnNotFound_whenIncomeNotFound() throws Exception{
        Long incomeId = 99L;

        IncomeDTO dto = new IncomeDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setSource("Salary");
        dto.setDescription("Monthly pay");
        dto.setAmount(59000.00);
        dto.setDate(LocalDate.of(2025,1,1));

        when(incomeService.updateIncome(eq(incomeId),any(IncomeDTO.class))).thenThrow(new ResourceNotFoundException(incomeId));

        mockMvc.perform(put("/api/income/{id}",incomeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
        verify(incomeService).updateIncome(eq(incomeId),any(IncomeDTO.class));
    }
    @Test
    void deleteIncome_shouldReturnVoid_whenIncomeExists() throws Exception{
        Long incomeId = 1L;

        doNothing().when(incomeService).deleteIncome(incomeId);

        mockMvc.perform(delete("/api/income/{id}",incomeId))
                .andExpect(status().isNoContent());
    }
}
