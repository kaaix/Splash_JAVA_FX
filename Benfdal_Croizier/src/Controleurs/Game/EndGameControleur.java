/**
 * Contrôleur pour gérer la fin de partie.
 * Affiche l’écran Game Over ou Victoire avec transition animée.
 */
package Controleurs.Game;

import Vues.game.GameOverView;
import Vues.game.VictoryView;
import javafx.stage.Stage;
import utils.TransitionUtils;

public class EndGameControleur {
    private final Stage stage;

    /**
     * Initialise le contrôleur de fin de partie.
     *
     * @param stage la fenêtre principale (Stage) de l’application
     */
    public EndGameControleur(Stage stage) {
        this.stage = stage;
    }

    /**
     * Affiche la vue de Game Over avec un fondu de transition.
     */
    public void showGameOver() {
        GameOverView gameOverView = new GameOverView(stage);
        TransitionUtils.fadeToScene(stage, gameOverView);
    }

    /**
     * Affiche la vue de victoire avec un message personnalisé
     * et le score du joueur, puis effectue un fondu de transition.
     *
     * @param playerName   nom du joueur victorieux
     * @param scoreSeconds score réalisé en secondes
     */
    public void showVictory(String playerName, int scoreSeconds) {
        VictoryView victoryView = new VictoryView(stage, playerName, scoreSeconds);
        TransitionUtils.fadeToScene(stage, victoryView);
    }
}
