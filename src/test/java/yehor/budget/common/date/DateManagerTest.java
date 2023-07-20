package yehor.budget.common.date;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.entity.Settings;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static common.factory.SettingsFactory.defaultSettings;
import static common.factory.SettingsFactory.settingsWithBudgetDateValidationOff;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class DateManagerTest {

    private DateManager dateManager;
    private Settings settings;

    @Test
    void testUpdateBudgetDatesIfNecessaryNotUpdatesEndDateAndNotInformsListenersWhenDateIsBeforeEndDate() {
        setUp(defaultSettings());
        try (var mock = mockStatic(SettingsNotificationManager.class)) {

            dateManager.updateBudgetDatesIfNecessary(settings.getBudgetEndDate().minusDays(5));

            mock.verifyNoInteractions();
            assertEquals(settings.getBudgetEndDate(), dateManager.getEndDate());
        }
    }

    @Test
    void testUpdateBudgetDatesIfNecessaryUpdatesEndDateAndInformsListenersWhenDateIsAfterEndDate() {
        setUp(defaultSettings());
        try (var mock = mockStatic(SettingsNotificationManager.class)) {

            LocalDate newEndDate = settings.getBudgetEndDate().plusDays(5);
            Settings expectedSettings = Settings.builder()
                    .budgetStartDate(settings.getBudgetStartDate())
                    .budgetEndDate(newEndDate)
                    .build();

            dateManager.updateBudgetDatesIfNecessary(newEndDate);

            mock.verify(() -> SettingsNotificationManager.updateListeners(eq(DateManager.class), eq(expectedSettings)));
            assertEquals(newEndDate, dateManager.getEndDate());
            assertNotEquals(settings.getBudgetEndDate(), dateManager.getEndDate());
        }
    }

    @Test
    void testUpdateBudgetDatesIfNecessaryNotUpdatesStartDateAndNotInformsListenersWhenDateIsAfterStartDate() {
        setUp(defaultSettings());
        try (var mock = mockStatic(SettingsNotificationManager.class)) {

            dateManager.updateBudgetDatesIfNecessary(settings.getBudgetStartDate().plusDays(5));

            mock.verifyNoInteractions();
            assertEquals(settings.getBudgetStartDate(), dateManager.getStartDate());
        }
    }

    @Test
    void testUpdateBudgetDatesIfNecessaryUpdatesStartDateAndInformsListenersWhenDateIsBeforeStartDate() {
        setUp(defaultSettings());
        try (var mock = mockStatic(SettingsNotificationManager.class)) {

            LocalDate newStartDate = settings.getBudgetStartDate().minusDays(5);
            Settings expectedSettings = Settings.builder()
                    .budgetStartDate(newStartDate)
                    .budgetEndDate(settings.getBudgetEndDate())
                    .build();

            dateManager.updateBudgetDatesIfNecessary(newStartDate);

            mock.verify(() -> SettingsNotificationManager.updateListeners(eq(DateManager.class), eq(expectedSettings)));
            assertEquals(newStartDate, dateManager.getStartDate());
            assertNotEquals(settings.getBudgetStartDate(), dateManager.getStartDate());
        }
    }

    @Test
    void testIsWithinBudgetSuccess() {
        setUp(defaultSettings());
        LocalDate date = LocalDate.now().plusDays(5);
        assertFalse(dateManager.isWithinBudget(date));
    }

    @Test
    void testIsWithinBudgetFailed() {
        setUp(defaultSettings());
        LocalDate date = LocalDate.now().minusDays(5);
        assertTrue(dateManager.isWithinBudget(date));
    }

    @Test
    void testIsWithinBudgetReturnsTrueIfBudgetValidationIsOff() {
        setUp(settingsWithBudgetDateValidationOff());
        LocalDate date = LocalDate.now().plusDays(5);
        assertTrue(dateManager.isWithinBudget(date));
    }

    @Test
    void testAreWithinBudgetSuccess() {
        setUp(defaultSettings());
        LocalDate date1 = LocalDate.now().minusDays(5);
        LocalDate date2 = LocalDate.now().minusDays(10);
        assertTrue(dateManager.areWithinBudget(date1, date2));
    }

    @Test
    void testAreWithinBudgetFailed() {
        setUp(defaultSettings());
        LocalDate date1 = LocalDate.now().minusDays(5);
        LocalDate date2 = LocalDate.now().plusDays(5);
        assertFalse(dateManager.areWithinBudget(date1, date2));
    }

    @Test
    void testAreWithinBudgetReturnsTrueIfBudgetValidationIsOff() {
        setUp(settingsWithBudgetDateValidationOff());
        LocalDate date1 = LocalDate.now().minusDays(5);
        LocalDate date2 = LocalDate.now().minusDays(15);
        assertTrue(dateManager.areWithinBudget(date1, date2));
    }

    @Test
    void testGetMonthsListIn() {
        setUp(defaultSettings());
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
        setUp(defaultSettings());
        FullMonth month = FullMonth.of(Month.JANUARY, 2022);

        List<FullMonth> expectedMonths = List.of(month);

        List<FullMonth> actualMonths = dateManager.getMonthsListIn(month, month);

        assertEquals(expectedMonths, actualMonths);
    }

    @Test
    void testValidateDateAfterStartSuccess() {
        setUp(defaultSettings());
        LocalDate date = dateManager.getStartDate().plusDays(5);

        dateManager.validateDateAfterStart(date);
    }

    @Test
    void testValidateDateAfterStartFailure() {
        setUp(defaultSettings());
        LocalDate date = dateManager.getStartDate().minusDays(5);

        try {
            dateManager.validateDateAfterStart(date);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertTrue(exception.getMessage().contains("Date argument is out of budget"));
        }
    }

    @Test
    void testValidateDateAfterStartDoesNotThrowExceptionWhenBudgetValidationIsOff() {
        setUp(settingsWithBudgetDateValidationOff());
        LocalDate date = dateManager.getStartDate().minusDays(5);

        dateManager.validateDateAfterStart(date);
    }

    @Test
    void testValidateDatesWithinBudgetSuccess() {
        setUp(defaultSettings());
        LocalDate date1 = LocalDate.now().minusDays(5);
        LocalDate date2 = LocalDate.now().minusDays(10);

        dateManager.validateDatesWithinBudget(date1, date2);
    }

    @Test
    void testValidateDatesWithinBudgetFailed() {
        setUp(defaultSettings());
        LocalDate date1 = LocalDate.now().minusDays(5);
        LocalDate date2 = LocalDate.now().plusDays(5);

        try {
            dateManager.validateDatesWithinBudget(date1, date2);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertTrue(exception.getMessage().contains("Date argument is out of budget"));
        }
    }

    @Test
    void testValidateDatesWithinBudgetDoesNotThrowExceptionIfBudgetValidationIsOff() {
        setUp(settingsWithBudgetDateValidationOff());
        LocalDate date1 = LocalDate.now().plusDays(5);
        LocalDate date2 = LocalDate.now().plusDays(15);

        dateManager.validateDatesWithinBudget(date1, date2);
    }

    @Test
    void testValidateDateWithinBudgetSuccess() {
        setUp(defaultSettings());
        LocalDate date = LocalDate.now().minusDays(5);

        dateManager.validateDateWithinBudget(date);
    }

    @Test
    void testValidateDateWithinBudgetFailed() {
        setUp(defaultSettings());
        LocalDate date = LocalDate.now().plusDays(10);

        try {
            dateManager.validateDateWithinBudget(date);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertTrue(exception.getMessage().contains("Date argument is out of budget"));
        }
    }

    @Test
    void testValidateDateWithinBudgetDoesNotThrowExceptionIfBudgetValidationIsOff() {
        setUp(settingsWithBudgetDateValidationOff());
        LocalDate date = LocalDate.now().plusDays(5);

        dateManager.validateDateWithinBudget(date);
    }

    @Test
    void testValidateDatesInSequentialOrderSuccess() {
        setUp(defaultSettings());
        LocalDate date1 = dateManager.getStartDate().minusDays(5);
        LocalDate date2 = dateManager.getStartDate().plusDays(5);

        dateManager.validateDatesInSequentialOrder(date1, date2);
    }

    @Test
    void testValidateDatesInSequentialOrderFailure() {
        setUp(defaultSettings());
        LocalDate date1 = dateManager.getStartDate().plusDays(5);
        LocalDate date2 = dateManager.getStartDate().minusDays(5);

        try {
            dateManager.validateDatesInSequentialOrder(date1, date2);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertTrue(exception.getMessage().contains("Reversed order of dates"));
        }
    }

    @Test
    void testValidateDatesInSequentialOrderStillThrowsExceptionWhenBudgetValidationIsOff() {
        setUp(settingsWithBudgetDateValidationOff());
        LocalDate date1 = dateManager.getStartDate().plusDays(5);
        LocalDate date2 = dateManager.getStartDate().minusDays(5);

        try {
            dateManager.validateDatesInSequentialOrder(date1, date2);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertTrue(exception.getMessage().contains("Reversed order of dates"));
        }
    }

    @Test
    void testValidateMonthWithinBudgetSuccess() {
        setUp(defaultSettings());
        LocalDate now = LocalDate.now();
        FullMonth month = FullMonth.of(now.getMonth(), now.getYear());

        dateManager.validateMonthWithinBudget(month);
    }

    @Test
    void testValidateMonthWithinBudgetFailure() {
        setUp(defaultSettings());
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
    void testValidateMonthWithinBudgetDoesNotThrowExceptionWhenBudgetValidationIsOff() {
        setUp(settingsWithBudgetDateValidationOff());
        LocalDate date = dateManager.getStartDate().minusMonths(1);
        FullMonth month = FullMonth.of(date.getMonth(), date.getYear());

        dateManager.validateMonthWithinBudget(month);
    }

    @Test
    void testValidateMonthsInSequentialOrderSuccess() {
        setUp(defaultSettings());
        FullMonth startMonth = FullMonth.of(Month.JANUARY, 2022);
        FullMonth endMonth = FullMonth.of(Month.MAY, 2022);

        dateManager.validateMonthsInSequentialOrder(startMonth, endMonth);
    }

    @Test
    void testValidateMonthsInSequentialOrderFailure() {
        setUp(defaultSettings());
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
    void testIsValidLocalDatePatternReturnsTrueWhenPassedValueIsLocalDate() {
        setUp(defaultSettings());
        boolean result = dateManager.isValidLocalDatePattern("2020-10-10");
        assertTrue(result);
    }

    @Test
    void testIsValidLocalDatePatternReturnsFalseWhenPassedValueIsNotLocalDate() {
        setUp(defaultSettings());
        boolean result = dateManager.isValidLocalDatePattern("not-local-date");
        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("dayProvider")
    void testValidateDayOfMonth(int day) {
        setUp(defaultSettings());
        dateManager.validateDayOfMonth(day);
    }

    static Stream<Integer> dayProvider() {
        return Stream.of(1, 29, 31);
    }

    @ParameterizedTest
    @MethodSource("illegalDayProvider")
    void testValidateDayOfMonthIllegalValue(int day) {
        setUp(defaultSettings());
        try {
            dateManager.validateDayOfMonth(day);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertEquals("Provided value is not a day of month - " + day, exception.getMessage());
        }
    }

    static Stream<Integer> illegalDayProvider() {
        return Stream.of(-1, 0, 32);
    }

    @ParameterizedTest
    @MethodSource("dateToDayProvider")
    void testGetLastDayOfMonth(Pair<LocalDate, Integer> paramPair) {
        setUp(defaultSettings());
        LocalDate date = paramPair.getKey();
        Integer expectedResult = paramPair.getValue();
        int actualResult = dateManager.getLastDayOfMonth(date);
        assertEquals(expectedResult, actualResult);
    }

    static Stream<Pair<LocalDate, Integer>> dateToDayProvider() {
        return Stream.of(
                Pair.of(LocalDate.of(2020, 2, 1), 29),
                Pair.of(LocalDate.of(2021, 2, 1), 28),
                Pair.of(LocalDate.of(2022, 12, 1), 31)
        );
    }

    @ParameterizedTest
    @MethodSource("dateToDateProvider")
    void testGetLastDateOfMonth(Pair<LocalDate, LocalDate> paramPair) {
        setUp(defaultSettings());
        LocalDate date = paramPair.getKey();
        LocalDate expectedResult = paramPair.getValue();
        LocalDate actualResult = dateManager.getLastDateOfMonth(date);
        assertEquals(expectedResult, actualResult);
    }

    static Stream<Pair<LocalDate, LocalDate>> dateToDateProvider() {
        return Stream.of(
                Pair.of(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29)),
                Pair.of(LocalDate.of(2021, 2, 1), LocalDate.of(2021, 2, 28)),
                Pair.of(LocalDate.of(2022, 12, 1), LocalDate.of(2022, 12, 31))
        );
    }

    private void setUp(Settings settings) {
        this.settings = settings;
        dateManager = new DateManager(settings);
    }
}