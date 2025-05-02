/**
 * Vue de l’éditeur de niveaux 2D.
 * Permet de dessiner la grille, de choisir un outil
 * (obstacle, spawn joueur, spawn ennemi, étage suivant)
 * et de charger/sauvegarder des niveaux sous forme JSON.
 */
package Vues.editor;

import Modeles.editor.LevelEditorModel;
import Controleurs.Editor.LevelEditorController;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import utils.I18N;

import java.io.File;

public class LevelEditorView extends StackPane {
    /**
     * Outils disponibles pour modifier le niveau :
     * OBSTACLE — placer ou retirer des obstacles,
     * PLAYER — définir le point de spawn du joueur,
     * ENEMY — ajouter des positions d’ennemis,
     * NEXT_FLOOR — positionner la case “étage suivant”.
     */
    public enum Tool { OBSTACLE, PLAYER, ENEMY, NEXT_FLOOR }

    private final LevelEditorModel model;
    private final LevelEditorController controller;

    private final BorderPane contentPane;
    private Canvas canvas;
    private ToggleGroup toolGroup;
    private Label floorLabel;

    /**
     * Construit la vue de l’éditeur de niveaux.
     *
     * @param model      le modèle contenant la taille et les données du niveau
     * @param controller le contrôleur gérant les actions (clic tile, load/save, etc.)
     */
    public LevelEditorView(LevelEditorModel model, LevelEditorController controller) {
        this.model = model;
        this.controller = controller;
        this.contentPane = new BorderPane();
        getChildren().add(contentPane);
        initUI();
    }

    private void initUI() {
        // --- Canvas + ScrollPane ---
        canvas = new Canvas(
                model.TILE_SIZE * controller.getCols(),
                model.TILE_SIZE * controller.getRows()
        );
        canvas.setOnMouseClicked(e -> {
            int x = (int)(e.getX() / model.TILE_SIZE);
            int y = (int)(e.getY() / model.TILE_SIZE);
            controller.onTileClick(x, y);
        });
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setPannable(true);
        contentPane.setCenter(scroll);

        // --- Tool palette on the left ---
        toolGroup = new ToggleGroup();
        RadioButton rbObs    = new RadioButton(I18N.get("editor.obstacle"));
        RadioButton rbPlayer = new RadioButton(I18N.get("editor.spawn_player"));
        RadioButton rbEnemy  = new RadioButton(I18N.get("editor.spawn_enemy"));
        RadioButton rbNext   = new RadioButton(I18N.get("editor.next_floor"));
        rbObs.setUserData(Tool.OBSTACLE);
        rbPlayer.setUserData(Tool.PLAYER);
        rbEnemy.setUserData(Tool.ENEMY);
        rbNext.setUserData(Tool.NEXT_FLOOR);
        rbObs.setToggleGroup(toolGroup);
        rbPlayer.setToggleGroup(toolGroup);
        rbEnemy.setToggleGroup(toolGroup);
        rbNext.setToggleGroup(toolGroup);
        rbObs.setSelected(true);

        Button btnSelectBg = new Button(I18N.get("editor.select_background"));
        btnSelectBg.setOnAction(e -> controller.onSelectBackground());

        Button btnLoad     = new Button(I18N.get("editor.load"));
        btnLoad.setOnAction(e -> controller.onLoad());

        Button btnSave     = new Button(I18N.get("editor.save"));
        btnSave.setOnAction(e -> controller.onSave());

        Button btnReset    = new Button(I18N.get("editor.reset"));
        btnReset.setOnAction(e -> controller.onReset());

        Button btnReturn   = new Button(I18N.get("editor.return"));
        btnReturn.setOnAction(e -> controller.onReturn());

        // Nouvelle zone : affichage + changement de l’étage
        Button btnMinus = new Button("-");
        floorLabel = new Label(I18N.get("editor.floor"));
        Button btnPlus = new Button("+");
        btnMinus.setOnAction(e -> controller.changeFloorBy(-1));
        btnPlus.setOnAction(e -> controller.changeFloorBy(+1));

        VBox toolPane = new VBox(10,
                rbObs, rbPlayer, rbEnemy, rbNext,
                new Separator(),
                btnSelectBg,
                btnLoad,
                btnSave,
                new Separator(),floorLabel,
                btnMinus, btnPlus,
                new Separator(),
                btnReset,
                btnReturn
        );
        toolPane.setPadding(new Insets(10));
        contentPane.setLeft(toolPane);

        // Initial render
        redraw();
    }

    /**
     * Retourne l’outil actuellement sélectionné dans la palette.
     *
     * @return l’outil actif (OBSTACLE, PLAYER, ENEMY ou NEXT_FLOOR)
     */
    public Tool getSelectedTool() {
        return (Tool) toolGroup.getSelectedToggle().getUserData();
    }

    /**
     * Redessine entièrement le contenu du canvas :
     * arrière-plan, obstacles, tuiles “next floor”,
     * spawns joueur et ennemis, en fonction du modèle.
     */
    public void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // draw background
        if (model.getBackgroundImagePath() != null) {
            Image bg = new Image(new File(model.getBackgroundImagePath())
                    .toURI().toString());
            gc.drawImage(bg, 0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
        // draw obstacles & next-floor tiles
        for (int y = 0; y < controller.getRows(); y++) {
            for (int x = 0; x < controller.getCols(); x++) {
                double px = x * model.TILE_SIZE;
                double py = y * model.TILE_SIZE;
                if (!model.isWalkable(x, y)) {
                    gc.setFill(Color.rgb(255, 0, 0, 0.4));
                    gc.fillRect(px, py, model.TILE_SIZE, model.TILE_SIZE);
                }
                if (model.getNextFloorTiles().contains(new Point2D(x, y))) {
                    gc.setFill(Color.rgb(0, 0, 255, 0.4));
                    gc.fillRect(px, py, model.TILE_SIZE, model.TILE_SIZE);
                }
            }
        }
        // draw player spawn
        if (model.getPlayerSpawn() != null) {
            Point2D p = model.getPlayerSpawn();
            gc.setFill(Color.GREEN);
            gc.fillOval(p.getX() * model.TILE_SIZE + 10,
                    p.getY() * model.TILE_SIZE + 10,
                    20, 20);
        }
        // draw enemy spawns
        for (Point2D p : model.getEnemySpawns()) {
            gc.setFill(Color.ORANGE);
            gc.fillOval(p.getX() * model.TILE_SIZE + 10,
                    p.getY() * model.TILE_SIZE + 10,
                    20, 20);
        }
    }

    /**
     * Met à jour l’étiquette affichant le numéro d’étage courant.
     *
     * @param floor le numéro de l’étage à afficher
     */
    public void updateFloorLabel(int floor) {
        floorLabel.setText("" + floor);
    }
}
