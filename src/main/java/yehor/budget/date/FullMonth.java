package yehor.budget.date;

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
}
