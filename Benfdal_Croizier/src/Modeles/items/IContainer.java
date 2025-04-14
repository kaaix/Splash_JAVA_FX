package Modeles.items;

import java.util.List;

public interface IContainer {
    
    public boolean store(Item item);
    public boolean remove(Item item);
    public boolean contains(Item item);
    public List<Item> getContent();

}
