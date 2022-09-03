package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.common.helper.CalculatorHelper;
import yehor.budget.repository.RowRegularExpectedExpenseRepository;
import yehor.budget.web.converter.RegularExpectedExpenseConverter;
import yehor.budget.web.dto.full.RegularExpectedExpenseFullDto;
import yehor.budget.web.dto.full.RowRegularExpectedExpenseFullDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RegularExpectedExpenseService {

    private final RowRegularExpectedExpenseRepository rowRegularExpectedExpenseRepository;
    private final RegularExpectedExpenseConverter regularExpectedExpenseConverter;
    private final CalculatorHelper calculatorHelper;

    public RegularExpectedExpenseFullDto getOne() {
        RegularExpectedExpenseFullDto fullDto = new RegularExpectedExpenseFullDto();

        List<RowRegularExpectedExpenseFullDto> rows = rowRegularExpectedExpenseRepository.findAll().stream()
                .map(regularExpectedExpenseConverter::convert)
                .toList();
        fullDto.setRows(rows);

        fullDto.setTotal1to7(sumInColumn(rows, RowRegularExpectedExpenseFullDto::getDays1to7));
        fullDto.setTotal8to14(sumInColumn(rows, RowRegularExpectedExpenseFullDto::getDays8to14));
        fullDto.setTotal15to21(sumInColumn(rows, RowRegularExpectedExpenseFullDto::getDays15to21));
        fullDto.setTotal22to31(sumInColumn(rows, RowRegularExpectedExpenseFullDto::getDays22to31));

        BigDecimal total = calculatorHelper.sum(
                fullDto.getTotal1to7(),
                fullDto.getTotal8to14(),
                fullDto.getTotal15to21(),
                fullDto.getTotal22to31());
        fullDto.setTotal(total);

        return fullDto;
    }

    private BigDecimal sumInColumn(List<RowRegularExpectedExpenseFullDto> rows,
                                   Function<RowRegularExpectedExpenseFullDto, BigDecimal> function) {
        return calculatorHelper.sum(rows.stream().map(function).toList());
    }
}
