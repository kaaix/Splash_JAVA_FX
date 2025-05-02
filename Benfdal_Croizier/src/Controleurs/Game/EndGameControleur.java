package Controleurs.Game;

import Vues.game.GameOverView;
import Vues.game.VictoryView;
import javafx.stage.Stage;
import utils.TransitionUtils;

public class EndGameControleur {
    private final Stage stage;

    public EndGameControleur(Stage stage) {
        this.stage = stage;
    }

    public void showGameOver() {
        GameOverView gameOverView = new GameOverView(stage);
        TransitionUtils.fadeToScene(stage, gameOverView);
    }

    public void showVictory(String playerName, int scoreSeconds) {
        VictoryView victoryView = new VictoryView(stage, playerName, scoreSeconds);
        TransitionUtils.fadeToScene(stage, victoryView);
    }
}
