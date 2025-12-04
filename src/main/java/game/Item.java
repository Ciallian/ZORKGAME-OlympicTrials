package game;

import java.io.Serializable;

public abstract class Item implements SharedUses, Serializable {
    private String description;
    private String name;
    private String location;
    private int id;
    private boolean isVisible;


    public Item(String name, String description) {
        this.name = name;
        this.description = description;
        this.isVisible = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public void interact(Player player) {
        System.out.println(description);
    }

}

class SearchItems extends Item implements SharedUses, Serializable {
    private String foundItem;

    public SearchItems(String name, String description, String foundItem) {
        super(name, description);
        this.foundItem = foundItem;
    }

    @Override
    public void interact(Player player) {

        Item target = findItemInRoomByName(player.getCurrentRoom(), foundItem);
        if (target == null) {
            System.out.println("Using the" + getName() + "yielded no result...");
            return;
        }

        if (!target.isVisible()) {
            target.setVisible(true);
            System.out.println("You uncover " + target.getName() + "! It was cleverly concealed.");
        } else {
            System.out.println("You've already revealed " + target.getName() + " here.");
        }
    }

    private Item findItemInRoomByName(Room room, String name) {
        for (Item item : room.getItems()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

}

class HonourItems extends Item implements SharedUses, Serializable {
    private String honourGod;
    private int honourAmount;

    public HonourItems(String name, String description, String honourGod, int honourAmount) {
        super(name, description);
        this.honourGod = honourGod;
        this.honourAmount = honourAmount;

    }

    @Override
    public void interact(Player player) {
        System.out.println(getDescription());

        if (honourGod != null);
        player.modifyHonour(honourGod, honourAmount, "Interacted with: " + getName());
    }
}

class Key extends Item implements Serializable {
    private String keyColour;

    public Key(String name, String description, String keyColour) {
        super(name, description);
        this.keyColour = keyColour;
    }
    public String getKeyColour() { return keyColour; }

}

class Weapon extends Item implements Serializable {
    private double damage;

    public Weapon(String name, String description, double damage) {
        super(name, description);
        this.damage = damage;
    }

    public double getDamage() { return damage; }

}

class Shield extends Item implements Serializable {
    private double blockAmount;

    public Shield(String name, String description, double blockAmount) {
        super(name, description);
        this.blockAmount = blockAmount;
    }

    public double getBlockAmount() { return blockAmount; }
}

class Food extends Item implements SharedUses, Serializable {
    private double healAmount;

    public Food(String name, String description, double healAmount) {
        super(name, description);
        this.healAmount = healAmount;
    }

    @Override
    public void interact(Player player) {
        double health = player.getHealth();
        if (health >= 100) {
            System.out.println("You're already at full health!");
            return;
        }
        System.out.println("You eat the " + getName());
        double healed = health + healAmount;
        if (health < 100) {
            if (healed >= 100) {
                healed = 100;
            }

            player.setHealth(healed);
            System.out.println("The " + getName() + " wamrs your bones. " +
                    "You're healed for " + healAmount + " health. Current Health: " + healed);
        }
    }
}

class GodlyGemstone extends Item {
    public GodlyGemstone(String name, String description) {
        super(name, description);
    }
}

class Papyrus extends Item implements Serializable {

    public Papyrus(String name, String description) {
        super(name, description);
    }

}

