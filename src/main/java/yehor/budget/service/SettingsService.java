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
import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SettingsService implements InitializingBean {

    private static final Logger LOG = LogManager.getLogger(SettingsService.class);
    private static final Long SETTING_ID = 1L;

    private final Environment environment;
    private final SettingsRepository settingsRepository;
    private final SettingsConverter settingsConverter;

    public SettingsFullDto getSettings() {
        Settings settings = getSettingsEntity();
        return settingsConverter.convert(settings);
    }

    public Settings getSettingsEntity() {
        return settingsRepository.getById(SETTING_ID);
    }

    @Transactional
    public void updateSettings(SettingsLimitedDto settingsLimitedDto) {
        Settings newSettings = settingsConverter.convert(settingsLimitedDto);
        updateSettings(newSettings);
    }

    @Transactional
    public void updateSettings(Settings newSettings) {
        Settings existingSettings = settingsRepository.getById(SETTING_ID);
        Settings settings = mergeSettings(newSettings, existingSettings); //TODO test 2 dif kinds of merge
        LOG.info("Updating settings: {}", settings);
        DateManager.updateWithSettings(settings);
        settingsRepository.updateById(settings);
    }

    @Override
    public void afterPropertiesSet() {
        if (settingsRepository.findById(SETTING_ID).isEmpty()) { //TODO test
            Settings defaultSettings = defaultSettings(); //TODO test
            LOG.info("Initializing default settings: {}", defaultSettings);
            settingsRepository.save(defaultSettings);
        }
    }

    private Settings defaultSettings() {
        Boolean budgetDateValidation = Boolean.TRUE.equals(
                environment.getProperty("budget.date.validation", Boolean.class));

        return Settings.builder()
                .id(SETTING_ID)
                .isBudgetDateValidation(budgetDateValidation)
                .budgetStartDate(LocalDate.now().minusDays(30))
                .budgetEndDate(LocalDate.now())
                .build();
    }

    private Settings mergeSettings(Settings newSettings, Settings existingSettings) {
        Settings settings = new Settings();
        settings.setId(SETTING_ID);

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
