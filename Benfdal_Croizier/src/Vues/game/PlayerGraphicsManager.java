/**
 * Gère les sprites et les animations du joueur.
 * Charge les images directionnelles et crée les timelines
 * pour l’animation de marche vers le haut et vers le bas,
 * et positionne les images statiques pour les déplacements gauche/droite.
 */
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

    /**
     * Initialise le gestionnaire de graphismes du joueur.
     * Charge toutes les images de déplacement et prépare les animations.
     *
     * @param player l’ImageView du joueur à animer
     */
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

    /**
     * Démarre l’animation de marche vers le haut.
     * Arrête l’animation de marche vers le bas si elle était en cours.
     */
    public void startWalkUp() {
        walkDownAnimation.stop();
        walkUpAnimation.play();
    }

    /**
     * Arrête l’animation de marche vers le haut
     * et replace le sprite sur l’image statique de face vers le haut.
     */
    public void stopWalkUp() {
        walkUpAnimation.stop();
        player.setImage(playerUp1);
    }

    /**
     * Démarre l’animation de marche vers le bas.
     * Arrête l’animation de marche vers le haut si elle était en cours.
     */
    public void startWalkDown() {
        walkUpAnimation.stop();
        walkDownAnimation.play();
    }

    /**
     * Arrête l’animation de marche vers le bas
     * et replace le sprite sur l’image statique de face vers le bas.
     */
    public void stopWalkDown() {
        walkDownAnimation.stop();
        player.setImage(playerDown1);
    }

    /**
     * Positionne le sprite du joueur sur l’image statique face à gauche (pas d’animation).
     */
    public void startWalkLeft() {
        player.setImage(playerLeft);
    }

    /**
     * Positionne le sprite du joueur sur l’image statique face à droite (pas d’animation).
     */
    public void startWalkRight() {
        player.setImage(playerRight);
    }
}
