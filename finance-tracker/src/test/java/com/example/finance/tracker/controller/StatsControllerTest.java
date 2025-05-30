package com.example.finance.tracker.controller;

import com.example.finance.tracker.dto.StatsDTO;
import com.example.finance.tracker.service.stats.StatsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
public class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsServiceImpl statsService;

    @Test
    void getUserStats_shouldReturnDTO() throws Exception{
        Long userId = 1L;

        StatsDTO mockStats = StatsDTO.builder()
                .userId(userId)
                .totalIncome(5000.0)
                .totalExpenses(2000.0)
                .netBalance(3000.0)
                .build();

        when(statsService.getUserFinanceStats(userId)).thenReturn(mockStats);

        mockMvc.perform(get("/api/stats/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.totalIncome").value(5000.0))
                .andExpect(jsonPath("$.totalExpenses").value(2000.0))
                .andExpect(jsonPath("$.netBalance").value(3000.0));
    }
}

