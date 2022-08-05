package yehor.budget;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SettingsNotificationManager notificationManager() {
        SettingsNotificationManager notificationManager = new SettingsNotificationManager();
        notificationManager.addListener(SettingsService.class, dateManager());
        notificationManager.addListener(DateManager.class, settingsService());
        return notificationManager;
    }

    @Bean
    public DateManager dateManager() {
        return new DateManager(settingsService().getSettingsEntity());
    }

    @Bean
    public SettingsService settingsService() {
        return new SettingsService(
                applicationContext.getEnvironment(),
                applicationContext.getBean(SettingsRepository.class),
                applicationContext.getBean(SettingsConverter.class));
    }

}
