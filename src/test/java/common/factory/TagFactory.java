package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.Tag;
import yehor.budget.web.dto.full.TagFullDto;
import yehor.budget.web.dto.limited.TagLimitedDto;

import java.util.Set;

import static common.factory.ExpenseFactory.DEFAULT_EXPENSE_ID;
import static common.factory.ExpenseFactory.defaultExpense;
import static common.factory.ExpenseFactory.thirdExpense;

@UtilityClass
public class TagFactory {

    public static final long DEFAULT_TAG_ID = 1L;
    public static final long SECOND_TAG_ID = 2L;

    public static TagLimitedDto defaultTagLimitedDto() {
        return TagLimitedDto.builder()
                .name("Donation")
                .build();
    }

    public static TagFullDto defaultTagFullDto() {
        return TagFullDto.builder()
                .id(DEFAULT_TAG_ID)
                .name("Donation")
                .build();
    }

    public static Tag defaultTag() {
        return Tag.builder()
                .id(DEFAULT_TAG_ID)
                .name("Donation")
                .build();
    }

    public static TagLimitedDto secondTagLimitedDto() {
        return TagLimitedDto.builder()
                .name("Trip-22")
                .build();
    }

    public static TagFullDto secondTagFullDto() {
        return TagFullDto.builder()
                .id(SECOND_TAG_ID)
                .name("Trip-22")
                .build();
    }

    public static Tag secondTag() {
        return Tag.builder()
                .id(SECOND_TAG_ID)
                .name("Trip-22")
                .build();
    }

    public static Tag tagWithExpenses() {
        return Tag.builder()
                .id(DEFAULT_EXPENSE_ID)
                .name("MyTag")
                .expenses(Set.of(defaultExpense(), thirdExpense()))
                .build();
    }
}
