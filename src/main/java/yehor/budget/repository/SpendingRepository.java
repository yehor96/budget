package yehor.budget.repository;

import org.springframework.stereotype.Repository;
import yehor.budget.util.model.Interval;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static yehor.budget.util.Constants.START_DATE;

@Repository
public class SpendingRepository {

    private static final Map<LocalDate, Integer> DUMMY_VALUES = Map.of(
            START_DATE, 10,
            LocalDate.of(2022, 3, 24), 100,
            LocalDate.of(2022, 3, 25), 5,
            LocalDate.of(2022, 3, 26), 1,
            LocalDate.of(2022, 3, 27), 14,
            LocalDate.now(), 25
    );

    public int findValueByDate(LocalDate date) {
        return Optional.ofNullable(DUMMY_VALUES.get(date))
                .orElse(0);
    }

    public int findSumInInterval(LocalDate dateFrom, LocalDate dateTo) {
        Interval interval = Interval.of(dateFrom, dateTo);
        return DUMMY_VALUES.entrySet().stream()
                .filter(entry -> interval.isWithin(entry.getKey()))
                .map(Map.Entry::getValue)
                .reduce(0, Integer::sum);
    }

}
