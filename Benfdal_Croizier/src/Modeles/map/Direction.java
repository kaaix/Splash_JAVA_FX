/**
 * Énumération des directions cardinales utilisées pour la navigation.
 * Les valeurs possibles sont NORTH, EAST, SOUTH et WEST.
 */
package Modeles.map;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    /**
     * Retourne la direction opposée à la direction donnée.
     *
     * @param dir la direction dont on veut l’opposé
     * @return NORTH si dir est SOUTH, EAST si dir est WEST, etc.
     */
    public static Direction opposite(Direction dir) {
        switch(dir) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            default: return SOUTH;
        }
    }
}

