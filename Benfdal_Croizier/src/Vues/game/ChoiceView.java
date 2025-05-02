/**
 * Vue de choix de porte pour passer à l’étage suivant.
 * Affiche trois flèches cliquables pour la sélection de la porte,
 * gère l’affichage des bonus, le rendu des ennemis et la navigation
 * au clavier (touche Entrée pour valider).
 */
package Vues.game;

import Controleurs.Game.ChoiceControleur;
import Controleurs.Game.GameControleur;
import Modeles.characters.Character;
import Modeles.items.consumables.Consumable;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class ChoiceView {
    private final Stage stage;
    private final ChoiceControleur choiceController;
    private final Pane root;
    private ImageView leftArrow, middleArrow, rightArrow;
    private TranslateTransition leftAnim, middleAnim, rightAnim;

    // juste après les champs existants
    private final Pane enemyLayer = new Pane();
    private final Map<Character, ImageView> enemyViews = new HashMap<>();

    /**
     * Construit la vue de sélection de porte.
     *
     * @param stage            la fenêtre principale (Stage) de l’application
     * @param choiceController le contrôleur qui gère la logique de navigation et de sélection
     */
    public ChoiceView(Stage stage, ChoiceControleur choiceController) {
        this.stage = stage;
        this.choiceController = choiceController; // ✅ injecté de l'extérieur
        this.root = buildRoot();
    }

    /**
     * Construit le conteneur principal de la vue.
     * Initialise le fond, les trois flèches, les bonus cliquables,
     * bind les dimensions au Stage et configure les handlers clavier.
     *
     * @return le Pane racine prêt à être affiché
     */
    private Pane buildRoot() {
        Pane pane = new Pane();
        pane.prefWidthProperty().bind(stage.widthProperty());
        pane.prefHeightProperty().bind(stage.heightProperty());

        // Fond plein écran
        ImageView bg = new ImageView(
                new Image(getClass().getResource("/assets/image/choix.png").toExternalForm())
        );
        bg.setPreserveRatio(false);
        bg.fitWidthProperty().bind(pane.widthProperty());
        bg.fitHeightProperty().bind(pane.heightProperty());
        pane.getChildren().add(bg);

        // --- Flèches ---
        leftArrow   = createArrow();
        middleArrow = createArrow();
        rightArrow  = createArrow();

        leftArrow.setLayoutX(541);
        leftArrow.setLayoutY(880);
        middleArrow.setLayoutX(900);
        middleArrow.setLayoutY(880);
        rightArrow.setLayoutX(1258);
        rightArrow.setLayoutY(880);

        pane.getChildren().addAll(leftArrow, middleArrow, rightArrow);

        // Focus & clavier
        pane.setFocusTraversable(true);
        pane.setOnKeyPressed(evt -> {
            choiceController.handleKeyPressed(evt.getCode());
            updateArrowSelection();

            if (evt.getCode() == KeyCode.ENTER) {
                choiceController.confirmChoice(); // Confirme le choix et passe à l'étage suivant
            }
        });

        Platform.runLater(pane::requestFocus);

        updateArrowSelection();

        // --- Bonus ---

        for (int i = 0; i < 3; i++) {
            Consumable bonus = choiceController.getBonusChoices().get(i);

            // Image du bonus
            Image img = new Image(getClass().getResource("/assets/image/" + getImageForBonus(bonus)).toExternalForm());
            ImageView iv = new ImageView(img);
            iv.setFitWidth(128);
            iv.setFitHeight(128);

            // Overlay avec texte
            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
            overlay.setPrefSize(128, 128);
            overlay.setVisible(false);

            Label title = new Label(bonus.getDisplayName());
            title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            title.setLayoutX(10);
            title.setLayoutY(10);

            Label desc = new Label(bonus.getDescription());
            desc.setStyle("-fx-text-fill: white;");
            desc.setWrapText(true);
            desc.setPrefWidth(108);
            desc.setLayoutX(10);
            desc.setLayoutY(40);

            overlay.getChildren().addAll(title, desc);

            // Container principal du bonus
            StackPane container = new StackPane(iv, overlay);
            container.setPrefSize(128, 128);
            container.setLayoutY(175);
            container.setLayoutX((i == 0) ? 540 : (i == 1) ? 900 : 1245);

            container.setOnMouseEntered(e -> overlay.setVisible(true));
            container.setOnMouseExited(e -> overlay.setVisible(false));

            pane.getChildren().add(container);
        }

        return pane;
    }

    /**
     * Retourne le composant JavaFX à insérer dans la scène.
     *
     * @return un Parent représentant l’arbre de nœuds de cette vue
     */
    public Parent getVue() {
        return root;
    }

    /**
     * Crée un ImageView configuré pour représenter une flèche de sélection.
     *
     * @return une flèche (ImageView) prête à être positionnée et animée
     */
    private ImageView createArrow() {
        ImageView iv = new ImageView(
                new Image(getClass().getResource("/assets/image/arrow.png").toExternalForm())
        );
        iv.setFitWidth(128);
        iv.setFitHeight(128);
        return iv;
    }

    /**
     * Met à jour l’opacité et les animations des flèches
     * en fonction de l’indice de sélection renvoyé par le contrôleur.
     */
    private void updateArrowSelection() {
        int sel = choiceController.getSelectedDoor();
        leftArrow.setOpacity(sel == 0 ? 1.0 : 0.5);
        middleArrow.setOpacity(sel == 1 ? 1.0 : 0.5);
        rightArrow.setOpacity(sel == 2 ? 1.0 : 0.5);

        animateArrow(leftArrow,   sel == 0);
        animateArrow(middleArrow, sel == 1);
        animateArrow(rightArrow,  sel == 2);
    }

    /**
     * Renvoie le nom de fichier de l’image correspondant à un bonus donné.
     *
     * @param bonus l’objet Consumable dont on veut l’icône
     * @return le nom du fichier image à charger (ex. "SpeedBoost.png")
     */
    private String getImageForBonus(Consumable bonus) {
        String id = bonus.getClass().getSimpleName(); // Exemple : "SpeedBoost"
        return switch (id) {
            case "SpeedBoost" -> "SpeedBoost.png";
            case "AttackPowerBoost" -> "AttackPowerBoost.png";
            case "HealthBoost" -> "HealthBoost.png";
            case "LuckBoost" -> "LuckBoost.png";
            default -> "default.png";
        };
    }


    /**
     * Anime ou stoppe l’oscillation verticale d’une flèche.
     *
     * @param arrow    la flèche (ImageView) à animer ou arrêter
     * @param selected true pour démarrer l’animation (sélectionnée), false pour l’arrêter
     */
    private void animateArrow(ImageView arrow, boolean selected) {
        TranslateTransition tt;
        if      (arrow == leftArrow)   tt = leftAnim;
        else if (arrow == middleArrow) tt = middleAnim;
        else                            tt = rightAnim;

        if (selected) {
            // Démarre l'animation si pas déjà en cours
            if (tt == null) {
                tt = new TranslateTransition(Duration.millis(500), arrow);
                tt.setFromY(0);
                tt.setToY(-20);
                tt.setAutoReverse(true);
                tt.setCycleCount(TranslateTransition.INDEFINITE);
                tt.play();
                if      (arrow == leftArrow)   leftAnim   = tt;
                else if (arrow == middleArrow) middleAnim = tt;
                else                            rightAnim  = tt;
            }
        } else {
            // Arrête l'animation si elle existe
            if (tt != null) {
                tt.stop();
                arrow.setTranslateY(0);
                if      (arrow == leftArrow)   leftAnim   = null;
                else if (arrow == middleArrow) middleAnim = null;
                else                            rightAnim  = null;
            }
        }
    }
}
