import org.w3c.dom.ls.LSOutput;
import out.production.JavaZork.CombatState;

import java.io.Serializable;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public abstract class Character implements Serializable {
    protected String name;
    protected Room currentRoom;
    protected double health;
    protected ArrayList<Item> inventory;
    private boolean stunned = false;
    protected transient GameIO io;


    public Character(String name, Room startingRoom, double health) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.health = health;
        this.inventory = new ArrayList<>();

    }

    public String getName() {
        return name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public double getHealth() { return health; }

    public void setHealth(double health) {  this.health = health; }

    public double attack() {
        return 0;
    }

    public double block() {
        return 0;
    }

    public void takeDamage(double amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isStunned() {
        return stunned;
    }

    public void applyStun() {
        stunned = true;
    }

    public void clearStun() {
        stunned = false;
    }

    /*public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            System.out.println("You moved to: " + currentRoom.getDescription());
        } else {
            System.out.println("You can't go that way!");
        }
    }*/

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void showInventory() {
        System.out.println(getName() + "'s Inventory");
        for (Item item : inventory) {
            System.out.println(item.getName());
        }
    }

    public void setIO(GameIO io) {
        this.io = io;
    }
}



class NPC extends Character implements SharedUses, Serializable {
    private ArrayList<String> dialogue;
    private int dialogueIndex = 0;
    private Item requiredItem;
    private Item rewardItem;
    private boolean tradeCompleted;
    private transient GameIO io;
    private transient GUI gui;


    public NPC(String name, Room startingRoom, double health) {
        super(name, startingRoom, health);
        dialogue = new ArrayList<>();
    }

    public void addDialogue(String text) {
        dialogue.add(text);
    }

    public void setTrade(Item required, Item reward) {
            this.requiredItem = required;
            this.rewardItem = reward;
    }

    public void interact(Player player) {
        showMenu(player);
    }

    private void showMenu(Player player) {
        ArrayList<String> options = new ArrayList<>();
        options.add(dialogueIndex == 0 ? "Talk" : "Continue Talking");
        options.add("Trade Items");
        options.add("Leave");

        io.print("How will you proceed?");
        io.promptChoices(options);

        io.setChoiceListener(new GameIO.ChoiceListener() {
            @Override
            public void choiceSelected(int choice) {
                switch (choice) {
                    case 0:
                        io.print(continueDialogue());
                        showMenu(player);
                        break;
                    case 1:
                        io.print(attemptTrade(player));
                        showMenu(player);
                        break;
                    case 2:
                        io.print("Farewell " + player.getName());
                        endInteraction();
                        break;
                }
            }
        });
    }

    public String continueDialogue() {
        if (dialogue.isEmpty()) {
            return "NPC doesn't wish to speak.";
        }
        String line = dialogue.get(dialogueIndex);
        dialogueIndex = (dialogueIndex + 1) % dialogue.size();
        return line;
    }

    public String attemptTrade(Player player) {
            if (requiredItem == null || rewardItem == null) {
                return "I have nothing to trade.";
            }
            if (tradeCompleted) {
                return "We already traded.";
            }

            Item neededItem = null;
            for (Item item : player.getInventory()) {
                if (item.getName().equalsIgnoreCase(requiredItem.getName())) {
                    neededItem = item;
                    break;
                }
            }

            if (neededItem != null) {
                player.getInventory().remove(neededItem);
                player.getInventory().add(rewardItem);
                tradeCompleted = true;
                if (gui != null) {
                    gui.refreshUI();
                }

                return "I appreciate your gratitude. Here is the " + rewardItem.getName() + " as promised.";
            } else {
                return "I would like a " + requiredItem.getName() + ".";
            }
    }

    public void setIO(GameIO io) {
        this.io = io;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }


    @Override
    public double attack() {
        return 0;
    }

    @Override
    public double block() {
        return 0;
    }

    private void endInteraction() {
        if (io != null) {
            io.clearChoices();
        }
    }

}



class Enemy extends Character implements Serializable {
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



