package yehor.budget.common;

import org.junit.jupiter.api.Test;
import yehor.budget.entity.Settings;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SettingsNotificationManagerTest {

    private final SettingsNotificationManager settingsNotificationManager = new SettingsNotificationManager();

    private final SettingsListener listener1 = mock(SettingsListener.class);
    private final SettingsListener listener2 = mock(SettingsListener.class);

    @Test
    void testUpdateAllListeners() {
        settingsNotificationManager.addListener(String.class, listener1);
        settingsNotificationManager.addListener(String.class, listener2);

        SettingsNotificationManager.updateListeners(String.class, new Settings());

        verify(listener1, times(1)).onUpdate(any(Settings.class));
        verify(listener2, times(1)).onUpdate(any(Settings.class));
    }

}