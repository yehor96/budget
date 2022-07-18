package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import yehor.budget.common.date.DateManager;
import yehor.budget.entity.Settings;
import yehor.budget.repository.SettingsRepository;
import yehor.budget.web.converter.SettingsConverter;
import yehor.budget.web.dto.full.SettingsFullDto;
import yehor.budget.web.dto.limited.SettingsLimitedDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SettingsServiceTest {

//    private final Environment environmentMock = mock(Environment.class);
//    private final SettingsRepository settingsRepositoryMock = mock(SettingsRepository.class);
//    private final SettingsConverter settingsConverterMock = mock(SettingsConverter.class);
//    private final DateManager dateManager = mock(DateManager.class);
//
//    private final SettingsService settingsService = new SettingsService(
//            environmentMock, settingsRepositoryMock, settingsConverterMock, dateManager);
//
//    @Test
//    void testGetSettings() {
//        Settings settings = Settings.builder()
//                .id(1L)
//                .budgetStartDate(LocalDate.now().minusDays(30))
//                .budgetEndDate(LocalDate.now())
//                .isBudgetDateValidation(true)
//                .build();
//        SettingsFullDto expectedSettingsDto = SettingsFullDto.builder()
//                .budgetStartDate(LocalDate.now().minusDays(30))
//                .budgetEndDate(LocalDate.now())
//                .isBudgetDateValidation(true)
//                .build();
//
//        when(settingsRepositoryMock.findById(1L)).thenReturn(Optional.of(settings));
//        when(settingsConverterMock.convert(settings)).thenReturn(expectedSettingsDto);
//
//        SettingsFullDto actualSettingsDto = settingsService.getSettings();
//
//        assertEquals(expectedSettingsDto, actualSettingsDto);
//    }
//
//    @Test
//    void testGetSettingsThrowsExceptionWhenDbNotInitialized() {
//
//        when(settingsRepositoryMock.findById(1L)).thenReturn(Optional.empty());
//
//        try {
//            settingsService.getSettingsEntity();
//            fail("Exception was not thrown");
//        } catch (Exception e) {
//            assertEquals(EntityNotFoundException.class, e.getClass());
//            IllegalStateException exception = (IllegalStateException) e;
//            assertEquals("Settings have not been initialized in database", exception.getMessage());
//        }
//    }
//
//    @Test
//    void updateSettings() {
//        SettingsLimitedDto settingsDto = SettingsLimitedDto.builder()
//                .isBudgetDateValidation(false)
//                .build();
//        Settings settings = Settings.builder()
//                .isBudgetDateValidation(false)
//                .build();
//        Settings expectedSettings = Settings.builder()
//                .id(1L)
//                .isBudgetDateValidation(false)
//                .build();
//
//        when(settingsConverterMock.convert(settingsDto)).thenReturn(settings);
//
//        settingsService.updateSettings(settingsDto);
//
//        verify(settingsRepositoryMock, times(1))
//                .updateById(expectedSettings);
//    }
}