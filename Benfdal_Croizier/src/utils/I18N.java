package utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
    private static ResourceBundle bundle = ResourceBundle.getBundle("lang.messages", Locale.FRENCH);

    public static void setLangue(String langue) {
        Locale locale;
        switch (langue) {
            case "Français" -> locale = Locale.FRENCH;
            case "日本語" -> locale = Locale.JAPANESE;
            default -> locale = Locale.ENGLISH;
        }
        bundle = ResourceBundle.getBundle("lang.messages", locale); // 👈 reste avec lang.messages
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static String getFormatted(String key, Object... args) {
        return String.format(get(key), args);
    }
}
