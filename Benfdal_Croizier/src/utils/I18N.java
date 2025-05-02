/**
 * Utilitaire de gestion de l’internationalisation (i18n).
 * Charge les bundles de ressources selon la langue choisie
 * et permet de récupérer des chaînes traduites.
 */
package utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
    private static ResourceBundle bundle = ResourceBundle.getBundle("lang.messages", Locale.FRENCH);

    /**
     * Change la langue utilisée pour les chaînes traduites.
     * Met à jour le ResourceBundle avec le locale correspondant.
     *
     * @param langue nom de la langue ("Français", "English", "日本語")
     */
    public static void setLangue(String langue) {
        Locale locale;
        switch (langue) {
            case "Français" -> locale = Locale.FRENCH;
            case "日本語" -> locale = Locale.JAPANESE;
            default -> locale = Locale.ENGLISH;
        }
        bundle = ResourceBundle.getBundle("lang.messages", locale); // 👈 reste avec lang.messages
    }

    /**
     * Récupère la chaîne traduite correspondant à la clé donnée.
     *
     * @param key la clé dans le bundle de ressources
     * @return la chaîne localisée
     */
    public static String get(String key) {
        return bundle.getString(key);
    }

    /**
     * Récupère une chaîne traduite et y injecte des paramètres.
     * Utilise String.format sur la valeur localisée.
     *
     * @param key  la clé dans le bundle de ressources
     * @param args les arguments à formater dans la chaîne
     * @return la chaîne localisée formatée
     */
    public static String getFormatted(String key, Object... args) {
        return String.format(get(key), args);
    }
}
