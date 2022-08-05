package yehor.budget.common;

import yehor.budget.entity.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsNotificationManager {

    private static final Map<Class<?>, List<SettingsListener>> listeners = new HashMap<>();

    public void addListener(Class<?> publisher, SettingsListener listener) {
        listeners.putIfAbsent(publisher, new ArrayList<>());
        listeners.get(publisher).add(listener);
    }

    public static void updateListeners(Class<?> publisher, Settings settings) {
        listeners.get(publisher).forEach(listener -> listener.onUpdate(settings));
    }
}
