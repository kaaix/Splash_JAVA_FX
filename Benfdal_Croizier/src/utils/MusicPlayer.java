/**
 * Utilitaire pour jouer et contrôler la musique de fond.
 * Gère le chargement asynchrone, la mise en boucle,
 * le réglage du volume, l’arrêt et le fondu de sortie.
 */
package utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class MusicPlayer {
    private static MediaPlayer mediaPlayer;
    private static double currentVolume = 1.0;

    /**
     * Joue un fichier audio en arrière-plan.
     * Si une lecture est déjà en cours, ne fait rien.
     * Lance la lecture en boucle si demandé.
     *
     * @param path chemin vers le fichier audio sur le système de fichiers
     * @param loop true pour répéter indéfiniment, false pour jouer une seule fois
     */
    public static void play(String path, boolean loop) {
        // Si déjà en cours, on ne relance pas
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        Task<Media> loadMusicTask = new Task<>() {
            @Override
            protected Media call() {
                return new Media(new File(path).toURI().toString());
            }
        };

        loadMusicTask.setOnSucceeded(e -> {
            mediaPlayer = new MediaPlayer(loadMusicTask.getValue());
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setVolume(currentVolume); // ✅ appliquer volume
                if (loop) mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            });
        });


        loadMusicTask.setOnFailed(e -> {
            System.err.println("❌ Erreur chargement musique : " + loadMusicTask.getException());
        });

        new Thread(loadMusicTask).start();
    }

    /**
     * Arrête immédiatement la musique en cours de lecture, si elle existe.
     */
    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Définit le volume de lecture de la musique.
     * Met à jour le volume actuel et l’applique si une musique est en cours.
     *
     * @param volume volume désiré, entre 0.0 (silence) et 1.0 (volume maximal)
     */
    public static void setVolume(double volume) {
        currentVolume = volume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    /**
     * Réduit progressivement le volume sur la durée spécifiée,
     * puis arrête totalement la lecture.
     *
     * @param durationSeconds durée du fondu en secondes
     */
    public static void fadeOutAndStop(double durationSeconds) {
        if (mediaPlayer == null) return;

        final double startVolume = mediaPlayer.getVolume();
        final long steps = 20;
        final double delay = durationSeconds / steps;

        Task<Void> fadeTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < steps; i++) {
                    double newVolume = startVolume * (1.0 - (i + 1) / (double) steps);
                    double clamped = Math.max(0, newVolume);

                    Platform.runLater(() -> mediaPlayer.setVolume(clamped));

                    Thread.sleep((long) (delay * 1000));
                }

                // Attendre un peu pour laisser le dernier setVolume s'appliquer
                Platform.runLater(() -> mediaPlayer.setVolume(0));
                Thread.sleep((long) (delay * 1000)); // ⏳ très court

                Platform.runLater(() -> mediaPlayer.stop());
                return null;
            }
        };
;

        new Thread(fadeTask).start();
    }

}
