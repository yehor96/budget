package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.recording.BalanceItem;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.entity.recording.ExpectedExpenseRecord;
import yehor.budget.entity.recording.IncomeSourceRecord;
import yehor.budget.web.dto.full.BalanceEstimateDto;
import yehor.budget.web.dto.full.BalanceItemFullDto;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.full.BalanceRecordFullDtoWithoutEstimates;
import yehor.budget.web.dto.limited.BalanceItemLimitedDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static common.factory.IncomeSourceFactory.defaultIncomeSourceRecords;

@UtilityClass
public class BalanceFactory {

    public static final long DEFAULT_BALANCE_RECORD_ID = 1L;
    public static final long DEFAULT_BALANCE_ITEM_ID = 10L;
    public static final long DEFAULT_EXPECTED_EXPENSE_RECORD_ID = 100L;
    public static final BigDecimal DEFAULT_BALANCE_RECORD_TOTAL = new BigDecimal("110.00");

    public static BalanceRecordFullDto defaultBalanceRecordFullDto() {
        return BalanceRecordFullDto.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemFullDtoList())
                .build();
    }

    public static BalanceRecordFullDto secondBalanceRecordFullDto() {
        return BalanceRecordFullDto.builder()
                .id(2L)
                .date(LocalDate.now().plusDays(5))
                .balanceItems(secondBalanceItemFullDtoList())
                .build();
    }

    public static BalanceRecordFullDto balanceRecordFullDtoWithEstimates() {
        return BalanceRecordFullDto.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemFullDtoList())
                .balanceEstimates(List.of(defaultBalanceEstimationDto()))
                .build();
    }

    public static BalanceRecordFullDtoWithoutEstimates balanceRecordFullDtoWithoutEstimates() {
        return BalanceRecordFullDtoWithoutEstimates.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemFullDtoList())
                .build();
    }

    public static BalanceEstimateDto defaultBalanceEstimationDto() {
        return new BalanceEstimateDto(
                new BigDecimal("10.00"),
                new BigDecimal("10.00"),
                new BigDecimal("10.00"),
                LocalDate.of(2023, 1, 31)
        );
    }

    public static BalanceRecord defaultBalanceRecord() {
        return BalanceRecord.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemList())
                .expectedExpenseRecord(defautExpectedExpenseRecord())
                .build();
    }

    public static BalanceRecord secondBalanceRecord() {
        return BalanceRecord.builder()
                .id(2L)
                .date(LocalDate.now().plusDays(5))
                .balanceItems(secondBalanceItemList())
                .expectedExpenseRecord(defautExpectedExpenseRecord())
                .build();
    }

    public static BalanceRecord balanceRecordWithSetIncomes() {
        BalanceRecord balanceRecord = BalanceRecord.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemList())
                .expectedExpenseRecord(defautExpectedExpenseRecord())
                .build();
        List<IncomeSourceRecord> incomeSourceRecords = defaultIncomeSourceRecords();
        incomeSourceRecords.forEach(i -> i.setBalanceRecord(balanceRecord));
        balanceRecord.setIncomeSourceRecords(incomeSourceRecords);
        return balanceRecord;
    }

    public static BalanceRecord balanceRecordWithNotSetExpensesAndIncome() {
        return BalanceRecord.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemList())
                .build();
    }

    public static BalanceRecordLimitedDto defaultBalanceRecordLimitedDto() {
        return BalanceRecordLimitedDto.builder()
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemLimitedDtoList())
                .build();
    }

    public static List<BalanceItemFullDto> defaultBalanceItemFullDtoList() {
        return List.of(defaultBalanceItemFullDto(), secondBalanceItemFullDto());
    }

    public static List<BalanceItemFullDto> secondBalanceItemFullDtoList() {
        return List.of(thirdBalanceItemFullDto(), fourthBalanceItemFullDto());
    }

    public static List<BalanceItem> defaultBalanceItemList() {
        return List.of(defaultBalanceItem(), secondBalanceItem());
    }

    public static List<BalanceItem> secondBalanceItemList() {
        return List.of(thirdBalanceItem(), fourthBalanceItem());
    }

    public static List<BalanceItemLimitedDto> defaultBalanceItemLimitedDtoList() {
        return List.of(defaultBalanceItemLimitedDto(), secondBalanceItemLimitedDto());
    }

    public static BalanceItemLimitedDto defaultBalanceItemLimitedDto() {
        return BalanceItemLimitedDto.builder()
                .itemName("actor1")
                .card(BigDecimal.TEN)
                .cash(new BigDecimal("50.00"))
                .build();
    }

    public static BalanceItemLimitedDto secondBalanceItemLimitedDto() {
        return BalanceItemLimitedDto.builder()
                .itemName("actor2")
                .card(new BigDecimal("20.00"))
                .cash(new BigDecimal("30.00"))
                .build();
    }

    public static BalanceItemFullDto defaultBalanceItemFullDto() {
        return BalanceItemFullDto.builder()
                .id(DEFAULT_BALANCE_ITEM_ID)
                .itemName("actor1")
                .card(BigDecimal.TEN)
                .cash(new BigDecimal("50.00"))
                .build();
    }

    public static BalanceItemFullDto secondBalanceItemFullDto() {
        return BalanceItemFullDto.builder()
                .id(20L)
                .itemName("actor2")
                .card(new BigDecimal("20.00"))
                .cash(new BigDecimal("30.00"))
                .build();
    }

    public static BalanceItemFullDto thirdBalanceItemFullDto() {
        return BalanceItemFullDto.builder()
                .id(3L)
                .itemName("actor1")
                .card(new BigDecimal("50.00"))
                .cash(new BigDecimal("30.00"))
                .build();
    }

    public static BalanceItemFullDto fourthBalanceItemFullDto() {
        return BalanceItemFullDto.builder()
                .id(4L)
                .itemName("actor2")
                .card(new BigDecimal("10.00"))
                .cash(new BigDecimal("20.00"))
                .build();
    }

    public static BalanceItem defaultBalanceItem() {
        return BalanceItem.builder()
                .id(DEFAULT_BALANCE_ITEM_ID)
                .itemName("actor1")
                .card(BigDecimal.TEN)
                .cash(new BigDecimal("50.00"))
                .build();
    }

    public static BalanceItem secondBalanceItem() {
        return BalanceItem.builder()
                .id(20L)
                .itemName("actor2")
                .card(new BigDecimal("20.00"))
                .cash(new BigDecimal("30.00"))
                .build();
    }

    public static BalanceItem thirdBalanceItem() {
        return BalanceItem.builder()
                .id(3L)
                .itemName("actor1")
                .card(BigDecimal.TEN)
                .cash(new BigDecimal("30.00"))
                .build();
    }

    public static BalanceItem fourthBalanceItem() {
        return BalanceItem.builder()
                .id(4L)
                .itemName("actor2")
                .card(new BigDecimal("10.00"))
                .cash(new BigDecimal("20.00"))
                .build();
    }

    public static ExpectedExpenseRecord defautExpectedExpenseRecord() {
        return ExpectedExpenseRecord.builder()
                .id(DEFAULT_EXPECTED_EXPENSE_RECORD_ID)
                .total1to7(new BigDecimal("51.00"))
                .total8to14(new BigDecimal("105.00"))
                .total15to21(new BigDecimal("220.00"))
                .total22to31(new BigDecimal("40.00"))
                .build();
    }

}
