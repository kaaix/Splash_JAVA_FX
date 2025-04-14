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

    public InkBackground() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), e -> addInk()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

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
