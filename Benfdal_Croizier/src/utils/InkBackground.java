/**
 * Pane animé affichant des “bulles d’encre” aléatoires
 * qui apparaissent et se déplacent pour créer un fond dynamique.
 * Les bulles sont de taille et de couleur aléatoires, puis disparaissent.
 */
package utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Random;

public class InkBackground extends Pane {
    private final Random rand = new Random();

    /**
     * Initialise l’animation continue d’ajout de bulles d’encre.
     * Crée une Timeline qui invoque addInk() toutes les 0.2 secondes.
     */
    public InkBackground() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), e -> addInk()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Crée une nouvelle “bulle” (Circle) de taille et couleur aléatoires,
     * la place à une position aléatoire dans le Pane, puis lance
     * une transition pour la déplacer et la retirer à la fin.
     */
    private void addInk() {
        Circle bubble = new Circle(30 + rand.nextInt(40));
        bubble.setFill(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 0.2));
        bubble.setLayoutX(rand.nextDouble() * getWidth());
        bubble.setLayoutY(rand.nextDouble() * getHeight());
        getChildren().add(bubble);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(10), bubble);
        transition.setByX(rand.nextDouble() * 200 - 100);
        transition.setByY(rand.nextDouble() * 200 - 100);
        transition.setOnFinished(e -> getChildren().remove(bubble));
        transition.play();
    }
}
