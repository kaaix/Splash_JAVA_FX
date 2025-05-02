package Controleurs.Editor;

import Controleurs.Menu.MenuControleur;
import Modeles.editor.LevelEditorModel;
import Modeles.settings.SettingsModel;
import Vues.Menu.SplashMenu;
import Vues.editor.LevelEditorView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.I18N;
import utils.TransitionUtils;
import utils.InkBackground;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.geometry.Point2D;

public class LevelEditorController {
    private final MenuControleur menuControleur;
    private final LevelEditorModel model;
    private final LevelEditorView view;
    private final Stage stage;
    private final Parent vue;

    public LevelEditorController(Stage stage, MenuControleur menuControleur, int cols, int rows) {
        this.stage = stage;
        this.menuControleur = menuControleur;
        this.model = new LevelEditorModel(cols, rows);
        this.view = new LevelEditorView(model, this);

        InkBackground fond = new InkBackground();
        fond.prefWidthProperty().bind(stage.widthProperty());
        fond.prefHeightProperty().bind(stage.heightProperty());

        StackPane contentWrapper = new StackPane(view);
        contentWrapper.setPickOnBounds(false);

        this.vue = new StackPane(fond, contentWrapper);
    }

    public Parent getVue() {
        return vue;
    }

    public int getCols() {
        return model.getCols();
    }

    public int getRows() {
        return model.getRows();
    }

    public void changeFloorBy(int delta) {
        int newFloor = Math.max(0, model.getFloorNumber() + delta);
        model.setFloorNumber(newFloor);
        view.updateFloorLabel(newFloor);
    }

    public void onTileClick(int x, int y) {
        Point2D p = new Point2D(x, y);
        boolean hasPlayer = model.getPlayerSpawn() != null && model.getPlayerSpawn().equals(p);
        boolean hasEnemy = model.getEnemySpawns().contains(p);

        switch (view.getSelectedTool()) {
            case OBSTACLE -> {
                if (hasPlayer || hasEnemy) return;
                if (model.getNextFloorTiles().contains(p)) model.removeNextFloorTile(x, y);
                model.toggleWalkable(x, y);
            }
            case NEXT_FLOOR -> {
                if (hasPlayer || hasEnemy) return;
                if (!model.isWalkable(x, y)) model.toggleWalkable(x, y);
                if (model.getNextFloorTiles().contains(p)) model.removeNextFloorTile(x, y);
                else model.addNextFloorTile(x, y);
            }
            case PLAYER -> {
                if (model.isWalkable(x, y) && !model.getNextFloorTiles().contains(p)) {
                    model.setPlayerSpawn(x, y);
                }
            }
            case ENEMY -> {
                if (model.isWalkable(x, y) && !model.getNextFloorTiles().contains(p)) {
                    if (model.getEnemySpawns().contains(p)) model.removeEnemySpawn(x, y);
                    else model.addEnemySpawn(x, y);
                }
            }
        }
        view.redraw();
    }

