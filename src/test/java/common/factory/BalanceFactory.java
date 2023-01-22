package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.recording.BalanceItem;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.entity.recording.IncomeSourceRecord;
import yehor.budget.web.dto.full.BalanceItemFullDto;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;
import yehor.budget.web.dto.limited.BalanceItemLimitedDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static common.factory.ActorFactory.DEFAULT_ACTOR_ID;
import static common.factory.ActorFactory.SECOND_ACTOR_ID;
import static common.factory.ActorFactory.defaultActor;
import static common.factory.ActorFactory.defaultActorFullDto;
import static common.factory.ActorFactory.secondActor;
import static common.factory.ActorFactory.secondActorFullDto;
import static common.factory.EstimatedExpenseFactory.defaultEstimatedExpenseFullDto;
import static common.factory.IncomeSourceFactory.defaultIncomeSourceRecords;

@UtilityClass
public class BalanceFactory {

    public static final long DEFAULT_BALANCE_RECORD_ID = 1L;
    public static final long DEFAULT_BALANCE_ITEM_ID = 10L;
    public static final BigDecimal DEFAULT_BALANCE_RECORD_TOTAL = new BigDecimal("110.00");

    public static BalanceRecordFullDto defaultBalanceRecordFullDto() {
        return BalanceRecordFullDto.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemFullDtoList())
                .build();
    }

    public static BalanceRecord defaultBalanceRecord() {
        EstimatedExpenseFullDto estimatedExpenseFullDto = defaultEstimatedExpenseFullDto();
        return BalanceRecord.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemList())
                .total1to7(estimatedExpenseFullDto.getTotal1to7())
                .total8to14(estimatedExpenseFullDto.getTotal8to14())
                .total15to21(estimatedExpenseFullDto.getTotal15to21())
                .total22to31(estimatedExpenseFullDto.getTotal22to31())
                .build();
    }

    public static BalanceRecord balanceRecordWithSetIncomes() {
        EstimatedExpenseFullDto estimatedExpenseFullDto = defaultEstimatedExpenseFullDto();
        BalanceRecord balanceRecord = BalanceRecord.builder()
                .id(DEFAULT_BALANCE_RECORD_ID)
                .date(LocalDate.now())
                .balanceItems(defaultBalanceItemList())
                .total1to7(estimatedExpenseFullDto.getTotal1to7())
                .total8to14(estimatedExpenseFullDto.getTotal8to14())
                .total15to21(estimatedExpenseFullDto.getTotal15to21())
                .total22to31(estimatedExpenseFullDto.getTotal22to31())
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

    public static List<BalanceItem> defaultBalanceItemList() {
        return List.of(defaultBalanceItem(), secondBalanceItem());
    }

    public static List<BalanceItemLimitedDto> defaultBalanceItemLimitedDtoList() {
        return List.of(defaultBalanceItemLimitedDto(), secondBalanceItemLimitedDto());
    }

    public static BalanceItemLimitedDto defaultBalanceItemLimitedDto() {
        return BalanceItemLimitedDto.builder()
                .actorId(DEFAULT_ACTOR_ID)
                .card(BigDecimal.TEN)
                .cash(new BigDecimal("50.00"))
                .build();
    }

    public static BalanceItemLimitedDto secondBalanceItemLimitedDto() {
        return BalanceItemLimitedDto.builder()
                .actorId(SECOND_ACTOR_ID)
                .card(new BigDecimal("20.00"))
                .cash(new BigDecimal("30.00"))
                .build();
    }

    public static BalanceItemFullDto defaultBalanceItemFullDto() {
        return BalanceItemFullDto.builder()
                .id(DEFAULT_BALANCE_ITEM_ID)
                .actor(defaultActorFullDto())
                .card(BigDecimal.TEN)
                .cash(new BigDecimal("50.00"))
                .build();
    }

    public static BalanceItemFullDto secondBalanceItemFullDto() {
        return BalanceItemFullDto.builder()
                .id(20L)
                .actor(secondActorFullDto())
                .card(new BigDecimal("20.00"))
                .cash(new BigDecimal("30.00"))
                .build();
    }

    public static BalanceItem defaultBalanceItem() {
        return BalanceItem.builder()
                .id(DEFAULT_BALANCE_ITEM_ID)
                .actor(defaultActor())
                .card(BigDecimal.TEN)
                .cash(new BigDecimal("50.00"))
                .build();
    }

    public static BalanceItem secondBalanceItem() {
        return BalanceItem.builder()
                .id(20L)
                .actor(secondActor())
                .card(new BigDecimal("20.00"))
                .cash(new BigDecimal("30.00"))
                .build();
    }

}
