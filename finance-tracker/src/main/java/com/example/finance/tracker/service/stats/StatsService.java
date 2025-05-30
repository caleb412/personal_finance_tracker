package com.example.finance.tracker.service.stats;

import com.example.finance.tracker.dto.StatsDTO;

public interface StatsService {
    StatsDTO getUserFinanceStats (Long userId);
}
