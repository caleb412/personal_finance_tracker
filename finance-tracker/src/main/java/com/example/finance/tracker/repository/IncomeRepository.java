package com.example.finance.tracker.repository;

import com.example.finance.tracker.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findAllByUserId(Long userId);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId")
    Double getTotalIncomeByUserId(@Param("userId") Long userId);
}
