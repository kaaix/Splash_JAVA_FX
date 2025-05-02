/**
 * Utilitaire pour jouer des effets sonores.
 * Permet de régler un volume global et de lancer la lecture
 * d’un fichier audio au format supporté par JavaFX.
 */
package utils;

import javafx.scene.media.AudioClip;
import java.io.File;

public class SoundEffects {
    private static double volume = 1.0;

    /**
     * Définit le volume global des effets sonores.
     * Le volume est borné entre 0.0 (silence) et 1.0 (volume maximal).
     *
     * @param newVolume le nouveau volume (valeur entre 0.0 et 1.0)
     */
    public static void setVolume(double newVolume) {
        volume = Math.max(0, Math.min(1.0, newVolume));
    }

    /**
     * Joue un effet sonore à partir du chemin de fichier fourni.
     * Si une erreur survient (fichier introuvable ou format non supporté),
     * une erreur est logguée sur la sortie d’erreur.
     *
     * @param path le chemin vers le fichier audio à jouer
     */
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
