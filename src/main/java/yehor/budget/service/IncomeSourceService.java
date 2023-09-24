package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.Currency;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.IncomeSource;
import yehor.budget.repository.IncomeSourceRepository;
import yehor.budget.service.client.currency.CurrencyRateService;
import yehor.budget.web.converter.IncomeSourceConverter;
import yehor.budget.web.dto.TotalIncomeDto;
import yehor.budget.web.dto.full.IncomeSourceFullDto;
import yehor.budget.web.dto.limited.IncomeSourceLimitedDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeSourceService {

    @Value("${income.sources.base.currency}")
    private Currency baseCurrency;

    private final IncomeSourceRepository incomeSourceRepository;
    private final IncomeSourceConverter incomeSourceConverter;
    private final CurrencyRateService currencyRateService;

    public TotalIncomeDto getTotalIncome() {
        List<IncomeSourceFullDto> incomeSources = incomeSourceRepository.findAll()
                .stream()
                .map(incomeSourceConverter::convert)
                .toList();

        BigDecimal totalIncomeInBaseCurrency = getTotalInBaseCurrency(incomeSources);

        return TotalIncomeDto.builder()
                .incomeSources(incomeSources)
                .total(totalIncomeInBaseCurrency)
                .totalCurrency(baseCurrency)
                .build();
    }

    public IncomeSourceFullDto save(IncomeSourceLimitedDto incomeSourceDto) {
        IncomeSource incomeSource = incomeSourceConverter.convert(incomeSourceDto);
        validateNotExists(incomeSource);
        IncomeSourceFullDto saved = incomeSourceConverter.convert(incomeSourceRepository.save(incomeSource));
        log.info("Saved: {}", saved);
        return saved;
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
    public IncomeSourceFullDto update(IncomeSourceFullDto incomeSourceDto) {
        validateExists(incomeSourceDto.getId());
        IncomeSource incomeSource = incomeSourceConverter.convert(incomeSourceDto);
        IncomeSourceFullDto updated = incomeSourceConverter.convert(incomeSourceRepository.save(incomeSource));
        log.info("Updated: {}", updated);
        return updated;
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

    private BigDecimal getTotalInBaseCurrency(List<IncomeSourceFullDto> incomeSources) {
        Map<Currency, BigDecimal> currencyToTotalMap = incomeSources.stream()
                .collect(groupingBy(
                        IncomeSourceFullDto::getCurrency,
                        reducing(BigDecimal.ZERO, IncomeSourceFullDto::getValue, BigDecimal::add)
                ));

        BigDecimal allTotal = BigDecimal.ZERO;
        for (var entry : currencyToTotalMap.entrySet()) {
            Currency currency = entry.getKey();
            if (currency.equals(baseCurrency)) {
                allTotal = allTotal.add(entry.getValue());
            } else {
                BigDecimal convertedValue = currencyRateService.convert(currency, baseCurrency, entry.getValue());
                allTotal = allTotal.add(convertedValue);
            }
        }

        return allTotal;
    }
}
