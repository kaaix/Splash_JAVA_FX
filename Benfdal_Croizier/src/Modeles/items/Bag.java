/**
 * Implémente un sac (inventaire) pour stocker des objets avec une capacité maximale.
 * Offre des opérations pour ajouter, retirer et interroger le contenu.
 */
package Modeles.items;

import java.util.ArrayList;
import java.util.List;

public class Bag implements IContainer {

    private static final int DEFAULT_CAPACITY = 100;

    private int capacity;
    private List<Item> items;

    /**
     * Construit un sac avec la capacité spécifiée.
     *
     * @param capacity nombre maximum d’objets pouvant être stockés
     */
    public Bag(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<Item>(capacity);
    }

    /**
     * Construit un sac avec la capacité par défaut (100 éléments).
     */
    public Bag() {
        this(DEFAULT_CAPACITY);
    }


    /**
     * Tente de stocker un objet dans le sac si la capacité n’est pas atteinte.
     *
     * @param item l’objet à ajouter
     * @return true si l’objet a été ajouté, false si le sac est plein
     */
    public boolean store(Item item) {
        if(items.size() < capacity) {
            items.add(item);
            return true;
        }
        return false;
    }

    /**
     * Retire un objet du sac.
     *
     * @param item l’objet à retirer
     * @return true si l’objet était présent et a été retiré, false sinon
     */
    public boolean remove(Item item) {
        return items.remove(item);
    }

    /**
     * Vérifie si un objet est présent dans le sac.
     *
     * @param item l’objet à rechercher
     * @return true si l’objet est stocké, false sinon
     */
    public boolean contains(Item item) {
        return items.contains(item);
    }

    /**
     * Retourne la liste des objets actuellement stockés dans le sac.
     *
     * @return List<Item> représentant le contenu du sac
     */
    public List<Item> getContent() {
        return this.items;
    }


}
