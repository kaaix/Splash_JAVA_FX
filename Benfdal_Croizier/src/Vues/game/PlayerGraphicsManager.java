package Vues.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Objects;

public class PlayerGraphicsManager {
    private final Image playerUp1;
    private final Image playerUp2;
    private final Image playerDown1;
    private final Image playerDown2;
    private final Image playerLeft;
    private final Image playerRight;

    private final ImageView player;
    private final Timeline walkUpAnimation;
    private final Timeline walkDownAnimation;

    public PlayerGraphicsManager(ImageView player) {
        this.player = player;

        this.playerUp1 = load("/assets/image/player-up1.png");
        this.playerUp2 = load("/assets/image/player-up2.png");
        this.playerDown1 = load("/assets/image/player-down1.png");
        this.playerDown2 = load("/assets/image/player-down2.png");
        this.playerLeft = load("/assets/image/player-left1.png");
        this.playerRight = load("/assets/image/player-right1.png");

        this.walkUpAnimation = createAnimation(playerUp1, playerUp2);
        this.walkDownAnimation = createAnimation(playerDown1, playerDown2);
    }

    private Image load(String path) {
        return new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
    }

    private Timeline createAnimation(Image img1, Image img2) {
        Timeline anim = new Timeline(
                new KeyFrame(Duration.seconds(0.3), e -> player.setImage(img1)),
                new KeyFrame(Duration.seconds(0.6), e -> player.setImage(img2))
        );
        anim.setCycleCount(Timeline.INDEFINITE);
        return anim;
    }

    public void startWalkUp() {
        walkDownAnimation.stop();
        walkUpAnimation.play();
    }

    public void stopWalkUp() {
        walkUpAnimation.stop();
        player.setImage(playerUp1);
    }

    public void startWalkDown() {
        walkUpAnimation.stop();
        walkDownAnimation.play();
    }

    public void stopWalkDown() {
        walkDownAnimation.stop();
        player.setImage(playerDown1);
    }

    public void startWalkLeft() {
        player.setImage(playerLeft);
    }

    public void startWalkRight() {
        player.setImage(playerRight);
    }
}
