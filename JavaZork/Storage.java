import java.util.ArrayList;
import java.io.Serializable;

public abstract class Storage<T extends Item> implements SharedUses, Serializable {
    protected String name;
    protected ArrayList<T> items;
    protected boolean isOpen = false;

    public Storage(String name) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public void addItem(T item) {
        items.add(item);
    }

    public ArrayList<T> getItems() {
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
            for (T item : items) {
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

class Shelves extends Storage<Papyrus> implements SharedUses, Serializable {

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

class Pithos extends Storage<Item> implements SharedUses, Serializable {

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

class Basket extends Storage<Food> implements SharedUses, Serializable {

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

class Crate extends Storage<Shield> implements SharedUses, Serializable {

    public Crate(String name) {
        super(name);
    }

    @Override
    public void open() {
        System.out.println("You force open the crate " + name + " and see:");
        showItems();
        setOpen(true);
    }

    @Override
    public void interact(Player player) {
        open();
        return;
    }
}

class WeaponRack extends Storage<Weapon> implements SharedUses, Serializable {

    public WeaponRack(String name) {
        super(name);
    }

    @Override
    public void open() {
        System.out.println("You examine the  " + name + " and see:");
        showItems();
        setOpen(true);
    }

    @Override
    public void interact(Player player) {
        open();
        return;
    }
}

