package utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class MusicPlayer {
    private static MediaPlayer mediaPlayer;
    private static double currentVolume = 1.0;


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

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public static void setVolume(double volume) {
        currentVolume = volume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }


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
