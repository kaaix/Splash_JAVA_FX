/**
 * Vue principale du jeu. Affiche la carte, le héros, les ennemis,
 * les barres de vie et statistiques du héros, l’étage et la difficulté.
 * Gère aussi le menu pause et la mise à jour des entités.
 */
package Vues.game;

import Controleurs.Game.GameControleur;
import Controleurs.Menu.MenuControleur;
import Controleurs.Menu.SettingsControleur;
import Modeles.characters.Character;
import Modeles.game.GameModel;
import Modeles.characters.Hero;
import Modeles.items.weapons.Weapon;
import Vues.Menu.SettingsView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.I18N;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameView extends StackPane {
    private GameModel model;
    private ImageView player;
    private final ImageView mapView;
    private final int nbCols = 30;
    private final int nbRows = 17;
    private final int taille = 64;
    private Rectangle hitbox;

    // Déclaration de la barre de vie
    private Rectangle hpBarFill;  // Barre rouge pour les points de vie
    private Label hpText;
    private Label speedText;
    private Label attackText;
    private Label critText;

    // Déclaration du label pour afficher l'étage et la difficulté
    private Label labelEtage;
    private final Pane enemyLayer = new Pane();
    private final Map<Character, Pane> enemyViews = new HashMap<>();
    private Rectangle attackZone;
    private ImageView mapLockView;
    private ImageView mapViewBoss;
    private GameControleur controleur;
    private boolean isPauseMenuVisible = false;
    private StackPane pauseOverlay;
    private Rectangle cooldownBar;
    private PlayerGraphicsManager playerGraphics;
    private EnemyGraphicsManager enemyGraphicsManager;

    /**
     * Construit la vue de jeu principale.
     *
     * @param model le GameModel contenant l’état du niveau, du héros et des ennemis
     */
    public GameView(GameModel model) {
        this.model = model; // ✅ affecte le modèle !
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        this.setPrefSize(1280, 720);  // 👈 fixe la taille virtuelle
        this.setMinSize(1280, 720);

        // Charger l'image de la carte
        Image mapImage = new Image(Objects.requireNonNull(getClass().getResource("/assets/image/map.png")).toExternalForm());
        mapView = new ImageView(mapImage);
        mapView.setId("mapView"); // pour debug si besoin
        mapView.setPreserveRatio(false);
        mapView.setSmooth(false);
        mapView.fitHeightProperty().bind(heightProperty());
        mapView.fitWidthProperty().bind(widthProperty());

        Image mapLockImage = new Image(Objects.requireNonNull(getClass().getResource("/assets/image/maplock.png")).toExternalForm());
        mapLockView = new ImageView(mapLockImage);
        mapView.setPreserveRatio(false);
        mapView.setSmooth(false);
        mapView.fitHeightProperty().bind(heightProperty());
        mapView.fitWidthProperty().bind(widthProperty());
        mapLockView.setVisible(false);       // caché par défaut

        Image bossMap = new Image(Objects.requireNonNull(getClass().getResource("/assets/image/mapfinal.png")).toExternalForm());
        mapViewBoss = new ImageView(bossMap);
        mapViewBoss.setPreserveRatio(false);
        mapViewBoss.setSmooth(false);
        mapViewBoss.fitHeightProperty().bind(heightProperty());
        mapViewBoss.fitWidthProperty().bind(widthProperty());
        mapViewBoss.setVisible(false); // caché par défaut

        enemyGraphicsManager = new EnemyGraphicsManager(enemyLayer, enemyViews);

        cooldownBar = new Rectangle(100, 8); // largeur max = 100
        cooldownBar.setFill(Color.ORANGE);
        cooldownBar.setArcWidth(5);
        cooldownBar.setArcHeight(5);
        cooldownBar.setVisible(false);
        cooldownBar.setLayoutX(590); // position vers le centre
        cooldownBar.setLayoutY(680); // tout en bas


        // Image du joueur
        Image playerImage = new Image(Objects.requireNonNull(getClass().getResource("/assets/image/player.png")).toExternalForm());
        player = new ImageView(playerImage);
        playerGraphics = new PlayerGraphicsManager(player);
        player.setFitWidth(150);
        player.setFitHeight(150);
        player.setPreserveRatio(true);

        // Création du label pour l'étage et la difficulté
        labelEtage = new Label();
        labelEtage.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        labelEtage.setTextFill(Color.WHITE);

        // Lier le texte du label à l'étage et à la difficulté
        labelEtage.setText(
                I18N.get("gameview.floor") + " " + model.getLocationActuelle().getFloorLevel() +
                        " - " + I18N.get("gameview.difficulty") + " : " + model.getLocationActuelle().getDifficulty()
        );


        // Création d'un Pane pour contenir le label et le positionner en haut à droite
        StackPane labelContainer = new StackPane();
        labelContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);  // Largeur automatique
        labelContainer.setPrefHeight(Region.USE_COMPUTED_SIZE); // Hauteur automatique
        labelContainer.setTranslateX(-10);  // Décalage de 10 pixels à gauche (ajuster si nécessaire)
        labelContainer.setTranslateY(10);   // Décalage de 10 pixels du haut
        labelContainer.getChildren().add(labelEtage);

        // Définir la mise en page dans un conteneur supérieur
        StackPane.setAlignment(labelEtage, javafx.geometry.Pos.TOP_RIGHT);  // Alignement en haut à droite

        // Définition de la hitbox
        hitbox = new Rectangle(64, 64);
        hitbox.setVisible(false);
//        hitbox.setStroke(Color.LIMEGREEN);
//        hitbox.setFill(Color.color(0, 1, 0, 0.2));

        // Création de la barre de vie
        hpBarFill = new Rectangle(200, 20);  // La barre rouge pour les HP
        hpBarFill.setFill(Color.RED);  // La barre rouge
        hpBarFill.setLayoutX(10);  // Positionner à gauche
        hpBarFill.setLayoutY(10);  // Positionner en haut

        // Text pour les HP
        hpText = new Label("HP: 100");
        hpText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        hpText.setTextFill(Color.WHITE);
        hpText.setLayoutX(220);  // Positionner à droite de la barre

        speedText = new Label();
        attackText = new Label();
        critText = new Label();

        speedText.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        speedText.setTextFill(Color.WHITE);

        attackText.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        attackText.setTextFill(Color.WHITE);

        critText.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        critText.setTextFill(Color.WHITE);


        // Créer une couche pour la barre de vie et le texte
        HBox hpBar = new HBox(5, hpBarFill, hpText);

        VBox statsBox = new VBox(3, hpBar, speedText, attackText, critText);
        statsBox.setLayoutX(10);
        statsBox.setLayoutY(10);


        // Création des couches pour la carte, le joueur, et la grille
        Pane playerLayer = new Pane();

        attackZone = new Rectangle(100, 80); // largeur, hauteur de la zone de frappe
        attackZone.setFill(Color.color(1, 0, 0, 0.3)); // rouge transparent
        attackZone.setVisible(false);
        playerLayer.getChildren().add(attackZone);

        playerLayer.getChildren().addAll(player, hitbox);

        Pane grilleLayer = new Pane();
        grilleLayer.setMouseTransparent(true);


        // Affichage de la grille
        for (int y = 0; y < nbRows; y++) {
            for (int x = 0; x < nbCols; x++) {
                Rectangle r = new Rectangle(x * taille, y * taille, taille, taille);

                // Vérifie si la case est une des cases spéciales pour passer à l'étage suivant
                if ((x == 13 && y == 4) || (x == 14 && y == 4) || (x == 15 && y == 4)) {
                    r.setFill(Color.BLUE);  // Met la case en bleu
                } else {
                    // Sinon, on vérifie si la case est bloquée
                    r.setFill(model.estCaseBloquee(x, y) ? Color.rgb(255, 0, 0, 0.3) : Color.TRANSPARENT);
                }

                // Bordure de la case
                r.setStroke(Color.rgb(255, 0, 0, 0.3));  // Bordure rouge avec une légère transparence

                // Affichage des coordonnées (pour la débug)
                Label coord = new Label(x + "," + y);
                coord.setTextFill(Color.rgb(255, 0, 0, 0.6));
                coord.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
                coord.setLayoutX(x * taille + 4);
                coord.setLayoutY(y * taille + 4);

                // Ajouter les rectangles et les labels de coordonnées dans le `grilleLayer`
                grilleLayer.getChildren().addAll(r, coord);
            }
        }

        // Ajouter tous les éléments à la vue
        //this.getChildren().addAll(mapLayer,mapLockView, grilleLayer, enemyLayer, playerLayer, statsBox, labelContainer);
        playerLayer.getChildren().add(cooldownBar); // enlever si on veut avoir une animation pour voir le cooldowen
        this.getChildren().addAll(mapView, mapViewBoss, mapLockView, enemyLayer, playerLayer, statsBox, labelContainer);


        this.setFocusTraversable(true);

        this.setFocusTraversable(true); // pour capter ESC
        activerEcouteClavier();

        // Mettre à jour la barre de vie du héros
        updateHpLabel(model);


    }

    /**
     * Met à jour la barre de vie et son label.
     *
     * @param currentHealth points de vie actuels (clampés entre 0 et maxHealth)
     * @param maxHealth points de vie maximum
     */
    public void updateHealth(int currentHealth, int maxHealth) {
        int clampedCurrent = Math.max(0, Math.min(currentHealth, maxHealth)); // ✅ sécurité
        double healthPercentage = (double) clampedCurrent / maxHealth;
        hpBarFill.setWidth(200 * healthPercentage);
        hpText.setText(I18N.get("gameview.hp") + ": " + clampedCurrent + " / " + maxHealth);
    }

    /**
     * Positionne le joueur et la hitbox, met à jour la barre de vie et les stats.
     *
     * @param x position X du joueur
     * @param y position Y du joueur
     */
    public void setPlayerPosition(double x, double y) {
        player.setLayoutX(x);
        player.setLayoutY(y);

        double hitboxWidth = 16;
        double hitboxHeight = 10;

        hitbox.setWidth(hitboxWidth);
        hitbox.setHeight(hitboxHeight);
        hitbox.setLayoutX(x + (150 - hitboxWidth) / 2);
        hitbox.setLayoutY(y + 150 - hitboxHeight - 10);

        cooldownBar.setLayoutX(x + 25); // centré au-dessus du joueur
        cooldownBar.setLayoutY(y - 10); // juste au-dessus de la tête

        // Mettre à jour la barre de vie à chaque déplacement
        updateHealth(model.getHero().getHealth(), model.getHero().getMaxHealth());
        updateStatsLabel();

    }

    /**
     * Supprime tous les ennemis graphiques de la vue.
     */
    public void clearEnemies() {
        enemyLayer.getChildren().clear();
        enemyViews.clear();
    }

    /**
     * Ajoute la représentation graphique d’un ennemi.
     *
     * @param mob le Character du modèle à afficher
     * @param x position X initiale de l’ennemi
     * @param y position Y initiale de l’ennemi
     */
    public void addEnemy(Character mob, double x, double y) {
        enemyGraphicsManager.addEnemy(mob, x, y);
    }

    private void updateHpLabel(GameModel model) {
        if (model != null && model.getHero() != null) {
            Hero hero = model.getHero();
            updateHealth(hero.getHealth(), hero.getMaxHealth());
        }
    }

    /**
     * Met à jour le label indiquant l’étage et la difficulté courants.
     */
    public void updateFloorLabel() {
        labelEtage.setText(
                I18N.get("gameview.floor") + " " + model.getLocationActuelle().getFloorLevel() +
                        " - " + I18N.get("gameview.difficulty") + " : " + model.getLocationActuelle().getDifficulty()
        );
    }

    /**
     * Retourne le Pane graphique d’un ennemi donné.
     *
     * @param mob le Character de l’ennemi
     * @return le Pane associé, ou null si non trouvé
     */
    public Pane getEnemyView(Character mob) {
        return enemyViews.get(mob);
    }

    /**
     * Affiche la superposition de verrouillage de la carte.
     */
    public void verrouillerMap() {
        mapLockView.setVisible(true);
    }

    /**
     * Masque la superposition de verrouillage de la carte.
     */
    public void deverrouillerMap() {
        mapLockView.setVisible(false);
    }

    /**
     * Met à jour les labels de vitesse, attaque et crit chance du héros.
     */
    public void updateStatsLabel() {
        Hero hero = model.getHero();
        if (hero != null) {
            Weapon weapon = hero.getWeapon();
            speedText.setText(I18N.get("gameview.speed") + " : " + hero.getSpeed());
            attackText.setText(I18N.get("gameview.attack") + " : " + hero.getAttackPower());
            critText.setText(I18N.get("gameview.crit") + " : " + hero.getCritChance() + "%");
        }
    }

    /**
     * Lance l’animation de clignotement pour indiquer l’invulnérabilité.
     */
    public void startImmunityBlink() {
        Timeline blink = new Timeline(
                new KeyFrame(Duration.millis(100), e -> player.setOpacity(0.3)),
                new KeyFrame(Duration.millis(200), e -> player.setOpacity(1.0))
        );
        blink.setCycleCount(10); // 2 secondes (10 * 200ms)
        blink.setOnFinished(e -> player.setOpacity(1.0)); // remet normal
        blink.play();
    }

    /**
     * Affiche la zone d’attaque selon la direction et l’arme du héros.
     *
     * @param x position X du joueur
     * @param y position Y du joueur
     * @param direction "up", "down", "left" ou "right"
     */
    public void afficherZoneAttaqueDirectionnelle(double x, double y, String direction) {
        Weapon weapon = model.getHero().getWeapon();
        double portee = weapon.getPortee();  // La hauteur de la zone
        double largeurFixe = 100;            // Largeur constante

        attackZone.setWidth(largeurFixe);
        attackZone.setHeight(portee);

        double offsetX = 0, offsetY = 0;

        switch (direction) {
            case "up" -> {
                offsetX = 25;                 // centré sur le joueur
                offsetY = -portee;           // vers le haut
            }
            case "down" -> {
                offsetX = 25;
                offsetY = 150;               // sous le joueur
            }
            case "left" -> {
                // Pour les côtés, on inverse largeur et hauteur
                attackZone.setWidth(portee);
                attackZone.setHeight(80);    // hauteur fixe sur les côtés
                offsetX = -portee;           // à gauche
                offsetY = 25;
            }
            case "right" -> {
                attackZone.setWidth(portee);
                attackZone.setHeight(80);
                offsetX = 150;               // à droite
                offsetY = 25;
            }
            default -> {
                return;
            }
        }

        attackZone.setLayoutX(x + offsetX);
        attackZone.setLayoutY(y + offsetY);
        attackZone.setVisible(true);
    }

    /**
     * Masque la zone d’attaque affichée.
     */
    public void cacherZoneAttaque() {
        attackZone.setVisible(false);
    }

    /**
     * Retourne le Pane contenant les ennemis.
     *
     * @return le layer des ennemis
     */
    public Pane getEnemyLayer() {
        return enemyLayer;
    }


    public void setMapBackgroundForFloor(int floorLevel) {
        boolean boss = (floorLevel == 30);
        mapView.setVisible(!boss);
        mapLockView.setVisible(!boss);
        mapViewBoss.setVisible(boss);


    }

    /**
     * Affiche le menu pause superposé sur la vue de jeu.
     */
    public void showPauseMenu() {
        if (pauseOverlay != null && this.getChildren().contains(pauseOverlay)) return;

        isPauseMenuVisible = true;

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("""
        -fx-background-color: rgba(0, 0, 0, 0.85);
        -fx-background-radius: 20;
        -fx-padding: 30;
    """);
        menuBox.setMaxWidth(300);
        menuBox.setMaxHeight(250);

        Button resumeBtn = new Button("🔙 " + I18N.get("pause.resume"));
        Button settingsBtn = new Button("⚙ " + I18N.get("pause.settings"));
        Button quitBtn = new Button("🏠 " + I18N.get("pause.quit"));
        Button saveBtn = new Button("💾 " + I18N.get("pause.save"));

        for (Button btn : new Button[]{resumeBtn, settingsBtn, quitBtn,saveBtn}) {
            btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            btn.setTextFill(Color.WHITE);
            btn.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 10; -fx-padding: 10;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-radius: 10; -fx-padding: 10;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 10; -fx-padding: 10;"));
        }

        resumeBtn.setOnAction(e -> hidePauseMenu());

        saveBtn.setOnAction(e -> {
            if (controleur != null) {
                controleur.sauvegarderPartie(controleur.getNomFichierSauvegarde());
            }
        });


        settingsBtn.setOnAction(e -> {
            Stage stage = (Stage) this.getScene().getWindow();
            MenuControleur menuControleur = new MenuControleur(stage);
            SettingsControleur sc = new SettingsControleur(stage, menuControleur);

            sc.setOnRetour(() -> {
                utils.TransitionUtils.fadeToScene(stage, this);
                if (controleur != null) {
                    controleur.updateSettings();
                    controleur.reloadKeyBindings();
                }
                hidePauseMenu();
            });

            StackPane settingsRoot = new StackPane(new utils.InkBackground(), new SettingsView(sc));
            utils.TransitionUtils.fadeToScene(stage, settingsRoot);
        });

        quitBtn.setOnAction(e -> {
            isPauseMenuVisible = false;
            MenuControleur mc = new MenuControleur((Stage) this.getScene().getWindow());
            utils.TransitionUtils.fadeToScene((Stage) this.getScene().getWindow(), mc.creerVueAvecFond(new Vues.Menu.SplashMenu(mc)));
        });

        menuBox.getChildren().addAll(resumeBtn, saveBtn, settingsBtn, quitBtn);
        pauseOverlay = new StackPane(menuBox);
        StackPane.setAlignment(menuBox, Pos.CENTER);
        this.getChildren().add(pauseOverlay);
    }


    /**
     * Ferme le menu pause et remet le focus sur le jeu.
     */
    public void hidePauseMenu() {
        if (pauseOverlay != null) {
            this.getChildren().remove(pauseOverlay);
            pauseOverlay = null;
        }
        isPauseMenuVisible = false;
        this.requestFocus(); // remet le focus au jeu
    }

    /**
     * Active la détection de la touche Échap pour basculer le menu pause.
     */
    public void activerEcouteClavier() {
        this.setFocusTraversable(true);
        this.requestFocus();

        // Handler local sur GameView
        this.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (isPauseMenuVisible) {
                    hidePauseMenu();
                } else {
                    showPauseMenu();
                }
            }
        });

    }

    /**
     * Indique si le menu pause est ouvert.
     *
     * @return true si visible, false sinon
     */
    public boolean isPauseMenuVisible() {
        return isPauseMenuVisible;
    }

    /**
     * Associe le contrôleur logique de la partie à cette vue.
     *
     * @param controleur le GameControleur à utiliser
     */
    public void setControleur(GameControleur controleur) {
        this.controleur = controleur;
    }

    /**
     * Affiche et anime la barre de cooldown sur la durée donnée.
     *
     * @param seconds durée du cooldown en secondes
     */
    public void afficherCooldown(double seconds) {
        cooldownBar.setVisible(true);
        cooldownBar.setWidth(100); // pleine barre

        Timeline cooldownAnim = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> cooldownBar.setWidth(100)),
                new KeyFrame(Duration.seconds(seconds), e -> {
                    cooldownBar.setVisible(false);
                    cooldownBar.setWidth(0);
                }, new javafx.animation.KeyValue(cooldownBar.widthProperty(), 0))
        );
        cooldownAnim.play();
    }

    /**
     * Retire la vue graphique d’un ennemi précis.
     *
     * @param mob le Character de l’ennemi à supprimer
     */
    public void removeEnemyView(Character mob) {
        enemyViews.remove(mob);
    }

    /**
     * Retourne le gestionnaire d’animations et sprites du joueur.
     *
     * @return l’instance de PlayerGraphicsManager
     */
    public PlayerGraphicsManager getPlayerGraphics() {
        return playerGraphics;
    }

}