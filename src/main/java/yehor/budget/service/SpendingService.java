package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.repository.SpendingRepository;

import java.time.LocalDate;

import static yehor.budget.util.Constants.START_DATE;

@Service
@RequiredArgsConstructor
public class SpendingService {

    private final SpendingRepository spendingRepository;

    public int findByDate(LocalDate date) {
        if (!isValid(date)) {
            throw new IllegalArgumentException(
                    "Date is out of budget. Start date is " + START_DATE +
                            ", and current date is " + LocalDate.now());
        }
        return spendingRepository.findValueByDate(date);
    }

    private boolean isValid(LocalDate date) {
        return START_DATE.isBefore(date) && LocalDate.now().isAfter(date);
    }
}
