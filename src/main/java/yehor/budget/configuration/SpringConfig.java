package yehor.budget.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.common.date.DateManager;
import yehor.budget.repository.SettingsRepository;
import yehor.budget.service.SettingsService;
import yehor.budget.web.converter.SettingsConverter;

@Configuration
public class SpringConfig {

    @Bean
    public SettingsNotificationManager notificationManager(ApplicationContext applicationContext) {
        SettingsNotificationManager notificationManager = new SettingsNotificationManager();
        notificationManager.addListener(SettingsService.class, dateManager(applicationContext));
        notificationManager.addListener(DateManager.class, settingsService(applicationContext));
        return notificationManager;
    }

    @Bean
    public DateManager dateManager(ApplicationContext applicationContext) {
        return new DateManager(settingsService(applicationContext).getSettingsEntity());
    }

    @Bean
    public SettingsService settingsService(ApplicationContext applicationContext) {
        return new SettingsService(
                applicationContext.getEnvironment(),
                applicationContext.getBean(SettingsRepository.class),
                applicationContext.getBean(SettingsConverter.class));
    }

}
