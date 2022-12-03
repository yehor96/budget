package yehor.budget.service.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.mockito.ArgumentCaptor;
import org.springframework.core.env.Environment;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.helper.CalculatorHelper;
import yehor.budget.entity.Category;
import yehor.budget.entity.Expense;
import yehor.budget.entity.RowEstimatedExpense;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.repository.RowEstimatedExpenseRepository;
import yehor.budget.service.SettingsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
    void testExpensesOfSameMonthSameCategoryDifDayBucketsWithNotExistingRowInDb() {
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
    void testExpensesOfSameMonthSameCategoryDifDayBucketsWithExistingRowInDb() {
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
    void testEmptyExpenses() {
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
    void testExpensesOfSameMonthDifCategories() {
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
    void testExpensesOfDifMonthsDifCategoriesDifBuckets() {
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
    void testExpensesOfSameMonthButDifYearsShouldBeCountedAsTwoMonths() {
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
    void testExpensesOfDifMonthsSameCategorySameBucket() {
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
}