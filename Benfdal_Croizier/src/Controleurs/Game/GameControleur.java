// Controleurs/Game/GameControleur.java
package Controleurs.Game;

import Controleurs.Menu.MenuControleur;
import Modeles.characters.Character;
import Modeles.game.GameModel;
import Modeles.items.consumables.Consumable;
import Modeles.settings.SettingsModel;
import Modeles.characters.Hero;
import Modeles.items.weapons.Weapon;
import Vues.Menu.SplashMenu;
import Vues.game.ChoiceView;
import Vues.game.GameView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.TransitionUtils;

import java.util.HashMap;
import java.util.List;
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
    private boolean isTransitioning = false;
    // stocke pour chaque mob son vecteur (dx,dy) normalisé
    private final Map<Modeles.characters.Character, Point2D> enemyDirs = new HashMap<>();
    private Timeline enemyLoop;


    public GameControleur(Stage stage) {
        this.stage = stage;

        // Crée un Hero avant de créer GameModel
        this.model = new GameModel(); // Crée d'abord un modèle vide

        Hero hero = new Hero("HeroName", Weapon.parseFromString("Sword"), model); // Passe le modèle
        this.model.setHero(hero); // Associe le héros au modèle
        this.vue = new GameView(model); // Vue peut maintenant utiliser model sans null

        // Chargement des touches personnalisées
        SettingsModel.load().getTouches().forEach((action, keyName) -> {
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

        // Gestion clavier pour le mouvement
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
        if (key == keyBindings.get("moveUp"))    return "up";
        if (key == keyBindings.get("moveDown"))  return "down";
        if (key == keyBindings.get("moveLeft"))  return "left";
        if (key == keyBindings.get("moveRight")) return "right";
        return null;
    }

    public Parent getVue() {
        return vue;
    }

    public void updateSelection(int selectedDoor) {
        System.out.println("🚪 Porte sélectionnée : " + selectedDoor);
    }

    private void startMouvementLoop() {
        mouvementLoop = new Timeline(new KeyFrame(Duration.millis(40), e -> {
            double futurX = model.getPlayerX();
            double futurY = model.getPlayerY();
            double pas = model.getHero().getSpeed() / 10.0; // ou / 8.0, à ajuster selon l’équilibre

            boolean up    = directionsActives.get("up");
            boolean down  = directionsActives.get("down");
            boolean left  = directionsActives.get("left");
            boolean right = directionsActives.get("right");

            if (up)    futurY -= pas;
            if (down)  futurY += pas;
            if (left)  futurX -= pas;
            if (right) futurX += pas;

            if (model.peutAller(futurX, futurY)) {
                model.setPlayerPosition(futurX, futurY);
                vue.setPlayerPosition(futurX, futurY);
            }

            // Animation en fonction de la direction
            String nouvelleDirection = up ? "up" : down ? "down" : null;
            if (!sameDirection(nouvelleDirection, currentAnimationDirection)) {
                stopCurrentAnimation();
                if ("up".equals(nouvelleDirection))   vue.startWalkUpAnimation();
                else if ("down".equals(nouvelleDirection)) vue.startWalkDownAnimation();
                currentAnimationDirection = nouvelleDirection;
            }

            // Passage à l'étage suivant
            if (!isTransitioning && model.checkNextFloor()) {
                isTransitioning = true; // bloque les prochaines détections
                model.changerEtage(model.getLocationActuelle().getFloorLevel() + 1, "Nouvelle zone !");
                showChoiceView();
            }

        }));
        mouvementLoop.setCycleCount(Animation.INDEFINITE);
        mouvementLoop.play();
    }

    private void showChoiceView() {
        ChoiceView choiceView = new ChoiceView(stage, this);
        TransitionUtils.fadeToScene(stage, choiceView.getVue());
    }

    public void updateFloor() {
        // 1) Repositionnement et réinitialisation du héros
        model.setPlayerPosition(13 * model.getTailleCase(),
                13 * model.getTailleCase());
        Hero hero = model.getHero();
        hero.resetStats();
        hero.reapplyBonuses();

        vue.clearEnemies();

        // 2) Récupération des mobs et infos de la grille
        List<Character> mobs = model.getLocationActuelle().getEnemies();
        int count = mobs.size();
        int floorLevel = model.getLocationActuelle().getFloorLevel();
        double mapW = vue.getWidth(), mapH = vue.getHeight();

        // 3) Paramètres du pattern « cercle »
        double centerX = mapW  / 2.0;
        double centerY = mapH  / 2.0;
        // Rayon = 20% de la plus petite dimension + 5% tous les 5 étages
        double baseRadius = Math.min(mapW, mapH) * 0.2;
        double extra      = Math.min(mapW, mapH) * 0.05 * ((floorLevel-1)/5.0);
        double radius     = baseRadius + extra;

        // 4) Placement : on répartit les ennemis sur le cercle
        for (int i = 0; i < count; i++) {
            Modeles.characters.Character mob = mobs.get(i);
            double angle = 2*Math.PI*i/count;
            double x = centerX + Math.cos(angle)*radius;
            double y = centerY + Math.sin(angle)*radius;
            vue.addEnemy(mob, x, y);
            enemyDirs.put(mob, new Point2D(Math.cos(angle), Math.sin(angle)));
        }

        // 5) Réaffichage / transition
        isTransitioning = false;
        vue.setPlayerPosition(model.getPlayerX(), model.getPlayerY());
        vue.updateFloorLabel();
        TransitionUtils.fadeToScene(stage, vue);

        if (enemyLoop != null) enemyLoop.stop();
        startEnemyLoop();
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
        if ("up".equals(currentAnimationDirection))   vue.stopWalkUpAnimation();
        else if ("down".equals(currentAnimationDirection)) vue.stopWalkDownAnimation();
    }

    private void startEnemyLoop() {
        enemyLoop = new Timeline(new KeyFrame(Duration.millis(40), ev -> {
            double mapW = vue.getWidth(), mapH = vue.getHeight();
            for (Modeles.characters.Character mob : model.getLocationActuelle().getEnemies()) {
                ImageView iv = vue.getEnemyView(mob);
                Point2D dir   = enemyDirs.get(mob);

                // même formule que pour le joueur :
                double pas = mob.getSpeed() / 10.0;

                // nouveau candidat de position
                double nx = iv.getLayoutX() + dir.getX() * pas;
                double ny = iv.getLayoutY() + dir.getY() * pas;

                // collision avec la carte : on teste murs et
                // on inverse la composante X et/ou Y en cas de mur
                boolean canX = model.peutAller(nx, iv.getLayoutY());
                boolean canY = model.peutAller(iv.getLayoutX(), ny);
                if (!canX) dir = new Point2D(-dir.getX(), dir.getY());
                if (!canY) dir = new Point2D(dir.getX(), -dir.getY());
                enemyDirs.put(mob, dir);

                // applique le déplacement *seulement* si la case à droite / bas est libre
                if (model.peutAller(nx, ny)) {
                    iv.setLayoutX(nx);
                    iv.setLayoutY(ny);
                }
            }
        }));
        enemyLoop.setCycleCount(Animation.INDEFINITE);
        enemyLoop.play();
    }




    public void quitterJeu() {
        MenuControleur mc = new MenuControleur(stage);
        mc.jouerMusiqueMenu();
        SplashMenu menu = new SplashMenu(mc);
        mc.creerVueAvecFond(menu);
        TransitionUtils.fadeToScene(stage, mc.creerVueAvecFond(menu));
    }

    public GameModel getModel() {
        return model;
    }


}
