/**
 * Affiche la vue de victoire avec un message personnalisé
 * et le score du joueur, puis effectue un fondu de transition.
 *
 * @param playerName   nom du joueur victorieux
 * @param scoreSeconds score réalisé en secondes
 */
package Controleurs.Game;

import Controleurs.Menu.MenuControleur;
import Modeles.characters.Character;
import Modeles.game.GameModel;
import Modeles.items.consumables.Consumable;
import Modeles.save.SaveData;
import Modeles.settings.SettingsModel;
import Modeles.characters.Hero;
import Modeles.items.weapons.Weapon;
import Vues.Menu.SplashMenu;
import Vues.game.ChoiceView;
import Vues.game.GameView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.I18N;
import utils.MusicPlayer;
import utils.SoundEffects;
import utils.TransitionUtils;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import Modeles.items.Item;


import java.util.*;
import java.util.stream.Collectors;

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
    private boolean isGameOver = false;
    private boolean partieChargee = false;
    private String nomFichierSauvegarde = "save1.bin"; // valeur par défaut
    private String nomHero;
    private String nomArme;
    private long dernierCoupHero = 0;
    private ChoiceControleur choiceController;

    /**
     * Crée un nouveau contrôleur de jeu.
     * Charge les bindings clavier depuis les paramètres,
     * initialise le modèle et la vue de jeu,
     * et met en place les listeners pour le joueur et les ennemis.
     *
     * @param stage la fenêtre JavaFX principale (Stage)
     */
    public GameControleur(Stage stage) {
        this.stage = stage;

        // Crée un Hero avant de créer GameModel
        this.model = new GameModel(); // Crée d'abord un modèle vide
        this.vue = new GameView(model); // Vue peut maintenant utiliser model sans null
        this.vue.setControleur(this); // 👈 lie le contrôleur à la vue

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

        vue.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (vue.isPauseMenuVisible()) {
                    vue.hidePauseMenu();
                } else {
                    vue.showPauseMenu();
                }
                return;
            }

            if (vue.isPauseMenuVisible()) return;

            String dir = getDirectionFromKey(event.getCode());
            if (dir != null) {
                directionsActives.put(dir, true);
                if (mouvementLoop == null) startMouvementLoop();
            }
        });


        model.demarrerChrono();

    }


    private String getDirectionFromKey(KeyCode key) {
        if (key == keyBindings.get("moveUp")) return "up";
        if (key == keyBindings.get("moveDown")) return "down";
        if (key == keyBindings.get("moveLeft")) return "left";
        if (key == keyBindings.get("moveRight")) return "right";
        return null;
    }

    /**
     * Retourne le nœud JavaFX à insérer dans la scène pour afficher le jeu.
     *
     * @return le Parent racine de la vue de jeu
     */
    public Parent getVue() {
        return vue;
    }

    /**
     * Notifie la vue du changement de porte sélectionnée
     * lors de la transition vers le choix de bonus.
     *
     * @param selectedDoor indice de la porte sélectionnée (0 à 2)
     */
    public void updateSelection(int selectedDoor) {
        System.out.println("🚪 Porte sélectionnée : " + selectedDoor);
    }


    private void startMouvementLoop() {
        mouvementLoop = new Timeline(new KeyFrame(Duration.millis(40), e -> {
            double futurX = model.getPlayerX();
            double futurY = model.getPlayerY();
            double pas = model.getHero().getSpeed() / 10.0; // ou / 8.0, à ajuster selon l’équilibre

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

            // Animation en fonction de la direction
            String nouvelleDirection = null;
            if (up) nouvelleDirection = "up";
            else if (down) nouvelleDirection = "down";
            else if (left) nouvelleDirection = "left";
            else if (right) nouvelleDirection = "right";

            if (!sameDirection(nouvelleDirection, currentAnimationDirection)) {
                stopCurrentAnimation();
                currentAnimationDirection = nouvelleDirection;
            }

            // Relance toujours l'animation courante si une direction est active
            if ("up".equals(currentAnimationDirection)) vue.getPlayerGraphics().startWalkUp();
            else if ("down".equals(currentAnimationDirection)) vue.getPlayerGraphics().startWalkDown();
            else if ("left".equals(currentAnimationDirection)) vue.getPlayerGraphics().startWalkLeft();
            else if ("right".equals(currentAnimationDirection)) vue.getPlayerGraphics().startWalkRight();


            if (isGameOver) return; // Empêche la transition post-mort

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

    /**
     * Recharge et applique les préférences utilisateur :
     * langue, volumes audio et mode plein écran.
     * Met à jour également l’affichage (étage, raccourcis).
     */
    public void updateSettings() {
        SettingsModel settings = SettingsModel.load();

        // Langue
        I18N.setLangue(settings.getLangue());

        // Volume
        MusicPlayer.setVolume(settings.getMusicVolume() / 100.0);
        SoundEffects.setVolume(settings.getSfxVolume() / 100.0);

        vue.updateFloorLabel();
        reloadKeyBindings();

        // Plein écran
        stage.setFullScreenExitHint("");
        stage.setFullScreen(settings.isFullscreen());
    }

    /**
     * Passe à la vue de sélection de bonus après avoir atteint
     * la porte, en initialisant les choix et en animant la transition.
     */
    private void showChoiceView() {
        choiceController = new ChoiceControleur(this);         // 👈 création
        choiceController.initializeBonusChoices();              // 👈 logique métier
        ChoiceView choiceView = new ChoiceView(stage, choiceController); // 👈 passe le contrôleur à la vue
        TransitionUtils.fadeToScene(stage, choiceView.getVue());
    }

    /**
     * Met à jour la position du joueur, recharge la carte
     * et place graphiquement les ennemis pour le nouvel étage.
     */
    public void updateFloor() {
        model.setPlayerPosition(13 * model.getTailleCase(), 13 * model.getTailleCase());
        Hero hero = model.getHero();
        if (!partieChargee) {
            hero.refreshStats();
        }

        vue.clearEnemies();

        List<Character> mobs = model.getLocationActuelle().getEnemies();
        int floorLevel = model.getLocationActuelle().getFloorLevel();


        // Coordonnées spécifiques
        Point2D[] coordsEtage1_9 = {new Point2D(850, 300)};
        Point2D[] coordsEtage11_19 = {
                new Point2D(700, 300),
                new Point2D(900, 300)
        };
        Point2D[] coordsEtage21_29 = {
                new Point2D(700, 300),
                new Point2D(800, 300),
                new Point2D(900, 300)
        };
        Point2D bossCoord = new Point2D(850, 300); // Coord unique boss

        Point2D[] coordsUtilisees;
        if (floorLevel >= 1 && floorLevel <= 9) {
            coordsUtilisees = coordsEtage1_9;
        } else if (floorLevel >= 11 && floorLevel <= 19) {
            coordsUtilisees = coordsEtage11_19;
        } else if (floorLevel >= 21 && floorLevel <= 29) {
            coordsUtilisees = coordsEtage21_29;
        } else {
            coordsUtilisees = new Point2D[]{
                    new Point2D(100, 700), new Point2D(200, 700), new Point2D(300, 700)
            };
        }

        // Placement des ennemis
        for (int i = 0; i < mobs.size(); i++) {
            Character mob = mobs.get(i);
            Point2D pos;

            if (floorLevel == 10 || floorLevel == 20 || floorLevel == 30) {
                pos = bossCoord; // boss → coord unique
            } else if (i < coordsUtilisees.length) {
                pos = coordsUtilisees[i];
            } else {
                pos = new Point2D(100 + i * 100, 700); // fallback
            }

            vue.addEnemy(mob, pos.getX(), pos.getY());
            enemyDirs.put(mob, directionAleatoire());
        }

        // Affichage + animation
        isTransitioning = false;
        vue.setPlayerPosition(model.getPlayerX(), model.getPlayerY());
        vue.updateFloorLabel();
        vue.setMapBackgroundForFloor(floorLevel);
        TransitionUtils.fadeToScene(stage, vue);
        Platform.runLater(() -> {
            vue.setMapBackgroundForFloor(floorLevel);
            System.out.println("✅ Map mise à jour pour l'étage " + floorLevel);
        });

        // Gère la map lock
        if (!mobs.isEmpty()) {
            vue.verrouillerMap();
        } else {
            vue.deverrouillerMap();
        }

        if (enemyLoop != null) enemyLoop.stop();
        startEnemyLoop();

        System.out.println("🔫 Arme actuelle : " + model.getHero().getWeapon().getDisplayName());


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
        return Objects.equals(a, b);
    }

    private void stopCurrentAnimation() {
        if ("up".equals(currentAnimationDirection)) vue.getPlayerGraphics().stopWalkUp();
        else if ("down".equals(currentAnimationDirection)) vue.getPlayerGraphics().stopWalkDown();
        // gauche/droite : pas besoin de stopper, image statique déjà posée
    }


    private void startEnemyLoop() {
        enemyLoop = new Timeline(new KeyFrame(Duration.millis(40), ev -> {
            for (Character mob : model.getLocationActuelle().getEnemies()) {
                if (mob.getHealth() <= 0) continue;

                Pane enemyPane = vue.getEnemyView(mob);
                if (enemyPane == null) continue;

                Point2D dir = enemyDirs.get(mob);
                double pas = mob.getSpeed() / 10.0;
                double nx = enemyPane.getLayoutX() + dir.getX() * pas;
                double ny = enemyPane.getLayoutY() + dir.getY() * pas;

                Point2D posJoueur = new Point2D(model.getPlayerX(), model.getPlayerY());
                Point2D posEnnemi = new Point2D(nx, ny);
                if (posJoueur.distance(posEnnemi) < 50) {
                    long maintenant = System.currentTimeMillis();
                    if (maintenant - dernierHit >= 2000) {
                        model.getHero().takeDamage(mob.getAttackPower());
                        vue.updateHealth(model.getHero().getHealth(), model.getHero().getMaxHealth());
                        vue.startImmunityBlink();
                        dernierHit = maintenant;
                    }
                }

                boolean canX = model.peutAller(nx, enemyPane.getLayoutY());
                boolean canY = model.peutAller(enemyPane.getLayoutX(), ny);
                if (!canX && !canY) dir = directionAleatoire();
                else {
                    if (!canX) dir = new Point2D(-dir.getX(), dir.getY());
                    if (!canY) dir = new Point2D(dir.getX(), -dir.getY());
                }
                enemyDirs.put(mob, dir);

                if (model.peutAller(nx, ny)) {
                    enemyPane.setLayoutX(nx);
                    enemyPane.setLayoutY(ny);
                }
            }

            if (model.getHero().getHealth() <= 0 && !isGameOver) {
                isGameOver = true;
                stopMouvementLoop();
                if (enemyLoop != null) enemyLoop.stop();
                EndGameControleur end = new EndGameControleur(stage);
                end.showGameOver();
            }
        }));
        enemyLoop.setCycleCount(Animation.INDEFINITE);
        enemyLoop.play();
    }

    /**
     * Recharge les raccourcis clavier depuis les paramètres
     * et met à jour la map interne des bindings.
     */
    public void reloadKeyBindings() {
        keyBindings.clear();
        SettingsModel.load().getTouches().forEach((action, keyName) -> {
            try {
                keyBindings.put(action, KeyCode.valueOf(keyName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Touche invalide : " + keyName);
            }
        });
        System.out.println("✅ Raccourcis clavier rechargés !");
    }

    /**
     * Interrompt la partie en cours, rejoue la musique de menu
     * et revient au SplashMenu avec une transition.
     */
    public void quitterJeu() {
        MenuControleur mc = new MenuControleur(stage);
        mc.jouerMusiqueMenu();
        SplashMenu menu = new SplashMenu(mc);
        mc.creerVueAvecFond(menu);
        TransitionUtils.fadeToScene(stage, mc.creerVueAvecFond(menu));
    }

    /**
     * Retourne le modèle de la partie, contenant état du niveau,
     * position du joueur et liste des ennemis.
     *
     * @return l’instance de GameModel utilisée
     */
    public GameModel getModel() {
        return model;
    }


    private void attaqueHero() {
        double heroX = model.getPlayerX();
        double heroY = model.getPlayerY();
        double zoneLargeur = 100;
        double zoneHauteur = model.getHero().getWeapon().getPortee();


        double cooldown = model.getHero().getWeapon().getFireRate(); // coups par seconde
        long delaiMs = (long) (1000 / cooldown);
        long maintenant = System.currentTimeMillis();

        if (maintenant - dernierCoupHero < delaiMs) return; // trop tôt
        dernierCoupHero = maintenant;

        vue.afficherCooldown(delaiMs / 1000.0); // en secondes

        double zoneX = 0, zoneY = 0;

        switch (currentAnimationDirection) {
            case "up" -> {
                zoneLargeur = 100;
                zoneHauteur = model.getHero().getWeapon().getPortee();
                zoneX = heroX + 25;
                zoneY = heroY - zoneHauteur;
            }
            case "down" -> {
                zoneLargeur = 100;
                zoneHauteur = model.getHero().getWeapon().getPortee();
                zoneX = heroX + 25;
                zoneY = heroY + 150;
            }
            case "left" -> {
                zoneLargeur = model.getHero().getWeapon().getPortee();
                zoneHauteur = 80;
                zoneX = heroX - zoneLargeur;
                zoneY = heroY + 40;
            }
            case "right" -> {
                zoneLargeur = model.getHero().getWeapon().getPortee();
                zoneHauteur = 80;
                zoneX = heroX + 150;
                zoneY = heroY + 40;
            }
            default -> {
                return;
            }
        }

        vue.afficherZoneAttaqueDirectionnelle(heroX, heroY, currentAnimationDirection);
        new Timeline(new KeyFrame(Duration.seconds(0.2), e -> vue.cacherZoneAttaque())).play();

        Rectangle2D zoneAttaque = new Rectangle2D(zoneX, zoneY, zoneLargeur, zoneHauteur);

        for (Character ennemi : model.getLocationActuelle().getEnemies()) {
            Pane enemyPane = vue.getEnemyView(ennemi);
            if (enemyPane == null) continue;

            ImageView iv = (ImageView) enemyPane.getChildren().getFirst(); // ✅ plus safe maintenant

            double ennemiX = enemyPane.getLayoutX() + iv.getLayoutX() + iv.getFitWidth() / 2;
            double ennemiY = enemyPane.getLayoutY() + iv.getLayoutY() + iv.getFitHeight() / 2;



            if (zoneAttaque.contains(ennemiX, ennemiY)) {
                model.getHero().attack(ennemi);
                System.out.println("💥 Ennemi touché ! PV restants : " + ennemi.getHealth());
                System.out.println("P: " + ennemi.getHealth() + "/" + ennemi.getMaxHealth());

                if (ennemi.getHealth() <= 0) {
                    vue.getEnemyLayer().getChildren().remove(enemyPane);
                    model.getLocationActuelle().getEnemies().remove(ennemi);
                    enemyDirs.remove(ennemi);

                    vue.getEnemyView(ennemi); // pour confirmer
                    vue.removeEnemyView(ennemi); // ✅ propre et encapsulé

                    // Fin de combat
                    if (model.getLocationActuelle().getEnemies().isEmpty()) {
                        vue.deverrouillerMap();

                        // Fin du jeu si le boss final est vaincu
                        if (ennemi.getName().equals("Final boss") && !isGameOver) {
                            isGameOver = true;
                            EndGameControleur end = new EndGameControleur(stage);
                            String playerName = model.getHero().getName();
                            int temps = model.getTempsEnSecondes();
                            end.showVictory(playerName, temps);
                            return;
                        }

                    }

                    break; // éviter ConcurrentModificationException
                }
            }
        }

    }

    /**
     * Sauvegarde l’état actuel du jeu (héros, inventaire, chrono)
     * dans le fichier spécifié.
     *
     * @param nomFichier chemin où écrire la sauvegarde
     */
    public void sauvegarderPartie(String nomFichier) {
        try {
            Hero hero = model.getHero();

            String weaponName = (hero.getWeapon() != null)
                    ? hero.getWeapon().getDisplayName()
                    : "Shooter"; // Valeur par défaut

            List<String> bagContent = hero.getBag().getAllItemIds();


            int timeElapsed = model.getTempsEnSecondes(); // Assure-toi que cette méthode existe !

            SaveData data = new SaveData(
                    model.getLocationActuelle().getFloorLevel(),
                    model.getPlayerX(),
                    model.getPlayerY(),
                    hero.getHealth(),
                    weaponName,
                    bagContent,
                    timeElapsed,
                    hero.getName(),
                    hero.getMaxHealth() // ✅ nouveau paramètre
            );

            FileOutputStream fileOut = new FileOutputStream(nomFichier);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            out.close();
            fileOut.close();

            System.out.println("📤 Sac sauvegardé : " + bagContent);

            System.out.println("✅ Sauvegarde effectuée dans " + nomFichier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge une partie depuis un objet SaveData.
     * Reconstruit le héros, l’inventaire, le chrono et les ennemis,
     * puis met à jour la vue et le modèle.
     *
     * @param data objet de sauvegarde contenant tous les états
     */
    public void chargerDepuisSave(SaveData data) {
        this.partieChargee = true;

        System.out.println("📦 Sac restauré : " + data.bagItems);


        // Crée le héros avec l'arme et le nom
        Weapon weapon = Weapon.parseFromString(data.weapon);
        Hero hero = new Hero(data.nomHero, weapon, model);
        model.setHero(hero);             // important : set dans le modèle AVANT reset

        // Appliquer les consommables d'abord (car ils peuvent augmenter maxHealth)
        for (String itemName : data.bagItems) {
            Consumable c = Consumable.parseFromName(itemName);
            if (c != null) {
                c.setAlreadyApplied(false);
                hero.addConsumable(c);
            }
        }


        hero.refreshStats();

        hero.setMaxHealth(data.maxHp);   // met le max après les bonus
        hero.setHealth(data.currentHp);  // ajuste la vie actuelle

        vue.updateStatsLabel(); // bon moment ici


        // Position et étage
        model.changerEtage(data.floorLevel, "Chargement...");
        model.setPlayerPosition(data.playerX, data.playerY);
        model.setChronoOffset(data.timeSeconds);
        vue.setPlayerPosition(data.playerX, data.playerY);

        // Reset des mobs
        enemyDirs.clear();
        updateFloor();

        this.partieChargee = false;
    }

    /**
     * Définit le nom de fichier à utiliser pour la prochaine sauvegarde.
     *
     * @param nom chemin du fichier de sauvegarde
     */
    public void setNomFichierSauvegarde(String nom) {
        this.nomFichierSauvegarde = nom;
    }

    /**
     * Retourne le nom de fichier actuellement configuré
     * pour la sauvegarde.
     *
     * @return le chemin du fichier de sauvegarde
     */
    public String getNomFichierSauvegarde() {
        return nomFichierSauvegarde;
    }

    /**
     * Définit le nom du héros pour la partie en cours.
     *
     * @param nomHero le nom choisi par l’utilisateur
     */
    public void setNomHero(String nomHero) {
        this.nomHero = nomHero;
    }

    /**
     * Configure l’arme du héros avant le démarrage de la partie.
     *
     * @param nomArme identifiant/nom de l’arme sélectionnée
     */
    public void setNomArme(String nomArme) {
        this.nomArme = nomArme;
    }

    /**
     * Crée et place le héros dans le modèle avec nom et arme réglés,
     * puis lance la mise à jour de l’étage et de la position initiale.
     */
    public void initialiserHero() {
        Weapon weapon = nomArme != null ? Weapon.parseFromString(nomArme) : Weapon.parseFromString("Shooter");
        String nom = nomHero != null ? nomHero : "HeroName";
        Hero hero = new Hero(nom, weapon, model);
        this.model.setHero(hero);
        vue.setPlayerPosition(model.getPlayerX(), model.getPlayerY());

        updateFloor();
    }
}