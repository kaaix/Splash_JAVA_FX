package Modeles.settings;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SettingsModel {
    private static final String FILE_PATH = "config/settings.conf";

    private int volume;
    private boolean fullscreen;
    private String langue;
    private Map<String, String> touches;

    public SettingsModel() {
        // Valeurs par défaut
        volume = 50;
        fullscreen = false;
        langue = "Français";
        touches = new HashMap<>();
        touches.put("moveUp", "Z");
        touches.put("moveDown", "S");
        touches.put("moveLeft", "Q");
        touches.put("moveRight", "D");
    }

    public static SettingsModel load() {
        SettingsModel model = new SettingsModel();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("📁 Aucune config détectée, chargement des valeurs par défaut.");
            return model;
        }

        try (FileReader reader = new FileReader(file)) {
            Properties props = new Properties();
            props.load(reader);

            model.volume = Integer.parseInt(props.getProperty("volume", "50"));
            model.fullscreen = Boolean.parseBoolean(props.getProperty("fullscreen", "false"));
            model.langue = props.getProperty("langue", "Français");

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

    public void save() {
        Properties props = new Properties();
        props.setProperty("volume", String.valueOf(volume));
        props.setProperty("fullscreen", String.valueOf(fullscreen));
        props.setProperty("langue", langue);
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

    // === Getters & Setters ===
    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public Map<String, String> getTouches() { return touches; }
    public void setTouches(Map<String, String> touches) { this.touches = touches; }
}
