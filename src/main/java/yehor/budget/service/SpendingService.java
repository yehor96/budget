package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.repository.SpendingRepository;
import yehor.budget.web.converter.SpendingConverter;
import yehor.budget.web.dto.SpendingValueDto;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SpendingService {

    private static final SpendingConverter SPENDING_CONVERTER = new SpendingConverter();

    private final SpendingRepository spendingRepository;

    public SpendingValueDto findByDate(LocalDate date) {
        int value = spendingRepository.findValueByDate(date);
        return SPENDING_CONVERTER.convertToDto(value);
    }

    public SpendingValueDto findSumInInterval(LocalDate dateFrom, LocalDate dateTo) {
        int value = spendingRepository.findSumInInterval(dateFrom, dateTo);
        return SPENDING_CONVERTER.convertToDto(value);
    }
}
