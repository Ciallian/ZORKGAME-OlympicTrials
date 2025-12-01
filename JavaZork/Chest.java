//import javafx.concurrent.ScheduledService;

import java.io.Serializable;

public class Chest extends Storage implements SharedUses, Serializable {
    private boolean locked;
    private String chestColour;


    public Chest(String name, boolean locked, String chestColour) {
        super(name);
        this.locked = locked;
        this.chestColour = chestColour;
    }

    public String getName() {
        return name;
    }

    public String getChestColour() {
        return chestColour;
    }

    public boolean isLocked() {
        return locked;
    }

    public void unlock() {
        if (!locked) {
            System.out.println(name + " is already unlocked.");
        } else {
            locked = false;
            System.out.println(name + " has been unlocked!");
        }
    }

    @Override
    public void open() {
        System.out.println("You open the " + name + " and see:");
        showItems();
        setOpen(true);
    }

    @Override
    public void interact(Player player) {
        if (!locked) {
            open();
            return;
        }

        for (Item item : player.getInventory()) {
            if (item instanceof Key key && key.getKeyColour().equalsIgnoreCase(chestColour)) {
                unlock();
                int placeHolder = player.getInventory().indexOf(key);
                player.getInventory().remove(placeHolder);
                return;
            }
        }
        System.out.println("The key doesn't fit... maybe they need to match.");
    }
}
