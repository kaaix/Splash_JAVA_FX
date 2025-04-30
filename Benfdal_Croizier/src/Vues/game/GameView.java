package Vues.game;

import Modeles.characters.Character;
import Modeles.game.GameModel;
import Modeles.characters.Hero;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

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

    private Image playerUp1, playerUp2;
    private Image playerDown1, playerDown2;
    private Timeline walkUpAnimation;
    private Timeline walkDownAnimation;

    // Déclaration du label pour afficher l'étage et la difficulté
    private Label labelEtage;

    private final Pane enemyLayer = new Pane();
    private final Map<Character, ImageView> enemyViews = new HashMap<>();

    private Rectangle attackZone;
    private ImageView mapLockView;


    public GameView(GameModel model) {
        this.model = model; // ✅ affecte le modèle !
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        // Charger l'image de la carte
        Image mapImage = new Image(getClass().getResource("/assets/image/map.png").toExternalForm());
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(false);
        mapView.setSmooth(false);
        mapView.fitHeightProperty().bind(heightProperty());
        mapView.fitWidthProperty().bind(widthProperty());

        Image mapLockImage = new Image(getClass().getResource("/assets/image/maplock.png").toExternalForm());
        mapLockView = new ImageView(mapLockImage);
        mapView.setPreserveRatio(false);
        mapView.setSmooth(false);
        mapView.fitHeightProperty().bind(heightProperty());
        mapView.fitWidthProperty().bind(widthProperty());
        mapLockView.setVisible(false);       // caché par défaut

        // Chargement des images du joueur
        playerUp1 = new Image(getClass().getResource("/assets/image/player-up1.png").toExternalForm());
        playerUp2 = new Image(getClass().getResource("/assets/image/player-up2.png").toExternalForm());
        playerDown1 = new Image(getClass().getResource("/assets/image/player-down1.png").toExternalForm());
        playerDown2 = new Image(getClass().getResource("/assets/image/player-down2.png").toExternalForm());

        // Animation du joueur en haut
        walkUpAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.3), e -> player.setImage(playerUp1)),
                new KeyFrame(Duration.seconds(0.6), e -> player.setImage(playerUp2))
        );
        walkUpAnimation.setCycleCount(Animation.INDEFINITE);

        // Animation du joueur en bas
        walkDownAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.3), e -> player.setImage(playerDown1)),
                new KeyFrame(Duration.seconds(0.6), e -> player.setImage(playerDown2))
        );
        walkDownAnimation.setCycleCount(Animation.INDEFINITE);

        // Image du joueur
        Image playerImage = new Image(getClass().getResource("/assets/image/player.png").toExternalForm());
        player = new ImageView(playerImage);
        player.setFitWidth(150);
        player.setFitHeight(150);
        player.setPreserveRatio(true);

        // Création du label pour l'étage et la difficulté
        labelEtage = new Label();
        labelEtage.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        labelEtage.setTextFill(Color.WHITE);

        // Lier le texte du label à l'étage et à la difficulté
        labelEtage.setText("Étage " + model.getLocationActuelle().getFloorLevel() +
                " - Difficulté: " + model.getLocationActuelle().getDifficulty());


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
        hitbox.setStroke(Color.LIMEGREEN);
        hitbox.setFill(Color.color(0, 1, 0, 0.2));

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
        Pane mapLayer = new Pane(mapView);
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
        this.getChildren().addAll(mapLayer,mapLockView, grilleLayer, enemyLayer, playerLayer, statsBox, labelContainer);
        this.setFocusTraversable(true);

        // Mettre à jour la barre de vie du héros
        updateHpLabel(model);
    }

    // Méthode pour mettre à jour la barre de vie
    public void updateHealth(int currentHealth, int maxHealth) {
        double healthPercentage = (double) currentHealth / maxHealth;  // Calculer la proportion de la vie
        hpBarFill.setWidth(200 * healthPercentage);  // Ajuster la largeur de la barre rouge
        hpText.setText("HP: " + currentHealth);  // Afficher les HP
    }

    public void setPlayerPosition(double x, double y) {
        player.setLayoutX(x);
        player.setLayoutY(y);

        double hitboxWidth = 16;
        double hitboxHeight = 10;

        hitbox.setWidth(hitboxWidth);
        hitbox.setHeight(hitboxHeight);
        hitbox.setLayoutX(x + (150 - hitboxWidth) / 2);
        hitbox.setLayoutY(y + 150 - hitboxHeight - 10);

        // Mettre à jour la barre de vie à chaque déplacement
        updateHealth(model.getHero().getHealth(), 100);
        updateStatsLabel();
    }

    public void clearEnemies() {
        enemyLayer.getChildren().clear();
        enemyViews.clear();
    }

    public void addEnemy(Modeles.characters.Character mob, double x, double y) {
        Image img = new Image(getClass()
                .getResource("/assets/image/mob1.png")
                .toExternalForm());
        ImageView iv = new ImageView(img);
        iv.setFitWidth(150);           // on reprend 150px comme pour le joueur
        iv.setFitHeight(150);
        iv.setPreserveRatio(true);
        iv.setLayoutX(x);
        iv.setLayoutY(y);
        enemyViews.put(mob, iv);
        enemyLayer.getChildren().add(iv);
    }


    // Méthode pour mettre à jour les HP du modèle
    private void updateHpLabel(GameModel model) {
        if (model != null && model.getHero() != null) {
            int hp = model.getHero().getHealth(); // Récupérer les HP du héros
            updateHealth(hp, 100);  // Mise à jour de la barre de vie
        }
    }

    public void updateFloorLabel() {
        // Exemple de mise à jour de l'affichage de l'étage, tu devras l'adapter à ta vue
        labelEtage.setText("Étage " + model.getLocationActuelle().getFloorLevel() +
                " - Difficulté : " + model.getLocationActuelle().getDifficulty());

    }

    public ImageView getEnemyView(Modeles.characters.Character mob) {
        return enemyViews.get(mob);
    }


    public void startWalkUpAnimation() {
        walkDownAnimation.stop();
        walkUpAnimation.play();
    }

    public void stopWalkUpAnimation() {
        walkUpAnimation.stop();
        player.setImage(playerUp1);
    }

    public void startWalkDownAnimation() {
        walkUpAnimation.stop();
        walkDownAnimation.play();
    }

    public void verrouillerMap() {
        mapLockView.setVisible(true);
    }

    public void deverrouillerMap() {
        mapLockView.setVisible(false);
    }


    public void stopWalkDownAnimation() {
        walkDownAnimation.stop();
        player.setImage(playerDown1);
    }

    public void updateStatsLabel() {
        Hero hero = model.getHero();
        if (hero != null) {
            speedText.setText("Vitesse : " + hero.getSpeed());
            attackText.setText("Attaque : " + hero.getAttackPower());
            critText.setText("Critique : " + hero.getCritChance() + "%");
        }
    }

    public void startImmunityBlink() {
        Timeline blink = new Timeline(
                new KeyFrame(Duration.millis(100), e -> player.setOpacity(0.3)),
                new KeyFrame(Duration.millis(200), e -> player.setOpacity(1.0))
        );
        blink.setCycleCount(10); // 2 secondes (10 * 200ms)
        blink.setOnFinished(e -> player.setOpacity(1.0)); // remet normal
        blink.play();
    }

    public void afficherZoneAttaqueDirectionnelle(double x, double y, String direction) {
        double w = attackZone.getWidth();
        double h = attackZone.getHeight();
        double offsetX = 0, offsetY = 0;

        switch (direction) {
            case "up":
                offsetX = 25;
                offsetY = -h;
                break;
            case "down":
                offsetX = 25;
                offsetY = 150;
                break;
            case "left":
                offsetX = -w;
                offsetY = 25;
                break;
            case "right":
                offsetX = 150;
                offsetY = 25;
                break;
            default:
                return;
        }

        attackZone.setLayoutX(x + offsetX);
        attackZone.setLayoutY(y + offsetY);
        attackZone.setVisible(true);
    }

    public void cacherZoneAttaque() {
        attackZone.setVisible(false);
    }

    public Pane getEnemyLayer() {
        return enemyLayer;
    }



}
