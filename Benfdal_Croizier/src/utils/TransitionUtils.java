/**
 * Classe utilitaire pour animer les transitions entre scènes
 * et gérer la sortie de l’application avec un fondu au noir.
 */
package utils;

import Modeles.settings.SettingsModel;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TransitionUtils {
    /**
     * Réalise un fondu au noir sur la scène courante, remplace
     * le contenu par le nœud fourni, puis refait un fondu vers
     * transparent. À la fin, ré-applique les paramètres utilisateur
     * (langue, plein écran, résolution).
     *
     * @param stage      la fenêtre JavaFX à animer
     * @param newContent le nouveau nœud à afficher dans la scène
     */
    public static void fadeToScene(Stage stage, Node newContent) {
        if (!(stage.getScene().getRoot() instanceof StackPane root)) {
            System.out.println("❗ Le root n’est pas un StackPane, abandon de la transition");
            return;
        }

        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.BLACK);
        overlay.widthProperty().bind(root.widthProperty());
        overlay.heightProperty().bind(root.heightProperty());
        overlay.setOpacity(0);
        root.getChildren().add(overlay);

        // Fade Out (vers noir)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), overlay);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.setOnFinished(e -> {
            // Remplacer l’ancien contenu par le nouveau
            root.getChildren().setAll(newContent, overlay); // ⚠️ l’overlay reste au-dessus

            // Fade In (de noir vers transparent)
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), overlay);
            fadeIn.setFromValue(1);
            fadeIn.setToValue(0);
                        fadeIn.setOnFinished(f -> {
                                // 1) on enlève l’overlay
                                        root.getChildren().remove(overlay);

                                        // 2) ré-applique les paramètres utilisateurs
                                                SettingsModel settings = SettingsModel.load();
                                I18N.setLangue(settings.getLangue());
                                // récupère le Stage à partir du root
                                        Stage s = (Stage) root.getScene().getWindow();
                                s.setFullScreenExitHint("");
                                s.setFullScreen(settings.isFullscreen());
                                if (!settings.isFullscreen()) {
                                        String[] dims = settings.getResolution().split("x");
                                        s.setWidth(Double.parseDouble(dims[0]));
                                        s.setHeight(Double.parseDouble(dims[1]));
                                    }
                            });
            fadeIn.play();
        });

        fadeOut.play();
    }

    /**
     * Réalise un fondu au noir complet sur la scène courante,
     * puis quitte l’application lorsque l’animation est terminée.
     *
     * @param stage la fenêtre JavaFX sur laquelle appliquer le fondu
     */
    public static void fadeToBlackAndExit(Stage stage) {
        Scene scene = stage.getScene();
        StackPane root = (StackPane) scene.getRoot();

        Rectangle blackOverlay = new Rectangle();
        blackOverlay.setWidth(scene.getWidth());
        blackOverlay.setHeight(scene.getHeight());
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setOpacity(0);

        root.getChildren().add(blackOverlay);

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        fade.setFromValue(0);
        fade.setToValue(1);

        fade.setOnFinished(e -> Platform.exit());
        fade.play();
    }
}
