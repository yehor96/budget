package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.IncomeSource;
import yehor.budget.repository.IncomeSourceRepository;
import yehor.budget.web.converter.IncomeSourceConverter;
import yehor.budget.web.dto.TotalIncomeDto;
import yehor.budget.web.dto.full.IncomeSourceFullDto;
import yehor.budget.web.dto.limited.IncomeSourceLimitedDto;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeSourceService {

    private final IncomeSourceRepository incomeSourceRepository;
    private final IncomeSourceConverter incomeSourceConverter;

    public TotalIncomeDto getTotalIncome() {
        List<IncomeSourceFullDto> incomeSources = incomeSourceRepository.findAll()
                .stream()
                .map(incomeSourceConverter::convert)
                .toList();
        BigDecimal total = incomeSources.stream()
                .map(IncomeSourceFullDto::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TotalIncomeDto.builder()
                .incomeSources(incomeSources)
                .totalIncome(total)
                .build();
    }

    public void save(IncomeSourceLimitedDto incomeSourceDto) {
        IncomeSource incomeSource = incomeSourceConverter.convert(incomeSourceDto);
        validateNotExists(incomeSource);
        incomeSourceRepository.save(incomeSource);
        log.info("{} is saved", incomeSource);
    }

    public void delete(Long id) {
        try {
            incomeSourceRepository.deleteById(id);
            log.info("Income source with id {} is deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Income source with id " + id + " not found");
        }
    }

    @Transactional
    public void update(IncomeSourceFullDto incomeSourceDto) {
        validateExists(incomeSourceDto.getId());
        IncomeSource incomeSource = incomeSourceConverter.convert(incomeSourceDto);
        incomeSourceRepository.save(incomeSource);
        log.info("{} is updated", incomeSource);
    }

    private void validateNotExists(IncomeSource incomeSource) {
        if (incomeSourceRepository.existsByName(incomeSource.getName())) {
            throw new ObjectAlreadyExistsException("Income source " + incomeSource.getName() + " already exists");
        }
    }

    private void validateExists(Long id) {
        if (!incomeSourceRepository.existsById(id)) {
            throw new ObjectNotFoundException("Income source with id " + id + " does not exist");
        }
    }
}
