package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.SettingsListener;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.common.util.PropertiesHelper;
import yehor.budget.entity.Settings;
import yehor.budget.repository.SettingsRepository;
import yehor.budget.web.converter.SettingsConverter;
import yehor.budget.web.dto.full.SettingsFullDto;
import yehor.budget.web.dto.limited.SettingsLimitedDto;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class SettingsService implements SettingsListener {

    private static final Long SETTINGS_ID = 1L;

    private final PropertiesHelper propertiesHelper;
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
        log.info("Updating settings: {}", settings);
        SettingsNotificationManager.updateListeners(this.getClass(), settings);
        settingsRepository.save(settings);
    }

    @PostConstruct
    private void initialization() {
        if (!settingsRepository.existsById(SETTINGS_ID)) {
            Settings defaultSettings = defaultSettings();
            log.info("Initializing default settings: {}", defaultSettings);
            settingsRepository.save(defaultSettings);
        }
    }

    private Settings defaultSettings() {
        Boolean budgetDateValidation = propertiesHelper.getBooleanProperty("settings.budget.date.validation");
        Integer startDateStepBack = propertiesHelper.getIntProperty("settings.budget.start.date.step.back.days");
        Integer initDelay = propertiesHelper.getIntProperty("estimated.expense.worker.init.delay");
        Integer period = propertiesHelper.getIntProperty("estimated.expense.worker.period");
        String estimatedExpenseWorkerScopePattern = propertiesHelper.getStringProperty(
                "estimated.expense.worker.end.date.scope.pattern");

        return Settings.builder()
                .id(SETTINGS_ID)
                .isBudgetDateValidation(budgetDateValidation)
                .budgetStartDate(LocalDate.now().minusDays(startDateStepBack))
                .budgetEndDate(LocalDate.now())
                .estimatedExpenseWorkerInitDelay(initDelay)
                .estimatedExpenseWorkerPeriod(period)
                .estimatedExpenseWorkerEndDateScopePattern(estimatedExpenseWorkerScopePattern)
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

        if (Objects.isNull(newSettings.getEstimatedExpenseWorkerInitDelay())) {
            settings.setEstimatedExpenseWorkerInitDelay(existingSettings.getEstimatedExpenseWorkerInitDelay());
        } else {
            settings.setEstimatedExpenseWorkerInitDelay(newSettings.getEstimatedExpenseWorkerInitDelay());
        }
        if (Objects.isNull(newSettings.getEstimatedExpenseWorkerPeriod())) {
            settings.setEstimatedExpenseWorkerPeriod(existingSettings.getEstimatedExpenseWorkerPeriod());
        } else {
            settings.setEstimatedExpenseWorkerPeriod(newSettings.getEstimatedExpenseWorkerPeriod());
        }
        if (Objects.isNull(newSettings.getEstimatedExpenseWorkerEndDateScopePattern())) {
            settings.setEstimatedExpenseWorkerEndDateScopePattern(existingSettings.getEstimatedExpenseWorkerEndDateScopePattern());
        } else {
            settings.setEstimatedExpenseWorkerEndDateScopePattern(newSettings.getEstimatedExpenseWorkerEndDateScopePattern());
        }

        return settings;
    }
}
