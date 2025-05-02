/**
 * Retourne soit directement au menu principal si aucune sauvegarde
 * n’existe, soit au menu de sélection de sauvegarde.
 * Fait apparaître la vue correspondante avec une transition animée.
 */
package Controleurs.Game;

import Modeles.items.consumables.Consumable;
import javafx.scene.input.KeyCode;
import Modeles.settings.SettingsModel;

import java.util.*;

public class ChoiceControleur {

    private final GameControleur gameController;
    private int selectedDoor = 1; // 0 = gauche, 1 = milieu, 2 = droite
    private final Map<String, KeyCode> keyBindings = new HashMap<>();
    private List<Consumable> bonusChoices = new ArrayList<>();

    /**
     * Initialise le contrôleur de choix de porte pour une partie.
     * Charge les bindings clavier depuis les paramètres et prépare
     * la liste de bonus disponibles.
     *
     * @param gameController le GameControleur maître à notifier
     */
    public ChoiceControleur(GameControleur gameController) {
        this.gameController = gameController;

        // 1) Bindings par défaut pour les flèches
        keyBindings.put("moveUp",    KeyCode.UP);
        keyBindings.put("moveDown",  KeyCode.DOWN);
        keyBindings.put("moveLeft",  KeyCode.LEFT);
        keyBindings.put("moveRight", KeyCode.RIGHT);

        // 2) Chargement de la config utilisateur
        SettingsModel.load().getTouches().forEach((action, keyName) -> {
            try {
                KeyCode code = KeyCode.valueOf(keyName.toUpperCase(Locale.ROOT));
                keyBindings.put(action, code);
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Touche invalide dans settings.conf : " + keyName);
            }
        });


    }

    /**
     * Déplace la sélection d’une porte vers la gauche,
     * si possible, et notifie le GameControleur du changement.
     */
    public void selectLeftDoor() {
        if (selectedDoor > 0) {
            selectedDoor--;
            gameController.updateSelection(selectedDoor);
        }
    }

    /**
     * Déplace la sélection d’une porte vers la droite,
     * si possible, et notifie le GameControleur du changement.
     */
    public void selectRightDoor() {
        if (selectedDoor < 2) {
            selectedDoor++;
            gameController.updateSelection(selectedDoor);
        }
    }


    /**
     * Traite un appui clavier pour changer de porte
     * selon les flèches ou les bindings utilisateur.
     *
     * @param code le KeyCode reçu
     */
    public void handleKeyPressed(KeyCode code) {
        // 1) Gestion directe des flèches
        if (code == KeyCode.LEFT)  { selectLeftDoor();  return; }
        if (code == KeyCode.RIGHT) { selectRightDoor(); return; }

        // 2) Via bindings custom
        KeyCode up    = keyBindings.get("moveUp");
        KeyCode down  = keyBindings.get("moveDown");
        KeyCode left  = keyBindings.get("moveLeft");
        KeyCode right = keyBindings.get("moveRight");

        if (code == left)       selectLeftDoor();
        else if (code == right) selectRightDoor();
    }

    /**
     * Génère aléatoirement une liste de 3 bonus
     * pour la sélection à venir.
     */
    public void initializeBonusChoices() {
        bonusChoices.clear();
        for (int i = 0; i < 3; i++) {
            bonusChoices.add(Consumable.getRandomConsumable());
        }
    }

    /**
     * Retourne l’indice de la porte actuellement sélectionnée.
     *
     * @return 0=gauche, 1=milieu, 2=droite
     */
    public int getSelectedDoor() {
        return selectedDoor;
    }

    /**
     * Fournit la liste des bonus générés pour cette sélection.
     *
     * @return liste de 3 Consumable
     */
    public List<Consumable> getBonusChoices() {
        return bonusChoices;
    }

    /**
     * Valide le bonus choisi et l’ajoute à l’inventaire du héros,
     * puis passe à l’étage suivant via le GameControleur.
     */
    public void confirmChoice() {
        Consumable selectedBonus = bonusChoices.get(selectedDoor);
        gameController.getModel().getHero().addConsumable(selectedBonus);
        gameController.updateFloor();
    }

    /**
     * Récupère le bonus à l’indice donné.
     *
     * @param index indice du bonus (0–2)
     * @return le Consumable sélectionné
     */
    public Consumable getBonusAt(int index) {
        return bonusChoices.get(index);
    }

    /**
     * Retourne le nom à afficher pour le bonus à l’indice donné.
     *
     * @param index indice du bonus (0–2)
     * @return libellé localisé du bonus
     */
    public String getBonusDisplayName(int index) {
        return bonusChoices.get(index).getDisplayName();
    }

    /**
     * Fournit la description détaillée du bonus à l’indice donné.
     *
     * @param index indice du bonus (0–2)
     * @return description localisée du bonus
     */
    public String getBonusDescription(int index) {
        return bonusChoices.get(index).getDescription();
    }

    /**
     * Construit le nom de fichier de l’icône à charger
     * pour le bonus à l’indice donné.
     *
     * @param index indice du bonus (0–2)
     * @return nom de fichier image (ex. "SpeedBoost.png")
     */
    public String getBonusImageName(int index) {
        return bonusChoices.get(index).getClass().getSimpleName() + ".png";
    }
}
