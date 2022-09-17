package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import yehor.budget.common.SettingsListener;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.entity.Settings;
import yehor.budget.repository.SettingsRepository;
import yehor.budget.web.converter.SettingsConverter;
import yehor.budget.web.dto.full.SettingsFullDto;
import yehor.budget.web.dto.limited.SettingsLimitedDto;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Objects;

@RequiredArgsConstructor
public class SettingsService implements InitializingBean, SettingsListener {

    private static final Logger LOG = LogManager.getLogger(SettingsService.class);
    private static final Long SETTINGS_ID = 1L;

    private final Environment environment;
    private final SettingsRepository settingsRepository;
    private final SettingsConverter settingsConverter;

    public SettingsFullDto getSettings() {
        Settings settings = getSettingsEntity();
        return settingsConverter.convert(settings);
    }

    public Settings getSettingsEntity() {
        return settingsRepository.getById(SETTINGS_ID);
    }

    @Transactional
    public void updateSettings(SettingsLimitedDto settingsLimitedDto) {
        Settings newSettings = settingsConverter.convert(settingsLimitedDto);
        updateSettings(newSettings);
    }

    @Override
    @Transactional
    public void onUpdate(Settings settings) {
        updateSettings(settings);
    }

    @Transactional
    public void updateSettings(Settings newSettings) {
        Settings existingSettings = settingsRepository.getById(SETTINGS_ID);
        Settings settings = mergeSettings(newSettings, existingSettings);
        LOG.info("Updating settings: {}", settings);
        SettingsNotificationManager.updateListeners(this.getClass(), settings);
        settingsRepository.save(settings);
    }

    @Override
    public void afterPropertiesSet() {
        if (!settingsRepository.existsById(SETTINGS_ID)) {
            Settings defaultSettings = defaultSettings();
            LOG.info("Initializing default settings: {}", defaultSettings);
            settingsRepository.save(defaultSettings);
        }
    }

    private Settings defaultSettings() {
        Boolean budgetDateValidation = Boolean.TRUE.equals(
                environment.getProperty("budget.date.validation", Boolean.class));

        return Settings.builder()
                .id(SETTINGS_ID)
                .isBudgetDateValidation(budgetDateValidation)
                .budgetStartDate(LocalDate.now().minusDays(30))
                .budgetEndDate(LocalDate.now())
                .build();
    }

    private Settings mergeSettings(Settings newSettings, Settings existingSettings) {
        Settings settings = new Settings();
        settings.setId(SETTINGS_ID);

        if (Objects.isNull(newSettings.getBudgetStartDate())) {
            settings.setBudgetStartDate(existingSettings.getBudgetStartDate());
        } else {
            settings.setBudgetStartDate(newSettings.getBudgetStartDate());
        }
        if (Objects.isNull(newSettings.getBudgetEndDate())) {
            settings.setBudgetEndDate(existingSettings.getBudgetEndDate());
        } else {
            settings.setBudgetEndDate(newSettings.getBudgetEndDate());
        }
        if (Objects.isNull(newSettings.getIsBudgetDateValidation())) {
            settings.setIsBudgetDateValidation(existingSettings.getIsBudgetDateValidation());
        } else {
            settings.setIsBudgetDateValidation(newSettings.getIsBudgetDateValidation());
        }
        return settings;
    }
}
