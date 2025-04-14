package utils;

import javafx.scene.media.AudioClip;
import java.io.File;

public class SoundEffects {
    private static double volume = 1.0;

    public static void setVolume(double newVolume) {
        volume = Math.max(0, Math.min(1.0, newVolume));
    }

    public static void play(String path) {
        try {
            AudioClip sound = new AudioClip(new File(path).toURI().toString());
            sound.setVolume(volume);
            sound.play();
        } catch (Exception e) {
            System.err.println("❌ Erreur son bouton : " + e.getMessage());
        }
    }
}
