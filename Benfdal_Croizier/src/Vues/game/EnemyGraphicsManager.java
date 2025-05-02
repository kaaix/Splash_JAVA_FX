package Vues.game;

import Modeles.characters.Character;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Map;
import java.util.Objects;

public class EnemyGraphicsManager {

    private final Pane enemyLayer;
    private final Map<Character, Pane> enemyViews;

    private final Image mob1;
    private final Image mob2;
    private final Image mob3;

    public EnemyGraphicsManager(Pane enemyLayer, Map<Character, Pane> enemyViews) {
        this.enemyLayer = enemyLayer;
        this.enemyViews = enemyViews;

        // Chargement des sprites de base
        mob1 = new Image(Objects.requireNonNull(getClass().getResource("/assets/image/mob1.png")).toExternalForm());
        mob2 = new Image(Objects.requireNonNull(getClass().getResource("/assets/image/mob2.png")).toExternalForm());
        mob3 = new Image(Objects.requireNonNull(getClass().getResource("/assets/image/mob3.png")).toExternalForm());
    }

    public void addEnemy(Character mob, double x, double y) {
        String spritePath = switch (mob.getName()) {
            case "Boss 1" -> "/assets/image/boss1-1.png";
            case "Boss 2" -> "/assets/image/boss2-2.png";
            case "Boss 3" -> "/assets/image/boss3-1.png";
            case "Final boss" -> "/assets/image/bossfinal-1.png";
            default -> "/assets/image/mob1.png";
        };

        double width = mob.getName().equals("Final boss") || mob.getName().equals("Boss 3") ? 200 : 150;
        double height = width;

        ImageView iv = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(spritePath)).toExternalForm()));
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        iv.setPreserveRatio(true);
        iv.setLayoutY(6);

        Rectangle hpBarFill = new Rectangle(100, 6);
        hpBarFill.setFill(Color.RED);
        Rectangle clip = new Rectangle(100, 6);
        hpBarFill.setClip(clip);

        StackPane hpStack = new StackPane(hpBarFill);
        hpStack.setLayoutX((150 - 100) / 2.0);
        hpStack.setLayoutY(-10);
        hpStack.setPrefSize(100, 6);

        Pane enemyPane = new Pane(iv, hpStack);
        enemyPane.setLayoutX(x);
        enemyPane.setLayoutY(y);

        enemyViews.put(mob, enemyPane);
        enemyLayer.getChildren().add(enemyPane);

        Timeline hpUpdater = new Timeline(
                new KeyFrame(Duration.millis(50), e -> {
                    double ratio = Math.max(0.0, (double) mob.getHealth() / mob.getMaxHealth());
                    clip.setWidth(100 * ratio);
                })
        );
        hpUpdater.setCycleCount(Animation.INDEFINITE);
        hpUpdater.play();

        // Animation du sprite
        switch (mob.getName()) {
            case "Boss 1" -> animateBoss(iv, "/assets/image/boss1-1.png", "/assets/image/boss1-2.png", 0.3);
            case "Boss 2" -> animateBoss(iv,
                    "/assets/image/boss2-2.png",
                    "/assets/image/boss2-3.png",
                    "/assets/image/boss2-4.png", 0.4);
            case "Boss 3" -> animateBoss(iv,
                    "/assets/image/boss3-1.png",
                    "/assets/image/boss3-2.png",
                    "/assets/image/boss3-3.png",
                    "/assets/image/boss3-4.png", 0.2);
            case "Final boss" -> animateBoss(iv, "/assets/image/bossfinal-1.png", "/assets/image/bossfinal-2.png", 0.2);
            default -> animateMob(iv);
        }
    }

    private void animateMob(ImageView mobView) {
        Timeline mobAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.3), e -> mobView.setImage(mob1)),
                new KeyFrame(Duration.seconds(0.6), e -> mobView.setImage(mob2)),
                new KeyFrame(Duration.seconds(0.9), e -> mobView.setImage(mob3))
        );
        mobAnimation.setCycleCount(Animation.INDEFINITE);
        mobAnimation.play();
    }

    private void animateBoss(ImageView bossView, String path1, String path2, double speed) {
        Image img1 = new Image(Objects.requireNonNull(getClass().getResource(path1)).toExternalForm());
        Image img2 = new Image(Objects.requireNonNull(getClass().getResource(path2)).toExternalForm());

        Timeline anim = new Timeline(
                new KeyFrame(Duration.seconds(speed), e -> bossView.setImage(img1)),
                new KeyFrame(Duration.seconds(speed * 2), e -> bossView.setImage(img2))
        );
        anim.setCycleCount(Animation.INDEFINITE);
        anim.play();
    }

    private void animateBoss(ImageView bossView, String path1, String path2, String path3, double speed) {
        Image img1 = new Image(Objects.requireNonNull(getClass().getResource(path1)).toExternalForm());
        Image img2 = new Image(Objects.requireNonNull(getClass().getResource(path2)).toExternalForm());
        Image img3 = new Image(Objects.requireNonNull(getClass().getResource(path3)).toExternalForm());

        Timeline anim = new Timeline(
                new KeyFrame(Duration.seconds(speed), e -> bossView.setImage(img1)),
                new KeyFrame(Duration.seconds(speed * 2), e -> bossView.setImage(img2)),
                new KeyFrame(Duration.seconds(speed * 3), e -> bossView.setImage(img3))
        );
        anim.setCycleCount(Animation.INDEFINITE);
        anim.play();
    }

    private void animateBoss(ImageView bossView, String path1, String path2, String path3, String path4, double speed) {
        Image img1 = new Image(Objects.requireNonNull(getClass().getResource(path1)).toExternalForm());
        Image img2 = new Image(Objects.requireNonNull(getClass().getResource(path2)).toExternalForm());
        Image img3 = new Image(Objects.requireNonNull(getClass().getResource(path3)).toExternalForm());
        Image img4 = new Image(Objects.requireNonNull(getClass().getResource(path4)).toExternalForm());

        Timeline anim = new Timeline(
                new KeyFrame(Duration.seconds(speed), e -> bossView.setImage(img1)),
                new KeyFrame(Duration.seconds(speed * 2), e -> bossView.setImage(img2)),
                new KeyFrame(Duration.seconds(speed * 3), e -> bossView.setImage(img3)),
                new KeyFrame(Duration.seconds(speed * 4), e -> bossView.setImage(img4))
        );
        anim.setCycleCount(Animation.INDEFINITE);
        anim.play();
    }
}