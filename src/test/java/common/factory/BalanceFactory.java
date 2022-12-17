package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.BalanceItem;
import yehor.budget.entity.BalanceRecord;
import yehor.budget.web.dto.full.BalanceItemFullDto;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
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
