/**
 * Modèle des préférences utilisateur.
 * Gère le volume musique et SFX, le mode plein écran, la langue,
 * les raccourcis clavier et la résolution d’écran.
 * Permet de charger et sauvegarder ces paramètres dans un fichier.
 */
package Modeles.settings;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SettingsModel {
    private static final String FILE_PATH = "Benfdal_Croizier/config/settings.conf";

    private int musicVolume;
    private int sfxVolume ;
    private boolean fullscreen;
    private String langue;
    private Map<String, String> touches;
    private String resolution;

    /**
     * Initialise un nouveau SettingsModel avec les valeurs par défaut :
     * musicVolume=50, sfxVolume=50, fullscreen=false, langue="Français",
     * touches par défaut (Z,S,Q,D) et résolution "1280x720".
     */
    public SettingsModel() {
        // Valeurs par défaut
        musicVolume = 50;
        sfxVolume = 50;
        fullscreen = false;
        langue = "Français";
        touches = new HashMap<>();
        touches.put("moveUp", "w");
        touches.put("moveDown", "s");
        touches.put("moveLeft", "a");
        touches.put("moveRight", "d");
        resolution  = "1280x720";
    }

    /**
     * Charge les paramètres depuis le fichier settings.conf.
     * Si le fichier ou son dossier parent n’existe pas, crée-les
     * et retourne un modèle aux valeurs par défaut.
     *
     * @return un SettingsModel chargé ou aux valeurs par défaut
     */
    public static SettingsModel load() {
        SettingsModel model = new SettingsModel();
        File file = new File(FILE_PATH);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            System.out.println("📁 Aucune config détectée, chargement des valeurs par défaut.");
            return model;
        }

        try (FileReader reader = new FileReader(file)) {
            Properties props = new Properties();
            props.load(reader);

            model.musicVolume = Integer.parseInt(props.getProperty("musicVolume", "50"));
            model.sfxVolume = Integer.parseInt(props.getProperty("sfxVolume", "50"));
            model.fullscreen = Boolean.parseBoolean(props.getProperty("fullscreen", "false"));
            model.langue = props.getProperty("langue", "Français");
            model.resolution = props.getProperty("resolution", model.resolution);

            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("touches.")) {
                    String action = key.substring("touches.".length());
                    model.touches.put(action, props.getProperty(key));
                }
            }

            System.out.println("✅ Paramètres chargés depuis " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("❌ Erreur lecture fichier config : " + e.getMessage());
        }

        return model;
    }

    /**
     * Sauvegarde les paramètres courants dans le fichier settings.conf.
     * Crée le dossier de configuration si nécessaire.
     */
    public void save() {
        Properties props = new Properties();
        props.setProperty("musicVolume", String.valueOf(musicVolume));
        props.setProperty("sfxVolume", String.valueOf(sfxVolume));
        props.setProperty("fullscreen", String.valueOf(fullscreen));
        props.setProperty("langue", langue);
        props.setProperty("resolution", resolution);
        for (Map.Entry<String, String> entry : touches.entrySet()) {
            props.setProperty("touches." + entry.getKey(), entry.getValue());
        }

        File configDir = new File("config");
        if (!configDir.exists()) configDir.mkdir();

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            props.store(writer, "Paramètres du jeu");
            System.out.println("💾 Paramètres sauvegardés.");
        } catch (IOException e) {
            System.err.println("❌ Erreur écriture fichier config : " + e.getMessage());
        }
    }

    /**
     * Retourne le volume de la musique (0–100).
     * @return niveau de volume musique
     */
    public int getMusicVolume() { return musicVolume; }

    /**
     * Définit le volume de la musique et le persiste.
     * @param volume valeur entre 0 et 100
     */
    public void setMusicVolume(int volume) { this.musicVolume = volume; }

    /**
     * Retourne le volume des effets sonores (0–100).
     * @return niveau de volume SFX
     */
    public int getSfxVolume() { return sfxVolume; }

    /**
     * Définit le volume des effets sonores et le persiste.
     * @param volume valeur entre 0 et 100
     */
    public void setSfxVolume(int volume) { this.sfxVolume = volume; }

    /**
     * Indique si l’application est en plein écran.
     * @return true si plein écran, false sinon
     */
    public boolean isFullscreen() { return fullscreen; }

    /**
     * Active ou désactive le plein écran et le persiste.
     * @param fullscreen true pour plein écran, false pour fenêtre
     */
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }

    /**
     * Retourne la langue de l’interface.
     * @return nom de la langue (ex. "Français")
     */
    public String getLangue() { return langue; }

    /**
     * Change la langue de l’interface et la persiste.
     * @param langue nouveau nom de langue
     */
    public void setLangue(String langue) { this.langue = langue; }

    /**
     * Retourne la map des raccourcis clavier (action→touche).
     * @return map des touches configurées
     */
    public Map<String, String> getTouches() { return touches; }

    /**
     * Remplace la map des raccourcis clavier et la persiste.
     * @param touches map action→touche
     */
    public void setTouches(Map<String, String> touches) { this.touches = touches; }

    /**
     * Réinitialise les touches aux valeurs par défaut (Z,S,Q,D) et persiste.
     */
    public void resetTouchesParDefaut() {
        Map<String, String> defaut = Map.of(
                "moveUp", "Z",
                "moveDown", "S",
                "moveLeft", "Q",
                "moveRight", "D"
        );
        this.touches.clear();
        this.touches.putAll(defaut);
    }

    /**
     * Retourne la résolution écran sélectionnée (format "Larg×Haut").
     * @return résolution courante, ex. "1280x720"
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * Définit la résolution écran (format "Larg×Haut") et la persiste.
     * @param resolution nouvelle résolution
     */
   public void setResolution(String resolution) {
        this.resolution = resolution;
    }

}
