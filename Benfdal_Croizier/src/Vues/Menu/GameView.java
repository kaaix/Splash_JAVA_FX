package Vues.Menu;

import Controleurs.Menu.GameControleur;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class GameView extends VBox {
    public GameView(GameControleur controleur) {
        this.setSpacing(30);
        this.setAlignment(Pos.CENTER);

        Button retour = new Button("⬅ Quitter le jeu");
        retour.setOnAction(e -> controleur.quitterJeu());

        this.getChildren().add(retour);
    }
}
