package game;

import java.io.Serializable;
import java.util.ArrayList;

public class Enemy extends GameCharacter implements Serializable {
    private Item rewardItem;
    private double baseDamage;
    private int stunTurns = 0;


    Enemy(String name, Room startingRoom, double health, double baseDamage) {
        super(name, startingRoom, health);
        this.baseDamage = baseDamage;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    @Override
    public double attack() {
        ArrayList<Weapon> weapons = new ArrayList<>();
        for (Item item : inventory) {
            if (item instanceof Weapon) {
                weapons.add((Weapon) item);
            }
        }
        if (weapons.isEmpty()) {
            return getBaseDamage();
        }
        Weapon ranWeapon = weapons.get((int)(Math.random() * weapons.size()));
        return ranWeapon.getDamage();
    }

    @Override
    public double block() {
        ArrayList<Shield> shields = new ArrayList<>();
        for (Item item : inventory) {
            if (item instanceof Shield) {
                shields.add((Shield) item);
            }
        }
        if (shields.isEmpty()) {
            return 0;
        }
        Shield ranShield = shields.get((int)(Math.random() * shields.size()));
        return ranShield.getBlockAmount();
    }

    public void onDeath(Player player) {
        if (health <= 0) {
            System.out.println("You defeated the " + name + "!");
            player.modifyHonour("Ares", 25, "Defeated " + name);
            dropAllItems(currentRoom);
            currentRoom.removeEnemy(name);
        }
    }

    public void dropAllItems(Room room) {
        if (inventory.isEmpty()) {
            System.out.println("The " + name + " had nothing to drop.");
        } else {
            System.out.println("The " + name + " dropped items: ");
            for (Item item : inventory) {
                System.out.println(item.getName());
                room.addItem(item);
            }
        }
        inventory.clear();
    }

    public CombatState chooseAction() {
        int roll = new java.util.Random().nextInt(3);
        switch (roll) {
            case 0: return CombatState.ATTACK;
            case 1: return CombatState.BLOCK;
            case 2: return CombatState.FEINT;
            default: return CombatState.ATTACK;
        }
    }
}
