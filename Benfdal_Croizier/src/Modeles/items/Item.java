/**
 * Classe abstraite représentant un objet exploitable dans le jeu.
 * Chaque Item possède un nom affichable (displayName) et une
 * description textuelle. Les sous-classes définissent des types
 * d’objets concrets (consommables, armes, etc.).
 */
package Modeles.items;

public abstract class Item {
    
    protected String displayName;
    protected String description;

    /**
     * Construit un Item avec son nom à afficher et sa description.
     *
     * @param displayName libellé visible de l’objet
     * @param description explication ou usage de l’objet
     */
    public Item(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Retourne le nom affiché de l’objet.
     *
     * @return le displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retourne la description textuelle de l’objet.
     *
     * @return la description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Renvoie le displayName, pour un affichage lisible en listage.
     *
     * @return le nom affiché de l’objet
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Retourne le nom de la classe simple de cet objet,
     * utilisé comme identifiant de type (ex. "AttackBoost").
     *
     * @return nom de la classe (type de l’Item)
     */
    public String getName() {
        return this.getClass().getSimpleName(); // ex : AttackBoost
    }
}
