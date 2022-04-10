package yehor.budget.web.converter;

import yehor.budget.web.dto.SpendingValueDto;

public class SpendingConverter {
    public SpendingValueDto convertToDto(int value) {
        return SpendingValueDto.builder()
                .value(value)
                .build();
    }
}
