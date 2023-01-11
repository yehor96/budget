package yehor.budget.service.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.mockito.ArgumentCaptor;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.util.CalculatorHelper;
import yehor.budget.entity.Category;
import yehor.budget.entity.Expense;
import yehor.budget.entity.RowEstimatedExpense;
import yehor.budget.entity.Settings;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.repository.RowEstimatedExpenseRepository;
import yehor.budget.service.SettingsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static common.factory.SettingsFactory.defaultSettings;
import static common.factory.SettingsFactory.settingsWithNonDefaultEstimatedExpenseWorkerProperties;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EstimatedExpenseWorkerTest {

    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final RowEstimatedExpenseRepository rowEstimatedExpenseRepository = mock(RowEstimatedExpenseRepository.class);
    private final CalculatorHelper calculatorHelper = mock(CalculatorHelper.class);
    private final SettingsService settingsService = mock(SettingsService.class);
    private final DateManager dateManager = mock(DateManager.class);

    private final EstimatedExpenseWorker worker = new EstimatedExpenseWorker(expenseRepository, categoryRepository,
            rowEstimatedExpenseRepository, calculatorHelper, settingsService, dateManager);

    private final ArgumentCaptor<RowEstimatedExpense> rowEstimatedExpenseArgumentCaptor =
            ArgumentCaptor.forClass(RowEstimatedExpense.class);

    private final Category category1 = Category.builder().id(1L).build();
    private final Category category2 = Category.builder().id(2L).build();

    @Test
    void testOnUpdateRestartsTask() {
        setUpWorkerProperties(5, 5, "1y");
        ScheduledThreadPoolExecutor mockedExecutor = mock(ScheduledThreadPoolExecutor.class);
        worker.executor = mockedExecutor;
        Settings settings = settingsWithNonDefaultEstimatedExpenseWorkerProperties();

        when(mockedExecutor.isShutdown()).thenReturn(true);

        worker.onUpdate(settings);

        verify(mockedExecutor, times(1)).shutdown();
        assertEquals(settings.getEstimatedExpenseWorkerInitDelay(), worker.currentInitDelay);
        assertEquals(settings.getEstimatedExpenseWorkerPeriod(), worker.currentPeriod);
        assertEquals(settings.getEstimatedExpenseWorkerEndDateScopePattern(), worker.currentEstimationScopePattern);
    }

    @Test
    void testOnUpdateNotRestartsTaskWhenPassedPropertiesAreTheSameAsExistingProperties() {
        setUpWorkerProperties(5, 5, "1y");
        ScheduledThreadPoolExecutor mockedExecutor = mock(ScheduledThreadPoolExecutor.class);
        worker.executor = mockedExecutor;
        Settings settings = defaultSettings();

        worker.onUpdate(settings);

        verify(mockedExecutor, never()).shutdown();
        assertEquals(5, worker.currentInitDelay);
        assertEquals(5, worker.currentPeriod);
        assertEquals("1y", worker.currentEstimationScopePattern);
    }

    @Test
    void testOnUpdateNotRestartsTaskWhenPassedPropertiesAreTheSameEndDateScopePatternIsNotValid() {
        setUpWorkerProperties(5, 5, "1y");
        ScheduledThreadPoolExecutor mockedExecutor = mock(ScheduledThreadPoolExecutor.class);
        worker.executor = mockedExecutor;
        Settings settings = defaultSettings();
        settings.setEstimatedExpenseWorkerEndDateScopePattern("invalid-pattern");

        worker.onUpdate(settings);

        verify(mockedExecutor, never()).shutdown();
        assertEquals(5, worker.currentInitDelay);
        assertEquals(5, worker.currentPeriod);
        assertEquals("1y", worker.currentEstimationScopePattern);
    }

    @Test
    void testOnUpdateNotRestartsTaskWhenPassedPropertiesAreNotTheSameEndDateScopePatternIsNotValid() {
        setUpWorkerProperties(5, 5, "1y");
        ScheduledThreadPoolExecutor mockedExecutor = mock(ScheduledThreadPoolExecutor.class);
        worker.executor = mockedExecutor;
        Settings settings = settingsWithNonDefaultEstimatedExpenseWorkerProperties();
        settings.setEstimatedExpenseWorkerEndDateScopePattern("invalid-pattern");

        when(mockedExecutor.isShutdown()).thenReturn(true);

        worker.onUpdate(settings);

        verify(mockedExecutor, times(1)).shutdown();
        assertEquals(settings.getEstimatedExpenseWorkerInitDelay(), worker.currentInitDelay);
        assertEquals(settings.getEstimatedExpenseWorkerPeriod(), worker.currentPeriod);
        assertEquals("1y", worker.currentEstimationScopePattern);
    }

    @Test
    void testTaskExpensesOfSameMonthSameCategoryDifDayBucketsWithNotExistingRowInDb() {
        setUpWorkerProperties(5, 5, "1y");
        Expense expense1 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 1))
                .category(category1)
                .build();
        Expense expense2 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 10))
                .category(category1)
                .build();
        List<Expense> expenses = List.of(expense1, expense2);

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);
        when(calculatorHelper.divide(BigDecimal.TEN, BigDecimal.ONE)).thenReturn(BigDecimal.TEN);
        when(categoryRepository.getById(1L)).thenReturn(category1);
        when(rowEstimatedExpenseRepository.existsByCategoryId(1L)).thenReturn(false);

        worker.new EstimatedExpenseTask().run();

        verify(rowEstimatedExpenseRepository, times(1))
                .save(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category1, actualRow.getCategory());
        assertEquals(BigDecimal.TEN, actualRow.getDays1to7());
        assertEquals(BigDecimal.TEN, actualRow.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow.getDays22to31());
    }

    @Test
    void testTaskExpensesOfSameMonthSameCategoryDifDayBucketsWithExistingRowInDb() {
        setUpWorkerProperties(5, 5, "1y");
        Expense expense1 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 1))
                .category(category1)
                .build();
        Expense expense2 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 10))
                .category(category1)
                .build();
        List<Expense> expenses = List.of(expense1, expense2);

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);
        when(calculatorHelper.divide(BigDecimal.TEN, BigDecimal.ONE)).thenReturn(BigDecimal.TEN);
        when(categoryRepository.getById(1L)).thenReturn(category1);
        when(rowEstimatedExpenseRepository.existsByCategoryId(1L)).thenReturn(true);

        worker.new EstimatedExpenseTask().run();

        verify(rowEstimatedExpenseRepository, times(1))
                .updateByCategoryId(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category1, actualRow.getCategory());
        assertEquals(BigDecimal.TEN, actualRow.getDays1to7());
        assertEquals(BigDecimal.TEN, actualRow.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow.getDays22to31());
    }

    @Test
    void testTaskEmptyExpenses() {
        setUpWorkerProperties(5, 5, "1y");
        List<Expense> expenses = Collections.emptyList();

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);

        worker.new EstimatedExpenseTask().run();

        verify(rowEstimatedExpenseRepository, never())
                .save(any());
        verify(rowEstimatedExpenseRepository, never())
                .updateByCategoryId(any());
    }

    @Test
    void testTaskCatchesExceptionAndNotThrowsItOutOfMethod() {
        doThrow(new RuntimeException()).when(expenseRepository).findAllRegularInInterval(any(), any());

        worker.new EstimatedExpenseTask().run();

        assertDoesNotThrow((ThrowingSupplier<Exception>) Exception::new);
    }

    @Test
    void testTaskExpensesOfSameMonthDifCategories() {
        setUpWorkerProperties(5, 5, "1y");
        Expense expense1 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 1))
                .category(category1)
                .build();
        Expense expense2 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 10))
                .category(category2)
                .build();
        List<Expense> expenses = List.of(expense1, expense2);

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);
        when(calculatorHelper.divide(BigDecimal.TEN, BigDecimal.ONE)).thenReturn(BigDecimal.TEN);
        when(categoryRepository.getById(1L)).thenReturn(category1);
        when(categoryRepository.getById(2L)).thenReturn(category2);
        when(rowEstimatedExpenseRepository.existsByCategoryId(1L)).thenReturn(true);
        when(rowEstimatedExpenseRepository.existsByCategoryId(2L)).thenReturn(false);

        worker.new EstimatedExpenseTask().run();

        verify(rowEstimatedExpenseRepository, times(1))
                .updateByCategoryId(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category1, actualRow.getCategory());
        assertEquals(BigDecimal.TEN, actualRow.getDays1to7());
        assertEquals(BigDecimal.ZERO, actualRow.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow.getDays22to31());

        verify(rowEstimatedExpenseRepository, times(1))
                .save(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow2 = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category2, actualRow2.getCategory());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays1to7());
        assertEquals(BigDecimal.TEN, actualRow2.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays22to31());
    }

    @Test
    void testTaskExpensesOfDifMonthsDifCategoriesDifBuckets() {
        setUpWorkerProperties(5, 5, "1y");
        Expense expense1 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 1))
                .category(category1)
                .build();
        Expense expense2 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 2, 10))
                .category(category2)
                .build();
        List<Expense> expenses = List.of(expense1, expense2);

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);
        when(calculatorHelper.divide(BigDecimal.TEN, BigDecimal.valueOf(2))).thenReturn(BigDecimal.valueOf(5));
        when(categoryRepository.getById(1L)).thenReturn(category1);
        when(categoryRepository.getById(2L)).thenReturn(category2);
        when(rowEstimatedExpenseRepository.existsByCategoryId(1L)).thenReturn(true);
        when(rowEstimatedExpenseRepository.existsByCategoryId(2L)).thenReturn(false);

        worker.new EstimatedExpenseTask().run();

        verify(rowEstimatedExpenseRepository, times(1))
                .updateByCategoryId(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category1, actualRow.getCategory());
        assertEquals(BigDecimal.valueOf(5), actualRow.getDays1to7());
        assertEquals(BigDecimal.ZERO, actualRow.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow.getDays22to31());

        verify(rowEstimatedExpenseRepository, times(1))
                .save(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow2 = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category2, actualRow2.getCategory());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays1to7());
        assertEquals(BigDecimal.valueOf(5), actualRow2.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays22to31());
    }

    @Test
    void testTaskExpensesOfSameMonthButDifYearsShouldBeCountedAsTwoMonths() {
        setUpWorkerProperties(5, 5, "1y");
        Expense expense1 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2021, 1, 1))
                .category(category1)
                .build();
        Expense expense2 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 10))
                .category(category2)
                .build();
        List<Expense> expenses = List.of(expense1, expense2);

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);
        when(calculatorHelper.divide(BigDecimal.TEN, BigDecimal.valueOf(2))).thenReturn(BigDecimal.valueOf(5));
        when(categoryRepository.getById(1L)).thenReturn(category1);
        when(categoryRepository.getById(2L)).thenReturn(category2);
        when(rowEstimatedExpenseRepository.existsByCategoryId(1L)).thenReturn(true);
        when(rowEstimatedExpenseRepository.existsByCategoryId(2L)).thenReturn(false);

        worker.new EstimatedExpenseTask().run();

        verify(rowEstimatedExpenseRepository, times(1))
                .updateByCategoryId(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category1, actualRow.getCategory());
        assertEquals(BigDecimal.valueOf(5), actualRow.getDays1to7());
        assertEquals(BigDecimal.ZERO, actualRow.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow.getDays22to31());

        verify(rowEstimatedExpenseRepository, times(1))
                .save(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow2 = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category2, actualRow2.getCategory());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays1to7());
        assertEquals(BigDecimal.valueOf(5), actualRow2.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow2.getDays22to31());
    }

    @Test
    void testTaskExpensesOfDifMonthsSameCategorySameBucket() {
        setUpWorkerProperties(5, 5, "1y");
        Expense expense1 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 1, 1))
                .category(category1)
                .build();
        Expense expense2 = Expense.builder()
                .value(BigDecimal.TEN)
                .date(LocalDate.of(2022, 2, 1))
                .category(category1)
                .build();
        List<Expense> expenses = List.of(expense1, expense2);

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);
        when(calculatorHelper.divide(BigDecimal.valueOf(20), BigDecimal.valueOf(2))).thenReturn(BigDecimal.TEN);
        when(categoryRepository.getById(1L)).thenReturn(category1);
        when(rowEstimatedExpenseRepository.existsByCategoryId(1L)).thenReturn(true);

        worker.new EstimatedExpenseTask().run();

        verify(rowEstimatedExpenseRepository, times(1))
                .updateByCategoryId(rowEstimatedExpenseArgumentCaptor.capture());
        RowEstimatedExpense actualRow = rowEstimatedExpenseArgumentCaptor.getValue();
        assertEquals(category1, actualRow.getCategory());
        assertEquals(BigDecimal.TEN, actualRow.getDays1to7());
        assertEquals(BigDecimal.ZERO, actualRow.getDays8to14());
        assertEquals(BigDecimal.ZERO, actualRow.getDays15to21());
        assertEquals(BigDecimal.ZERO, actualRow.getDays22to31());
    }

    @Test
    void testEstimationEndDatePatternParsingReturns1YearAgo() {
        setUpWorkerProperties(5, 5, "1y");
        List<Expense> expenses = Collections.emptyList();

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);

        worker.new EstimatedExpenseTask().run();

        verify(expenseRepository, times(1))
                .findAllRegularInInterval(LocalDate.now().minusYears(1), LocalDate.now());
    }

    @Test
    void testEstimationEndDatePatternParsingReturns5MonthsAgo() {
        setUpWorkerProperties(5, 5, "5M");
        List<Expense> expenses = Collections.emptyList();

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);

        worker.new EstimatedExpenseTask().run();

        verify(expenseRepository, times(1))
                .findAllRegularInInterval(LocalDate.now().minusMonths(5), LocalDate.now());
    }

    @Test
    void testEstimationEndDatePatternParsingReturns50DaysAgo() {
        setUpWorkerProperties(5, 5, "50d");
        List<Expense> expenses = Collections.emptyList();

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);

        worker.new EstimatedExpenseTask().run();

        verify(expenseRepository, times(1))
                .findAllRegularInInterval(LocalDate.now().minusDays(50), LocalDate.now());
    }

    @Test
    void testEstimationEndDatePatternParsingReturnsExactDate() {
        setUpWorkerProperties(5, 5, "2020-10-10");
        List<Expense> expenses = Collections.emptyList();

        when(expenseRepository.findAllRegularInInterval(any(), any())).thenReturn(expenses);

        worker.new EstimatedExpenseTask().run();

        verify(expenseRepository, times(1))
                .findAllRegularInInterval(LocalDate.of(2020, 10, 10), LocalDate.now());
    }

    public void setUpWorkerProperties(int delay, int period, String pattern) {
        worker.currentInitDelay = delay;
        worker.currentPeriod = period;
        worker.currentEstimationScopePattern = pattern;
    }
}