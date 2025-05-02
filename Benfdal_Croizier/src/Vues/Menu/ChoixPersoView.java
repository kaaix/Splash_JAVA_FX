/**
 * Vue pour le choix du personnage (arme) et du nom de joueur.
 * Affiche un champ de saisie du nom, trois icônes d’armes (Shooter, Roller, Charger)
 * avec survol pour voir leurs statistiques, et boutons de validation/retour.
 */
package Vues.Menu;

import Controleurs.Menu.ChoixPersoControleur;
import Modeles.items.weapons.Weapon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.I18N;

import java.util.Objects;



public class ChoixPersoView extends VBox {

    private final Label infoLabel = new Label();

    /**
     * Construit la vue de choix de personnage.
     * Initialise le titre, le champ de nom, les images d’armes, ainsi que
     * les handlers de sélection et de validation.
     *
     * @param controleur le contrôleur gérant la logique de validation et de navigation
     */
    public ChoixPersoView(ChoixPersoControleur controleur) {
        this.setSpacing(40);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(50));
        this.setStyle("-fx-background-color: rgba(0,0,0,0.7);");

        Label titre = new Label(I18N.get("choix.titre"));
        titre.setFont(Font.font("Arial", 36));
        titre.setTextFill(Color.WHITE);

        TextField nomField = new TextField();
        nomField.setPromptText(I18N.get("choix.nom"));
        nomField.setMaxWidth(300);
        nomField.setStyle("-fx-font-size: 16px; -fx-padding: 10;");

        Label armeLabel = new Label(I18N.get("choix.arme"));
        armeLabel.setFont(Font.font("Arial", 22));
        armeLabel.setTextFill(Color.WHITE);

        ImageView shooter = createWeaponImage("/assets/image/shooter.png", 120);
        ImageView roller = createWeaponImage("/assets/image/rooler.png", 120);
        ImageView charger = createWeaponImage("/assets/image/charger.png", 120);

        infoLabel.setFont(Font.font("Arial", 16));
        infoLabel.setTextFill(Color.WHITE);
        infoLabel.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-padding: 10; -fx-background-radius: 10;");
        infoLabel.setVisible(false);

        infoLabel.setAlignment(Pos.TOP_RIGHT);

        VBox shooterBox = createWeaponBox(shooter);
        VBox rollerBox = createWeaponBox(roller);
        VBox chargerBox = createWeaponBox(charger);

        shooterBox.setOnMouseEntered(e -> showStats(Objects.requireNonNull(Weapon.parseFromString("Shooter"))));
        rollerBox.setOnMouseEntered(e -> showStats(Objects.requireNonNull(Weapon.parseFromString("Roller"))));
        chargerBox.setOnMouseEntered(e -> showStats(Objects.requireNonNull(Weapon.parseFromString("Charger"))));

        HBox choixArmes = new HBox(40, rollerBox, shooterBox, chargerBox);
        choixArmes.setAlignment(Pos.CENTER);
        choixArmes.setAlignment(Pos.CENTER);

        Button valider = new Button(I18N.get("choix.valider"));
        valider.setFont(Font.font("Arial", 18));
        valider.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 10 20;");

        Button retour = new Button(I18N.get("choix.retour"));
        retour.setFont(Font.font("Arial", 14));
        retour.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        this.getChildren().addAll(titre, nomField, armeLabel, choixArmes, valider, retour);

        final String[] armeChoisie = { "Shooter" }; // ← préselection
        highlightWeapon(shooter, roller, charger);

        // Sélection et mise en avant
        shooterBox.setOnMouseClicked(e -> {
            armeChoisie[0] = "Shooter";
            highlightWeapon(shooter, roller, charger);
        });

        rollerBox.setOnMouseClicked(e -> {
            armeChoisie[0] = "Roller";
            highlightWeapon(roller, shooter, charger);
        });

        chargerBox.setOnMouseClicked(e -> {
            armeChoisie[0] = "Charger";
            highlightWeapon(charger, shooter, roller);
        });

        valider.setOnAction(e -> {
            String nom = nomField.getText().trim();
            if (!nom.isEmpty() && armeChoisie[0] != null) {
                controleur.demarrerPartie(nom, armeChoisie[0]);
            } else {
                System.out.println("⚠️ Remplis le nom et choisis une arme !");
            }
        });

        retour.setOnAction(e -> controleur.retourMenu());
    }

    /**
     * Crée une boîte graphique cliquable contenant l’image d’une arme.
     *
     * @param imageView l’ImageView représentant l’arme
     * @return un VBox centré et stylé pour l’interaction utilisateur
     */
    private VBox createWeaponBox(ImageView imageView) {
        VBox box = new VBox(imageView);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-cursor: hand;");
        return box;
    }

    /**
     * Charge une image d’arme depuis les ressources et la configure.
     *
     * @param path chemin relatif vers la ressource image (dans /assets/image)
     * @param size taille désirée (en pixels) pour la largeur et la hauteur
     * @return un ImageView JavaFX configuré (curseur main, ratio conservé)
     */
    private ImageView createWeaponImage(String path, int size) {
        ImageView view = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm()));
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        view.setStyle("-fx-cursor: hand;");
        return view;
    }

    /**
     * Met en avant l’ImageView de l’arme sélectionnée en l’agrandissant,
     * et remet les autres armes à leur taille normale.
     *
     * @param selected l’ImageView de l’arme choisie
     * @param others   les ImageViews des autres armes à réinitialiser
     */
    private void highlightWeapon(ImageView selected, ImageView... others) {
        selected.setFitWidth(180);
        selected.setFitHeight(180);
        for (ImageView iv : others) {
            iv.setFitWidth(120);
            iv.setFitHeight(120);
        }
    }

    /**
     * Affiche dans le label infoLabel les statistiques de l’arme survolée.
     *
     * @param weapon l’objet Weapon dont on veut afficher le nom et les stats
     */
    private void showStats(Weapon weapon) {
        infoLabel.setText("🔫 " + weapon.getDisplayName() + "\n" +
                I18N.get("choix.attaque") + " " + weapon.getFirePower() + "\n" +
                I18N.get("choix.vitesse") + " " + weapon.getFireRate() + "\n" +
                I18N.get("choix.portee")  + " " + weapon.getPortee());
        infoLabel.setVisible(true);
    }

    /**
     * Retourne le Label utilisé pour afficher les informations d’arme.
     *
     * @return le Label d’information (visible lors du survol)
     */
    public Label getInfoLabel() {
        return infoLabel;
    }

}
