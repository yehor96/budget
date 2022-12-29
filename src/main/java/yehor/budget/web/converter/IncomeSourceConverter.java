package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.IncomeSource;
import yehor.budget.web.dto.full.IncomeSourceFullDto;
import yehor.budget.web.dto.limited.IncomeSourceLimitedDto;

@Component
public class IncomeSourceConverter {
    public IncomeSourceFullDto convert(IncomeSource incomeSource) {
        return IncomeSourceFullDto.builder()
                .id(incomeSource.getId())
                .name(incomeSource.getName())
                .value(incomeSource.getValue())
                .currency(incomeSource.getCurrency())
                .accrualDayOfMonth(incomeSource.getAccrualDay())
                .build();
    }

    public IncomeSource convert(IncomeSourceLimitedDto incomeSourceDto) {
        return IncomeSource.builder()
                .name(incomeSourceDto.getName())
                .value(incomeSourceDto.getValue())
                .currency(incomeSourceDto.getCurrency())
                .accrualDay(incomeSourceDto.getAccrualDayOfMonth())
                .build();
    }

    public IncomeSource convert(IncomeSourceFullDto incomeSourceDto) {
        return IncomeSource.builder()
                .id(incomeSourceDto.getId())
                .name(incomeSourceDto.getName())
                .value(incomeSourceDto.getValue())
                .currency(incomeSourceDto.getCurrency())
                .accrualDay(incomeSourceDto.getAccrualDayOfMonth())
                .build();
    }
}
