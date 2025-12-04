package game;

import java.io.Serializable;
import java.util.HashMap;

public class Player extends GameCharacter implements Serializable {
    private HashMap<String, Integer> honour;
    private Weapon equippedWeapon;
    private Shield equippedShield;
    private double baseDamage;

    Player(String name, Room startingRoom, double health, double baseDamage) {
        super(name, startingRoom, health);
        this.baseDamage = baseDamage;
        this.honour = new HashMap<>();
        initializeHonour();

    }

    public void pickup(Item item, Room room) {
        boolean removed = false;
        if (room.getItems().contains(item)) {
            room.getItems().remove(item);
            removed = true;
        } else {
            for (Storage storage : room.getStorages().values()) {
                if (storage.isOpen && storage.getItems().contains(item)) {
                    storage.getItems().remove(item);
                    removed = true;
                    break;
                }
            }
        }
        if (removed) {
            inventory.add(item);
            System.out.println("Picked up: " + item.getName());
        } else {
            System.out.println("Item not found here.");
        }
    }

    public void dropItem(Item item, Room room) {
        inventory.remove(item);
        room.getItems().add(item);
        System.out.println("Dropped: " + item.getName());
    }

    private void initializeHonour() {
        honour.put("Zeus", 0);
        honour.put("Ares", 0);
        honour.put("Poseidon", 0);
        honour.put("Athena", 0);
    }

    public HashMap<String, Integer> getHonour() {
        return honour;
    }

    public void addHonour(String god, int amount) {
        honour.put(god, honour.get(god) + amount);
    }

    public int getHonour(String god) {
        return honour.get(god);
    }

    public void showHonour() {
        System.out.println("    HONOUR STATS ");
        for (String god : getHonour().keySet()) {
            int value = getHonour().get(god);
            System.out.println(god + ": " + value);
        }
    }

    public void modifyHonour(String god, int amount, String reason) {
        addHonour(god, amount);
        System.out.println("Honour with " + god + " changed by " + amount + " (" + reason + ")");
    }

    public void equipWeapon(Weapon weapon) {
        if (equippedWeapon != null) {
            System.out.println("You unequip " + equippedWeapon.getName() + ".");
        }

        equippedWeapon = weapon;
    }

    public void equipShield(Shield shield) {
        if (equippedShield != null) {
            System.out.println("You unequip " + equippedShield.getName() + ".");
        }

        equippedShield= shield;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Shield getEquippedShield() {
        return equippedShield;
    }

    @Override
    public double attack() {
        double buffs = 1;

        if (equippedWeapon == null) {
            System.out.println("You reach for your weapon... and realize you never equipped one.");
            System.out.println("You try punching...");
            return baseDamage;
        }

        if (getHonour("Ares") >= 50) {
            buffs += 0.5;
        }
        return equippedWeapon.getDamage() * buffs;
    }

    @Override
    public double block() {
        if (equippedShield == null) {
            System.out.println("You reach for your shield... and realize you never equipped one.");
            return 0;
        }
        return equippedShield.getBlockAmount();
    }

    public int checkGems () {
        int counter = 0;
        for (Item item : inventory) {
            if (item instanceof GodlyGemstone) {
                counter++;
            }
        }
        return counter;
    }

    public void showHealth() {
        System.out.println("Current Health: " + health);
    }
}
