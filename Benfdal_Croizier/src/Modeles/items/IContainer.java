/**
 * Interface générique pour un conteneur d’objets {@link Item}.
 * Définit les opérations de base : ajouter, retirer, vérifier
 * la présence et lister le contenu.
 */
package Modeles.items;

import java.util.List;

public interface IContainer {

    /**
     * Tente d’ajouter un objet dans le conteneur.
     *
     * @param item l’objet à stocker
     * @return true si l’ajout a réussi, false si le conteneur est plein ou ne peut accepter l’objet
     */
    public boolean store(Item item);

    /**
     * Retire un objet du conteneur.
     *
     * @param item l’objet à retirer
     * @return true si l’objet était présent et a été retiré, false sinon
     */
    public boolean remove(Item item);

    /**
     * Vérifie si un objet est stocké dans le conteneur.
     *
     * @param item l’objet à rechercher
     * @return true si l’objet est présent, false sinon
     */
    public boolean contains(Item item);

    /**
     * Retourne la liste des objets actuellement stockés.
     *
     * @return liste des {@link Item} dans le conteneur
     */
    public List<Item> getContent();

    /**
     * Renvoie les identifiants (toString) de tous les objets stockés.
     *
     * @return liste des chaînes correspondant à chaque Item
     */
    default List<String> getAllItemIds() {
        return getContent().stream().map(Object::toString).toList();
    }


}
