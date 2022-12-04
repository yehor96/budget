package yehor.budget.service;

import org.junit.jupiter.api.Test;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.common.util.PropertiesHelper;
import yehor.budget.entity.Settings;
import yehor.budget.repository.SettingsRepository;
import yehor.budget.web.converter.SettingsConverter;
import yehor.budget.web.dto.full.SettingsFullDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

import static common.factory.SettingsFactory.DEFAULT_SETTINGS_ID;
import static common.factory.SettingsFactory.defaultSettings;
import static common.factory.SettingsFactory.defaultSettingsFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SettingsServiceTest {

    private final PropertiesHelper propertiesHelperMock = mock(PropertiesHelper.class);
    private final SettingsRepository settingsRepositoryMock = mock(SettingsRepository.class);
    private final SettingsConverter settingsConverterMock = mock(SettingsConverter.class);

    private final SettingsService settingsService =
            new SettingsService(propertiesHelperMock, settingsRepositoryMock, settingsConverterMock);

    @Test
    void testGetSettingsAndGetEntity() {
        Settings settings = defaultSettings();
        SettingsFullDto expectedSettingsDto = defaultSettingsFullDto();

        when(settingsRepositoryMock.getById(DEFAULT_SETTINGS_ID)).thenReturn(settings);
        when(settingsConverterMock.convert(settings)).thenReturn(expectedSettingsDto);

        SettingsFullDto actualSettingsDto = settingsService.getSettings();

        verify(settingsRepositoryMock, times(1))
                .getById(DEFAULT_SETTINGS_ID);

        assertEquals(expectedSettingsDto, actualSettingsDto);
    }

    @Test
    void testGetSettingsThrowsEntityNotFoundExceptionWhenThereAreNoSettingsWithDefaultId() {
        doThrow(new EntityNotFoundException()).when(settingsRepositoryMock).getById(DEFAULT_SETTINGS_ID);

        try {
            settingsService.getSettings();
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }

        verify(settingsRepositoryMock, times(1))
                .getById(DEFAULT_SETTINGS_ID);
    }

    @Test
    void updateSettingsSavesNewSettingsAndNotifiesListeners() {
        try (var mock = mockStatic(SettingsNotificationManager.class)) {
            Settings newSettings = defaultSettings();

            settingsService.updateSettings(newSettings);

            verify(settingsRepositoryMock, times(1))
                    .save(newSettings);
            mock.verify(() -> SettingsNotificationManager.updateListeners(eq(SettingsService.class), eq(newSettings)));
        }
    }

    @Test
    void updateSettingsSavesNewSettingsAndKeepsExistingSettingWhenNewOneIsNullAndNotifiesListeners() {
        try (var mock = mockStatic(SettingsNotificationManager.class)) {
            LocalDate newStartDate = LocalDate.of(2000, 10, 10);

            Settings newSettings = Settings.builder()
                    .isBudgetDateValidation(null)
                    .budgetEndDate(null)
                    .budgetStartDate(newStartDate)
                    .build();
            Settings existingSettings = defaultSettings();
            Settings expectedSettings = defaultSettings();
            expectedSettings.setBudgetStartDate(newStartDate);

            when(settingsRepositoryMock.getById(DEFAULT_SETTINGS_ID)).thenReturn(existingSettings);

            settingsService.updateSettings(newSettings);

            verify(settingsRepositoryMock, times(1))
                    .save(expectedSettings);
            mock.verify(() -> SettingsNotificationManager.updateListeners(eq(SettingsService.class), eq(expectedSettings)));
        }
    }
}