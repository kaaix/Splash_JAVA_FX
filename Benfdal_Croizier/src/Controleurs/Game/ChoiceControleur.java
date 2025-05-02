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

    public void selectLeftDoor() {
        if (selectedDoor > 0) {
            selectedDoor--;
            gameController.updateSelection(selectedDoor);
        }
    }

    public void selectRightDoor() {
        if (selectedDoor < 2) {
            selectedDoor++;
            gameController.updateSelection(selectedDoor);
        }
    }


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

    public void initializeBonusChoices() {
        bonusChoices.clear();
        for (int i = 0; i < 3; i++) {
            bonusChoices.add(Consumable.getRandomConsumable());
        }
    }

    public int getSelectedDoor() {
        return selectedDoor;
    }

    public List<Consumable> getBonusChoices() {
        return bonusChoices;
    }

    public void confirmChoice() {
        Consumable selectedBonus = bonusChoices.get(selectedDoor);
        gameController.getModel().getHero().addConsumable(selectedBonus);
        gameController.updateFloor();
    }

    public Consumable getBonusAt(int index) {
        return bonusChoices.get(index);
    }

    public String getBonusDisplayName(int index) {
        return bonusChoices.get(index).getDisplayName();
    }

    public String getBonusDescription(int index) {
        return bonusChoices.get(index).getDescription();
    }

    public String getBonusImageName(int index) {
        return bonusChoices.get(index).getClass().getSimpleName() + ".png";
    }
}
