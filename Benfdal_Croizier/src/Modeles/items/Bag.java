package Modeles.items;

import java.util.ArrayList;
import java.util.List;

public class Bag implements IContainer {

    private static final int DEFAULT_CAPACITY = 100;

    private int capacity;
    private List<Item> items;

    public Bag(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<Item>(capacity);
    }

    public Bag() {
        this(DEFAULT_CAPACITY);
    }


    public boolean store(Item item) {
        if(items.size() < capacity) {
            items.add(item);
            return true;
        }
        return false;
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }

    public List<Item> getContent() {
        return this.items;
    }


}
