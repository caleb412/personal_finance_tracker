package com.example.finance.tracker.controller;

import com.example.finance.tracker.dto.StatsDTO;
import com.example.finance.tracker.service.stats.StatsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Stats Controller")
public class StatsController {
    private final StatsServiceImpl statsService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get summary of user income and expenses")
    public ResponseEntity<StatsDTO> getUserStats(@PathVariable Long userId){
        StatsDTO stats = statsService.getUserFinanceStats(userId);
        return ResponseEntity.ok(stats);
    }

}
