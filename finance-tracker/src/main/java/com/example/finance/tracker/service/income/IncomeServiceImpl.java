package com.example.finance.tracker.service.income;


import com.example.finance.tracker.dto.IncomeDTO;
import com.example.finance.tracker.entity.Income;
import com.example.finance.tracker.entity.User;
import com.example.finance.tracker.exception.ResourceNotFoundException;
import com.example.finance.tracker.exception.UserNotFoundException;
import com.example.finance.tracker.repository.IncomeRepository;
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
public class IncomeServiceImpl implements IncomeService{
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(IncomeServiceImpl.class);
    @Override
    public IncomeDTO postIncome(IncomeDTO incomeDTO){
        logger.info("Posting new income for user ID:{}",incomeDTO.getUserId());
        Income income = saveOrUpdateIncome(new Income(),incomeDTO);
        logger.info("New income posted with ID:{}",income.getId());
        return income.getIncomeDto();
    }

    private Income saveOrUpdateIncome(Income income, IncomeDTO incomeDTO){
        logger.debug("Filling new DTO fields for IncomeDTO: {}",incomeDTO.getId());
        if (incomeDTO.getSource() == null || incomeDTO.getAmount() < 0) {
            throw new IllegalArgumentException("Invalid input: source must not be null and amount must be non-negative.");
        }
        income.setSource(incomeDTO.getSource());
        income.setDate(incomeDTO.getDate());
        income.setAmount(incomeDTO.getAmount());
        income.setDescription(incomeDTO.getDescription());
        User user = userRepository.findById(incomeDTO.getUserId())
                .orElseThrow(() -> {
                    logger.error("User with ID {}  not found",incomeDTO.getUserId());
                    return new UserNotFoundException(incomeDTO.getUserId());
                });
        income.setUser(user);
        logger.debug("Saving income to repository...");
        return incomeRepository.save(income);
    }
    @Override
    public IncomeDTO updateIncome(Long id, IncomeDTO incomeDTO){
        Optional<Income> optionalIncome = incomeRepository.findById(id);
        logger.info("Updating expense with ID {}...",incomeDTO.getId());
        if(optionalIncome.isPresent()){
            Income updated = saveOrUpdateIncome(optionalIncome.get(),incomeDTO);
            return updated.getIncomeDto();
        }else{
            throw new ResourceNotFoundException(id);
        }
    }
    @Override
    public List<IncomeDTO> getAllIncome(){
        logger.info("Getting all income...");
        return incomeRepository.findAll().stream()
                .sorted(Comparator.comparing(Income::getDate).reversed())
                .map(Income::getIncomeDto)
                .collect(Collectors.toList());
    }
    @Override
    public IncomeDTO getIncomeById(Long id){
        Optional<Income> optionalIncome = incomeRepository.findById(id);
        if(optionalIncome.isPresent()){
            logger.info("Getting income with ID {}...", id);
            return optionalIncome.get().getIncomeDto();
        } else{
            throw new ResourceNotFoundException(id);
        }
    }
    @Override
    public void deleteIncome(Long id){
        Optional<Income> optionalIncome = incomeRepository.findById(id);
        if(optionalIncome.isPresent()){
            logger.info("Deleting expense with ID {}...",id);
            incomeRepository.deleteById(id);
        } else{
            logger.error("Expense with ID {} not found",id);
            throw new ResourceNotFoundException(id);
        }
    }
    @Override
    public List<IncomeDTO> getAllIncomeByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            logger.error("Cannot get income for User with ID {}", userId);
            throw new ResourceNotFoundException(userId);
        }
        logger.info("Getting income for user with ID {}...",userId);
        return incomeRepository.findAllByUserId(userId).stream()
                .sorted(Comparator.comparing(Income::getDate).reversed())
                .map(Income::getIncomeDto)
                .collect(Collectors.toList());
    }
    @Override
    public Double getTotalIncomeByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            logger.error("Cannot get total income for User with ID {}", userId);
            throw new UserNotFoundException(userId);
        }
        logger.info("Getting total income for user with ID {}",userId);
        return Optional.ofNullable(incomeRepository.getTotalIncomeByUserId(userId)).orElse(0.0);
    }
}
