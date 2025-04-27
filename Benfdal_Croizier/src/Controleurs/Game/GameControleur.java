package Controleurs.Game;

import Controleurs.Menu.MenuControleur;
import Modeles.game.GameModel;
import Modeles.settings.SettingsModel;
import Modeles.characters.Hero;
import Modeles.items.weapons.Weapon;
import Vues.Menu.SplashMenu;
import Vues.game.ChoiceView;
import Vues.game.GameView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.TransitionUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GameControleur {
    private final Stage stage;
    private GameView vue;
    private GameModel model = null;
    private final Map<String, KeyCode> keyBindings = new HashMap<>();
    private final Map<String, Boolean> directionsActives = new HashMap<>();
    private Timeline mouvementLoop;
    private String currentAnimationDirection = null;

    public GameControleur(Stage stage) {
        this.stage = stage;

        // Crée un Hero avant de créer GameModel
        Hero hero = new Hero("HeroName", Weapon.parseFromString("Sword"), model);  // Remplace "null" par un GameModel si nécessaire
        this.model = new GameModel(hero);  // Passe le Hero à GameModel

        this.vue = new GameView(model);

        // Chargement des touches personnalisées
        SettingsModel settings = SettingsModel.load();
        settings.getTouches().forEach((action, keyName) -> {
            try {
                keyBindings.put(action, KeyCode.valueOf(keyName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Touche invalide : " + keyName);
            }
        });

        // Initialisation des directions
        directionsActives.put("up", false);
        directionsActives.put("down", false);
        directionsActives.put("left", false);
        directionsActives.put("right", false);

        vue.setPlayerPosition(model.getPlayerX(), model.getPlayerY());

        vue.setOnKeyPressed(event -> {
            String dir = getDirectionFromKey(event.getCode());
            if (dir != null) {
                directionsActives.put(dir, true);
                if (mouvementLoop == null) startMouvementLoop();
            }
        });

        vue.setOnKeyReleased(event -> {
            String dir = getDirectionFromKey(event.getCode());
            if (dir != null) {
                directionsActives.put(dir, false);
                if (directionsActives.values().stream().noneMatch(b -> b)) {
                    stopMouvementLoop();
                }
            }
        });
    }

    private String getDirectionFromKey(KeyCode key) {
        if (key == keyBindings.get("moveUp")) return "up";
        if (key == keyBindings.get("moveDown")) return "down";
        if (key == keyBindings.get("moveLeft")) return "left";
        if (key == keyBindings.get("moveRight")) return "right";
        return null;
    }

    private void startMouvementLoop() {
        mouvementLoop = new Timeline(new KeyFrame(Duration.millis(40), e -> {
            double futurX = model.getPlayerX();
            double futurY = model.getPlayerY();
            double pas = 8;

            boolean up = directionsActives.get("up");
            boolean down = directionsActives.get("down");
            boolean left = directionsActives.get("left");
            boolean right = directionsActives.get("right");

            if (up) futurY -= pas;
            if (down) futurY += pas;
            if (left) futurX -= pas;
            if (right) futurX += pas;

            if (model.peutAller(futurX, futurY)) {
                model.setPlayerPosition(futurX, futurY);
                vue.setPlayerPosition(futurX, futurY);
            }

            // Gestion propre de l'animation
            String nouvelleDirection = null;
            if (up) nouvelleDirection = "up";
            else if (down) nouvelleDirection = "down";
            // Ajout future : else if (left) nouvelleDirection = "left"; etc.

            if (!sameDirection(nouvelleDirection, currentAnimationDirection)) {
                stopCurrentAnimation();
                if ("up".equals(nouvelleDirection)) vue.startWalkUpAnimation();
                else if ("down".equals(nouvelleDirection)) vue.startWalkDownAnimation();
                // Ajout future : else if ("left") { ... }
                currentAnimationDirection = nouvelleDirection;
            }

            // Vérification de la case bleue pour passer à l'étage suivant
            if (model.checkNextFloor()) {
                // Changer d'étage dans le modèle
                model.changerÉtage(model.getLocationActuelle().getFloorLevel() + 1, "Nouvelle zone !");

                // Afficher la vue de choix
                showChoiceView();
            }
        }));

        mouvementLoop.setCycleCount(Animation.INDEFINITE);
        mouvementLoop.play();
    }

    private void showChoiceView() {
        // Créer une instance de ChoiceView et lui passer le stage et le contrôleur
        ChoiceView choiceView = new ChoiceView(stage, this);  // "this" ici représente le GameControleur
        choiceView.showChoiceScene();  // Afficher la vue de choix
    }


    // Ajoute cette méthode dans ton GameController
    public void updateFloor() {
        // Mettre à jour l'étage dans le modèle
        model.changerÉtage(model.getLocationActuelle().getFloorLevel() + 1, "Nouvelle zone !");

        // Revenir à la vue du jeu après la mise à jour de l'étage
        vue.updateFloorLabel(); // Cette méthode pourrait mettre à jour l'affichage de l'étage dans la vue si nécessaire.

        // Changer de scène pour revenir à la scène principale (jeu)
        stage.setScene(new Scene(vue));  // Retourner à la scène du jeu
    }


    private void stopMouvementLoop() {
        if (mouvementLoop != null) {
            mouvementLoop.stop();
            mouvementLoop = null;
        }
        stopCurrentAnimation();
        currentAnimationDirection = null;
    }


    private boolean sameDirection(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    private void stopCurrentAnimation() {
        if ("up".equals(currentAnimationDirection)) vue.stopWalkUpAnimation();
        else if ("down".equals(currentAnimationDirection)) vue.stopWalkDownAnimation();
        // Ajout future : else if ("left") vue.stopWalkLeftAnimation(); etc.
    }

    public Parent getVue() {
        return vue;
    }

    public void passerÀLétageSuivant() {
        // Exemple de mise à jour de l'étage
        model.changerÉtage(model.getLocationActuelle().getFloorLevel() + 1, "Nouvelle zone !");
    }

    public void quitterJeu() {
        MenuControleur menuControleur = new MenuControleur(stage);
        SplashMenu menu = new SplashMenu(menuControleur);
        StackPane root = menuControleur.creerVueAvecFond(menu);
        TransitionUtils.fadeToScene(stage, root);
    }
}
