package yehor.budget.common.date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.entity.Settings;
import yehor.budget.service.SettingsService;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class DateManagerTest {

    private final SettingsService settingsServiceMock = mock(SettingsService.class);
    private DateManager dateManager;

    private static final LocalDate startDate = LocalDate.now().minusDays(30);
    private static final LocalDate endDate = LocalDate.now();
    private final static Settings settings = Settings.builder()
            .budgetStartDate(startDate)
            .budgetEndDate(endDate)
            .isBudgetDateValidation(true)
            .build();

    @BeforeEach
    void setup() {
        dateManager = new DateManager(settings);
        SettingsNotificationManager settingsNotificationManager = new SettingsNotificationManager();
        settingsNotificationManager.addListener(dateManager.getClass(), settingsServiceMock);
    }

    @Test
    void testGetMonthsListIn() {
        FullMonth startMonth = FullMonth.of(Month.JANUARY, 2022);
        FullMonth month1 = FullMonth.of(Month.FEBRUARY, 2022);
        FullMonth month2 = FullMonth.of(Month.MARCH, 2022);
        FullMonth month3 = FullMonth.of(Month.APRIL, 2022);
        FullMonth endMonth = FullMonth.of(Month.MAY, 2022);

        List<FullMonth> expectedMonths = List.of(startMonth, month1, month2, month3, endMonth);

        List<FullMonth> actualMonths = dateManager.getMonthsListIn(startMonth, endMonth);

        assertEquals(expectedMonths, actualMonths);
    }

    @Test
    void testGetMonthsListInTheSameMonth() {
        FullMonth month = FullMonth.of(Month.JANUARY, 2022);

        List<FullMonth> expectedMonths = List.of(month);

        List<FullMonth> actualMonths = dateManager.getMonthsListIn(month, month);

        assertEquals(expectedMonths, actualMonths);
    }

    @Test
    void testValidateMonthWithinBudgetSuccess() {
        LocalDate now = LocalDate.now();
        FullMonth month = FullMonth.of(now.getMonth(), now.getYear());

        dateManager.validateMonthWithinBudget(month);
    }

    @Test
    void testValidateMonthWithinBudgetFailure() {
        LocalDate date = dateManager.getStartDate().minusMonths(1);
        FullMonth month = FullMonth.of(date.getMonth(), date.getYear());

        try {
            dateManager.validateMonthWithinBudget(month);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertEquals("Provided value is invalid " + month, exception.getMessage());
        }
    }

    @Test
    void testValidateMonthsInSequentialOrderSuccess() {
        FullMonth startMonth = FullMonth.of(Month.JANUARY, 2022);
        FullMonth endMonth = FullMonth.of(Month.MAY, 2022);

        dateManager.validateMonthsInSequentialOrder(startMonth, endMonth);
    }

    @Test
    void testValidateMonthsInSequentialOrderFailure() {
        FullMonth startMonth = FullMonth.of(Month.MAY, 2022);
        FullMonth endMonth = FullMonth.of(Month.JANUARY, 2022);

        try {
            dateManager.validateMonthsInSequentialOrder(startMonth, endMonth);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertEquals("Reversed order of provided months: " + startMonth + " and " + endMonth, exception.getMessage());
        }
    }

    @Test
    void testUpdateEndDateIfNecessaryNotCalledWhenDateIsWithinExistingBudgetPeriod() {
        try (var mock = mockStatic(SettingsNotificationManager.class)) {

            dateManager.updateBudgetDatesIfNecessary(LocalDate.now().minusDays(5));

            mock.verifyNoInteractions();
        }
    }

    @Test
    void testUpdateEndDateIfNecessaryCalledWhenDateIsOutsideOfExistingBudgetPeriod() {
        try (var mock = mockStatic(SettingsNotificationManager.class)) {

            LocalDate newEndDate = LocalDate.now().plusDays(5);
            Settings expectedSettings = Settings.builder()
                    .budgetStartDate(settings.getBudgetStartDate())
                    .budgetEndDate(newEndDate)
                    .build();

            dateManager.updateBudgetDatesIfNecessary(newEndDate);

            mock.verify(() -> SettingsNotificationManager.updateListeners(eq(DateManager.class), eq(expectedSettings)));
        }
    }
}