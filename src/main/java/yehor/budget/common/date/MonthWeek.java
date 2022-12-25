package yehor.budget.common.date;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public enum MonthWeek {

    DAYS_1_TO_7(List.of(1, 2, 3, 4, 5, 6, 7)),
    DAYS_8_TO_14(List.of(8, 9, 10, 11, 12, 13, 14)),
    DAYS_15_TO_21(List.of(15, 16, 17, 18, 19, 20, 21)),
    DAYS_22_TO_31(List.of(22, 23, 24, 25, 26, 27, 28, 29, 30, 31));

    @Getter
    private final List<Integer> range;

    MonthWeek(List<Integer> range) {
        this.range = range;
    }

    public static MonthWeek of(LocalDate date) {
        Integer day = date.getDayOfMonth();
        if (DAYS_1_TO_7.getRange().contains(day)) {
            return DAYS_1_TO_7;
        } else if (DAYS_8_TO_14.getRange().contains(day)) {
            return DAYS_8_TO_14;
        } else if (DAYS_15_TO_21.getRange().contains(day)) {
            return DAYS_15_TO_21;
        } else if (DAYS_22_TO_31.getRange().contains(day)) {
            return DAYS_22_TO_31;
        } else {
            throw new IllegalArgumentException("Illegal value provided " + date);
        }
    }

    public List<MonthWeek> getMonthWeeksAfter() {
        return Arrays.stream(values())
                .filter(m -> m.isAfter(this))
                .toList();
    }

    public boolean isAfter(MonthWeek that) {
        return this.ordinal() > that.ordinal();
    }
}
