package yehor.budget.web.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yehor.budget.common.helper.CalculatorHelper;
import yehor.budget.entity.RowRegularExpectedExpense;
import yehor.budget.web.dto.full.RowRegularExpectedExpenseFullDto;

@Component
@RequiredArgsConstructor
public class RegularExpectedExpenseConverter {

    private final CalculatorHelper calculatorHelper;

    public RowRegularExpectedExpenseFullDto convert(RowRegularExpectedExpense row) {
        return RowRegularExpectedExpenseFullDto.builder()
                .categoryId(row.getCategory().getId())
                .days1to7(row.getDays1to7())
                .days8to14(row.getDays8to14())
                .days15to21(row.getDays15to21())
                .days22to31(row.getDays22to31())
                .totalPerRow(calculatorHelper.sum(
                        row.getDays1to7(),
                        row.getDays8to14(),
                        row.getDays15to21(),
                        row.getDays22to31()))
                .build();
    }
}
