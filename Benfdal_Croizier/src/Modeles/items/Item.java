package Modeles.items;

public abstract class Item {
    
    protected String displayName;
    protected String description;

    public Item(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }


    public String getName() {
        return this.getClass().getSimpleName(); // ex : AttackBoost
    }
}
