package Modeles.map;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

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

