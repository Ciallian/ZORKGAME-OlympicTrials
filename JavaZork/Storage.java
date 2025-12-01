import java.util.ArrayList;
import java.io.Serializable;

public abstract class Storage {
    protected String name;
    protected ArrayList<Item> items;
    protected boolean isOpen = false;

    public Storage(String name) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void showItems() {
        if (items.isEmpty()) {
            System.out.println("No items here.");
        } else {
            System.out.println("Items:");
            for (Item item : items) {
                System.out.println("- " + item.getName());
            }
        }
    }

    public String getName() {
        return name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public abstract void open();


}

class Shelves extends Storage implements SharedUses, Serializable {

    public Shelves(String name) {
        super(name);
    }

    @Override
    public void open() {
        System.out.println("You remove the covering on the shelves " + name + " and see:");
        showItems();
        setOpen(true);
    }

    @Override
    public void interact(Player player) {
        open();
        return;
    }
}

class Pithos extends Storage implements SharedUses, Serializable {

    public Pithos(String name) {
        super(name);
    }

    @Override
    public void open() {
        System.out.println("You smash open the " + name + " and see:");
        showItems();
        setOpen(true);
    }

    @Override
    public void interact(Player player) {
        open();
        return;
    }
}

class Basket extends Storage implements SharedUses {

    public Basket(String name) {
        super(name);
    }

    @Override
    public void open() {
        System.out.println("You lift the lid off the " + name + " and see:");
        showItems();
    }

    @Override
    public void interact(Player player) {
        open();
        return;
    }
}



