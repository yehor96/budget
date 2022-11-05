package yehor.budget.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.common.date.DateManager;
import yehor.budget.repository.SettingsRepository;
import yehor.budget.service.SettingsService;
import yehor.budget.service.client.currency.CurrencyRateClient;
import yehor.budget.service.client.currency.CurrencyRateSimulator;
import yehor.budget.service.client.currency.ExchangeRateClient;
import yehor.budget.web.converter.SettingsConverter;

@Configuration
@Slf4j
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

    @Bean
    public CurrencyRateClient currencyRateClient(ApplicationContext applicationContext) {
        Boolean isSimulateExchangeRate = applicationContext.getEnvironment()
                .getProperty("api.currency.exchange.simulate", Boolean.class);
        if (Boolean.TRUE.equals(isSimulateExchangeRate)) {
            log.info("Using currency rate simulator");
            return new CurrencyRateSimulator();
        } else {
            return new ExchangeRateClient();
        }
    }

}
