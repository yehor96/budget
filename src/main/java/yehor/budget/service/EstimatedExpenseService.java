package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.common.helper.CalculatorHelper;
import yehor.budget.repository.RowEstimatedExpenseRepository;
import yehor.budget.web.converter.EstimatedExpenseConverter;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;
import yehor.budget.web.dto.full.RowEstimatedExpenseFullDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static yehor.budget.common.Currency.UAH;
import static yehor.budget.common.Currency.USD;

@Service
@RequiredArgsConstructor
public class EstimatedExpenseService {

    private final RowEstimatedExpenseRepository rowEstimatedExpenseRepository;
    private final EstimatedExpenseConverter estimatedExpenseConverter;
    private final CalculatorHelper calculatorHelper;
    private final CurrencyRateService currencyRateService;

    public EstimatedExpenseFullDto getOne() {
        EstimatedExpenseFullDto fullDto = new EstimatedExpenseFullDto();

        List<RowEstimatedExpenseFullDto> rows = rowEstimatedExpenseRepository.findAll().stream()
                .map(estimatedExpenseConverter::convert)
                .toList();
        fullDto.setRows(rows);

        fullDto.setTotal1to7(sumInColumn(rows, RowEstimatedExpenseFullDto::getDays1to7));
        fullDto.setTotal8to14(sumInColumn(rows, RowEstimatedExpenseFullDto::getDays8to14));
        fullDto.setTotal15to21(sumInColumn(rows, RowEstimatedExpenseFullDto::getDays15to21));
        fullDto.setTotal22to31(sumInColumn(rows, RowEstimatedExpenseFullDto::getDays22to31));

        BigDecimal total = calculatorHelper.sum(
                fullDto.getTotal1to7(),
                fullDto.getTotal8to14(),
                fullDto.getTotal15to21(),
                fullDto.getTotal22to31());
        fullDto.setTotal(total);
        fullDto.setTotalUsd(currencyRateService.convert(UAH, USD, total));

        return fullDto;
    }

    private BigDecimal sumInColumn(List<RowEstimatedExpenseFullDto> rows,
                                   Function<RowEstimatedExpenseFullDto, BigDecimal> function) {
        return calculatorHelper.sum(rows.stream().map(function).toList());
    }
}
