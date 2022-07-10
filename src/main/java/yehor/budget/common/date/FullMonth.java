package yehor.budget.common.date;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Month;

@Data
@AllArgsConstructor
public class FullMonth {
    private Month month;
    private Integer year;

    public static FullMonth of(java.time.Month month, Integer year) {
        return new FullMonth(month, year);
    }

    @Override
    public String toString() {
        return "[" + month.name() + ", " + year + "]";
    }

    public FullMonth next() {
        if (month.getValue() == 12) {
            return new FullMonth(Month.JANUARY, year + 1);
        } else {
            return new FullMonth(month.plus(1), year);
        }
    }
}
