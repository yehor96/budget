package yehor.budget.util.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Interval {

    private LocalDate startDate;
    private LocalDate endDate;

    public static Interval of(LocalDate startDate, LocalDate endDate) {
        return new Interval(startDate, endDate);
    }

    public boolean isWithin(LocalDate date) {
        return date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1));
    }
}
