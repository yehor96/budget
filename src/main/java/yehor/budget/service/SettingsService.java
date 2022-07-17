package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import yehor.budget.common.date.DateManager;
import yehor.budget.entity.Settings;
import yehor.budget.repository.SettingsRepository;
import yehor.budget.web.converter.SettingsConverter;
import yehor.budget.web.dto.full.SettingsFullDto;
import yehor.budget.web.dto.limited.SettingsLimitedDto;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsService implements InitializingBean {

    private static final Logger LOG = LogManager.getLogger(SettingsService.class);
    private static final Long SETTING_ID = 1L;

    private final Environment environment;
    private final SettingsRepository settingsRepository;
    private final SettingsConverter settingsConverter;

    public SettingsFullDto getSettings() {
        Settings settings = settingsRepository.findById(SETTING_ID)
                .orElseThrow(() -> new IllegalStateException("Settings have not been initialized in database"));
        return settingsConverter.convert(settings);
    }

    @Transactional
    public void updateSettings(SettingsLimitedDto settingsLimitedDto) {
        Settings settings = settingsConverter.convert(settingsLimitedDto);
        settings.setId(SETTING_ID);
        LOG.info("Updating settings: {}", settingsLimitedDto);
        settingsRepository.saveById(settings);
    }

    @Override
    public void afterPropertiesSet() {
        if (settingsRepository.findById(SETTING_ID).isEmpty()) {
            Settings defaultSettings = defaultSettings();
            LOG.info("Initializing default settings: {}", defaultSettings);
            settingsRepository.save(defaultSettings);
        }
    }

    private Settings defaultSettings() {
        boolean budgetDateValidation = Boolean.TRUE.equals(
                environment.getProperty("budget.date.validation", Boolean.class));

        return Settings.builder()
                .id(SETTING_ID)
                .isBudgetDateValidation(budgetDateValidation)
                .budgetStartDate(DateManager.START_DATE)
                .budgetEndDate(DateManager.getEndDate())
                .build();
    }
}
