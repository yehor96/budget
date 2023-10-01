package yehor.budget.web.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yehor.budget.common.util.CalculatorHelper;
import yehor.budget.entity.RowEstimatedExpense;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.entity.recording.ExpectedExpenseRecord;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;
import yehor.budget.web.dto.full.RowEstimatedExpenseFullDto;

@Component
@RequiredArgsConstructor
public class EstimatedExpenseConverter {

    private final CalculatorHelper calculatorHelper;
    private final CategoryConverter categoryConverter;

    public RowEstimatedExpenseFullDto convert(RowEstimatedExpense row) {
        return RowEstimatedExpenseFullDto.builder()
                .category(categoryConverter.convert(row.getCategory()))
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

    public ExpectedExpenseRecord convert(EstimatedExpenseFullDto estimatedExpenseFullDto, BalanceRecord balanceRecord) {
        return ExpectedExpenseRecord.builder()
                .total1to7(estimatedExpenseFullDto.getTotal1to7())
                .total8to14(estimatedExpenseFullDto.getTotal8to14())
                .total15to21(estimatedExpenseFullDto.getTotal15to21())
                .total22to31(estimatedExpenseFullDto.getTotal22to31())
                .balanceRecord(balanceRecord)
                .build();
    }
}