    public void onSelectBackground() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"));
        File f = fc.showOpenDialog(stage);
        if (f != null) {
            model.setBackgroundImagePath(f.getAbsolutePath());
            view.redraw();
        }
    }

    public void onSave() {
        if (model.getPlayerSpawn() == null ||
                model.getNextFloorTiles().isEmpty() ||
                model.getEnemySpawns().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Niveau incomplet");
            alert.setHeaderText(null);
            alert.setContentText("Le niveau doit contenir :\n- un spawn joueur\n- au moins une case bleue\n- au moins un ennemi.");
            alert.showAndWait();
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"width\": ").append(model.getCols()).append(",\n");
            sb.append("  \"height\": ").append(model.getRows()).append(",\n");
            sb.append("  \"floorNumber\": ").append(model.getFloorNumber()).append(",\n");
            sb.append("  \"backgroundImagePath\": ")
                    .append(model.getBackgroundImagePath() != null
                            ? "\"" + model.getBackgroundImagePath().replace("\\", "\\\\") + "\""
                            : "null").append(",\n");

            sb.append("  \"walkable\": [\n");
            for (int y = 0; y < model.getRows(); y++) {
                sb.append("    [");
                int finalY = y;
                String row = IntStream.range(0, model.getCols())
                        .mapToObj(x -> model.isWalkable(x, finalY) ? "true" : "false")
                        .collect(Collectors.joining(", "));
                sb.append(row).append("]");
                if (y < model.getRows() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ],\n");

            sb.append("  \"nextFloorTiles\": ")
                    .append(model.getNextFloorTiles().isEmpty()
                            ? "[]"
                            : model.getNextFloorTiles().stream()
                            .map(p -> String.format("{\"x\":%d,\"y\":%d}", (int)p.getX(), (int)p.getY()))
                            .collect(Collectors.joining(",\n    ", "[\n    ", "\n  ]")))
                    .append(",\n");

            sb.append("  \"playerSpawn\": ")
                    .append(model.getPlayerSpawn() != null
                            ? String.format("{\"x\":%d,\"y\":%d}",
                            (int)model.getPlayerSpawn().getX(), (int)model.getPlayerSpawn().getY())
                            : "null").append(",\n");

            sb.append("  \"enemySpawns\": ")
                    .append(model.getEnemySpawns().isEmpty()
                            ? "[]"
                            : model.getEnemySpawns().stream()
                            .map(p -> String.format("{\"x\":%d,\"y\":%d}", (int)p.getX(), (int)p.getY()))
                            .collect(Collectors.joining(",\n    ", "[\n    ", "\n  ]")))
                    .append("\n");

            sb.append("}\n");

            String fileName = String.format("level_%d_%d.json",
                    model.getFloorNumber(), System.currentTimeMillis());
            Files.write(Paths.get(fileName), sb.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("✅ Level saved to " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onReset() {
        model.clear();
        view.redraw();
        view.updateFloorLabel(model.getFloorNumber());
    }

    public void onReturn() {
        SettingsModel settings = SettingsModel.load();
        I18N.setLangue(settings.getLangue());
        stage.setFullScreenExitHint("");
        stage.setFullScreen(settings.isFullscreen());
        SplashMenu splash = new SplashMenu(menuControleur);
        StackPane root = menuControleur.creerVueAvecFond(splash);
        TransitionUtils.fadeToScene(stage, root);
    }

    public void onLoad() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Level JSON", "*.json"));
        File f = fc.showOpenDialog(stage);
        if (f == null) return;

        try {
            // 1. Lire le fichier
            String json = Files.readString(f.toPath(), StandardCharsets.UTF_8);

            // 2. Extraire width et height du JSON
            Matcher mw = Pattern.compile("\"width\"\\s*:\\s*(\\d+)").matcher(json);
            Matcher mh = Pattern.compile("\"height\"\\s*:\\s*(\\d+)").matcher(json);
            if (!mw.find() || !mh.find()) {
                showError("Fichier invalide : dimensions manquantes.");
                return;
            }
            int width = Integer.parseInt(mw.group(1));
            int height = Integer.parseInt(mh.group(1));

            // 3. Recréer un nouveau modèle + vue avec ces dimensions
            LevelEditorController newCtrl = new LevelEditorController(stage, menuControleur, width, height);
            newCtrl.loadFromJson(json);

            // 4. Transition vers la nouvelle vue
            TransitionUtils.fadeToScene(stage, newCtrl.getVue());

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur de chargement : " + e.getMessage());
        }
    }

    private void loadFromJson(String json) {
        try {
            model.clear();

            // floorNumber
            Matcher mf = Pattern.compile("\"floorNumber\"\\s*:\\s*(\\d+)").matcher(json);
            if (mf.find()) model.setFloorNumber(Integer.parseInt(mf.group(1)));

            // background
            Matcher mb = Pattern.compile("\"backgroundImagePath\"\\s*:\\s*(?:\"(.*?)\"|null)").matcher(json);
            if (mb.find()) model.setBackgroundImagePath(mb.group(1));

            // walkable
            Pattern pWalk = Pattern.compile("\"walkable\"\\s*:\\s*\\[((?:\\s*\\[[^\\]]*\\]\\s*,?)+)\\]", Pattern.DOTALL);
            Matcher mw = pWalk.matcher(json);
            if (mw.find()) {
                String block = mw.group(1).trim();
                String[] rowsArr = block.split("\\]\\s*,");
                for (int y = 0; y < rowsArr.length && y < model.getRows(); y++) {
                    String row = rowsArr[y].replaceAll("[\\[\\]\\s]", "");
                    if (row.isEmpty()) continue;
                    String[] vals = row.split(",");
                    for (int x = 0; x < vals.length && x < model.getCols(); x++) {
                        boolean walk = Boolean.parseBoolean(vals[x]);
                        if (!walk) model.toggleWalkable(x, y);
                    }
                }
            }

            // nextFloorTiles
            Matcher mn = Pattern.compile("\"nextFloorTiles\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(json);
            if (mn.find()) {
                Matcher mc = Pattern.compile("\\{\"x\":(\\d+),\"y\":(\\d+)\\}").matcher(mn.group(1));
                while (mc.find()) {
                    model.addNextFloorTile(Integer.parseInt(mc.group(1)), Integer.parseInt(mc.group(2)));
                }
            }

            // playerSpawn
            Matcher mp = Pattern.compile("\"playerSpawn\"\\s*:\\s*(\\{.*?\\}|null)", Pattern.DOTALL).matcher(json);
            if (mp.find() && !mp.group(1).equals("null")) {
                Matcher mc = Pattern.compile("\"x\":(\\d+),\"y\":(\\d+)").matcher(mp.group(1));
                if (mc.find()) {
                    model.setPlayerSpawn(Integer.parseInt(mc.group(1)), Integer.parseInt(mc.group(2)));
                }
            }

            // enemySpawns
            Matcher me = Pattern.compile("\"enemySpawns\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(json);
            if (me.find()) {
                Matcher mc = Pattern.compile("\\{\"x\":(\\d+),\"y\":(\\d+)\\}").matcher(me.group(1));
                while (mc.find()) {
                    model.addEnemySpawn(Integer.parseInt(mc.group(1)), Integer.parseInt(mc.group(2)));
                }
            }

            // mise à jour interface
            view.updateFloorLabel(model.getFloorNumber());
            view.redraw();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Erreur de parsing du JSON.");
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}