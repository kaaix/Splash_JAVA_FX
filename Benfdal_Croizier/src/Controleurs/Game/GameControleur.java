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
import javafx.geometry.Rectangle2D;
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
    private long dernierHit = 0; // temps en ms du dernier coup reçu


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

        vue.setOnMouseClicked(e -> {
            System.out.println("clic détecté : " + e.getButton()); // test brut
            attaqueHero();

        });


        updateFloor();
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
            String nouvelleDirection = null;
            if (up) nouvelleDirection = "up";
            else if (down) nouvelleDirection = "down";
            else if (left) nouvelleDirection = "left";
            else if (right) nouvelleDirection = "right";

            if (!sameDirection(nouvelleDirection, currentAnimationDirection)) {
                stopCurrentAnimation();
                if ("up".equals(nouvelleDirection))   vue.startWalkUpAnimation();
                else if ("down".equals(nouvelleDirection)) vue.startWalkDownAnimation();
                if (nouvelleDirection != null) currentAnimationDirection = nouvelleDirection; // ✅ garde la dernière connue
            }

            // Passage à l'étage suivant
            if (!isTransitioning && model.checkNextFloor() && model.getLocationActuelle().getEnemies().isEmpty()) {
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
        model.setPlayerPosition(13 * model.getTailleCase(), 13 * model.getTailleCase());
        Hero hero = model.getHero();
        hero.resetStats();
        hero.reapplyBonuses();
        vue.clearEnemies();

        List<Character> mobs = model.getLocationActuelle().getEnemies();
        int floorLevel = model.getLocationActuelle().getFloorLevel();

        if (floorLevel == 10 || floorLevel == 20 || floorLevel == 30) {
            isTransitioning = false;
            vue.setPlayerPosition(model.getPlayerX(), model.getPlayerY());
            vue.updateFloorLabel();
            vue.deverrouillerMap(); // ✅ Ajouté ici
            TransitionUtils.fadeToScene(stage, vue);
            if (enemyLoop != null) enemyLoop.stop();
            vue.deverrouillerMap();
            return;
        }


        // 👇 Ajoute ici tes coordonnées personnalisées
        Point2D[] coordsEtage1_9 = {
                new Point2D(850, 300) // Un seul ennemi
        };

        Point2D[] coordsEtage11_19 = {
                new Point2D(700, 300),
                new Point2D(900, 300)
        };

        Point2D[] coordsEtage21_29 = {
                new Point2D(700, 300),
                new Point2D(800, 300),
                new Point2D(900, 300) // ✅ 3 ennemis ici
        };

        Point2D[] coordsUtilisees;

        if (floorLevel >= 1 && floorLevel <= 9) {
            coordsUtilisees = coordsEtage1_9;
        } else if (floorLevel >= 11 && floorLevel <= 19) {
            coordsUtilisees = coordsEtage11_19;
        } else if (floorLevel >= 21 && floorLevel <= 29) {
            coordsUtilisees = coordsEtage21_29;
        } else {
            coordsUtilisees = new Point2D[] {
                    new Point2D(100, 700), new Point2D(200, 700), new Point2D(300, 700)
            };
        }

        for (int i = 0; i < mobs.size() && i < coordsUtilisees.length; i++) {
            Character mob = mobs.get(i);
            Point2D pos = coordsUtilisees[i];
            vue.addEnemy(mob, pos.getX(), pos.getY());
            enemyDirs.put(mob, directionAleatoire()); // ✅ maintenant direction aléatoire
        }

        if (!mobs.isEmpty()) {
            vue.verrouillerMap();
        } else {
            vue.deverrouillerMap();
        }

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
    }

    private Point2D directionAleatoire() {
        double angle = Math.random() * 2 * Math.PI; // angle entre 0 et 2π
        return new Point2D(Math.cos(angle), Math.sin(angle));
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
            for (Character mob : model.getLocationActuelle().getEnemies()) {
                if (mob.getHealth() <= 0) continue; // 💀 l'ennemi est mort, on ignore

                ImageView iv = vue.getEnemyView(mob);
                if (iv == null) continue;

                Point2D dir = enemyDirs.get(mob);
                double pas = mob.getSpeed() / 10.0;
                double nx = iv.getLayoutX() + dir.getX() * pas;
                double ny = iv.getLayoutY() + dir.getY() * pas;

                // Collision avec le joueur ?
                double playerX = model.getPlayerX();
                double playerY = model.getPlayerY();
                Point2D posJoueur = new Point2D(playerX, playerY);
                Point2D posEnnemi = new Point2D(nx, ny);
                double distance = posJoueur.distance(posEnnemi);

                if (distance < 50) {
                    long maintenant = System.currentTimeMillis();
                    if (maintenant - dernierHit >= 2000) {
                        model.getHero().takeDamage(10);
                        vue.updateHealth(model.getHero().getHealth(), 100);
                        vue.startImmunityBlink(); // ✨ clignotement !
                        dernierHit = maintenant;
                    }
                }

                boolean canX = model.peutAller(nx, iv.getLayoutY());
                boolean canY = model.peutAller(iv.getLayoutX(), ny);
                if (!canX && !canY) {
                    // Rebond contre deux murs : angle complètement aléatoire
                    dir = directionAleatoire();
                } else {
                    if (!canX) dir = new Point2D(-dir.getX(), dir.getY());
                    if (!canY) dir = new Point2D(dir.getX(), -dir.getY());
                }

                enemyDirs.put(mob, dir);

                if (model.peutAller(nx, ny)) {
                    iv.setLayoutX(nx);
                    iv.setLayoutY(ny);
                }
            }        }));
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

    private void attaqueHero() {
        double heroX = model.getPlayerX();
        double heroY = model.getPlayerY();
        double zoneLargeur = 100; // taille de la zone d’attaque
        double zoneHauteur = 80;

        // Déterminer la direction du héros
        double zoneX = 0, zoneY = 0;

        switch (currentAnimationDirection) {
            case "up":
                zoneX = heroX + 25;
                zoneY = heroY - zoneHauteur;
                break;
            case "down":
                zoneX = heroX + 25;
                zoneY = heroY + 150;
                break;
            case "left":
                zoneX = heroX - zoneLargeur;
                zoneY = heroY + 40; // au milieu du sprite
                break;
            case "right":
                zoneX = heroX + 150;
                zoneY = heroY + 40;
                break;
            default:
                return;
        }

        // Affiche la zone visuellement
        vue.afficherZoneAttaqueDirectionnelle(heroX, heroY, currentAnimationDirection);
        new Timeline(new KeyFrame(Duration.seconds(0.2), e -> vue.cacherZoneAttaque())).play();

        Rectangle2D zoneAttaque = new Rectangle2D(zoneX, zoneY, zoneLargeur, zoneHauteur);

        for (Character ennemi : model.getLocationActuelle().getEnemies()) {
            ImageView iv = vue.getEnemyView(ennemi);
            if (iv == null || ennemi.getHealth() <= 0) continue;

            double ennemiX = iv.getLayoutX() + 75;
            double ennemiY = iv.getLayoutY() + 75;

            if (zoneAttaque.contains(ennemiX, ennemiY)) {
                model.getHero().attack(ennemi);
                System.out.println("💥 Ennemi touché ! PV restants : " + ennemi.getHealth());

                if (ennemi.getHealth() <= 0) {
                    vue.getEnemyLayer().getChildren().remove(iv); // ✅ maintenant reconnu
                    model.getLocationActuelle().getEnemies().remove(ennemi); // modèle à jour
                    if (model.getLocationActuelle().getEnemies().isEmpty()) {
                        vue.deverrouillerMap(); // 🔓 ouvre la porte !
                    }
                    enemyDirs.remove(ennemi); // stop le mouvement
                    break; // évite ConcurrentModificationException
                }

                }
            }
        }
    }
