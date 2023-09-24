package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.FutureExpense;
import yehor.budget.repository.FutureExpenseRepository;
import yehor.budget.web.converter.FutureExpenseConverter;
import yehor.budget.web.dto.full.FutureExpenseFullDto;
import yehor.budget.web.dto.limited.FutureExpenseLimitedDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
/*
    Class not covered with unit tests
 */
public class FutureExpenseService {

    private final FutureExpenseRepository futureExpenseRepository;
    private final FutureExpenseConverter futureExpenseConverter;

    public List<FutureExpenseFullDto> getAll() {
        List<FutureExpense> futureExpenses = futureExpenseRepository.findAll();
        return futureExpenses.stream()
                .map(futureExpenseConverter::convert)
                .toList();
    }

    public FutureExpenseFullDto save(FutureExpenseLimitedDto futureExpenseDto) {
        FutureExpense saved = futureExpenseRepository.save(futureExpenseConverter.convert(futureExpenseDto));
        log.info("Saved: {}", saved);
        return futureExpenseConverter.convert(saved);
    }

    public void delete(Long id) {
        try {
            futureExpenseRepository.deleteById(id);
            log.info("Future expense with id {} is deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Future expense with id " + id + " not found");
        }
    }
}
