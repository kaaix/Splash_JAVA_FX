package utils;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TransitionUtils {

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
            fadeIn.setOnFinished(f -> root.getChildren().remove(overlay));
            fadeIn.play();
        });

        fadeOut.play();
    }
}
