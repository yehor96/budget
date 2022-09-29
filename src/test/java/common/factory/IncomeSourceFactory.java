package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.IncomeSource;
import yehor.budget.web.dto.TotalIncomeDto;
import yehor.budget.web.dto.full.IncomeSourceFullDto;
import yehor.budget.web.dto.limited.IncomeSourceLimitedDto;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class IncomeSourceFactory {

    public static final long DEFAULT_INCOME_SOURCE_ID = 1L;

    public static IncomeSource defaultIncomeSource() {
        return IncomeSource.builder()
                .id(DEFAULT_INCOME_SOURCE_ID)
                .name("My salary")
                .value(new BigDecimal("100.00"))
                .build();
    }

    public static IncomeSourceFullDto defaultIncomeSourceFullDto() {
        return IncomeSourceFullDto.builder()
                .id(DEFAULT_INCOME_SOURCE_ID)
                .name("My salary")
                .value(new BigDecimal("100.00"))
                .build();
    }

    public static IncomeSourceLimitedDto defaultIncomeSourceLimitedDto() {
        return IncomeSourceLimitedDto.builder()
                .name("My salary")
                .value(new BigDecimal("100.00"))
                .build();
    }

    public static IncomeSource secondIncomeSource() {
        return IncomeSource.builder()
                .id(2L)
                .name("Dividends")
                .value(new BigDecimal("10.00"))
                .build();
    }

    public static IncomeSourceFullDto secondIncomeSourceFullDto() {
        return IncomeSourceFullDto.builder()
                .id(2L)
                .name("Dividends")
                .value(new BigDecimal("10.00"))
                .build();
    }

    public static IncomeSourceLimitedDto secondIncomeSourceLimitedDto() {
        return IncomeSourceLimitedDto.builder()
                .name("Dividends")
                .value(new BigDecimal("10.00"))
                .build();
    }

    public static TotalIncomeDto defaultTotalIncomeDto() {
        return TotalIncomeDto.builder()
                .incomeSources(List.of(defaultIncomeSourceFullDto(), secondIncomeSourceFullDto()))
                .totalIncome(new BigDecimal("110.00"))
                .build();
    }
}
