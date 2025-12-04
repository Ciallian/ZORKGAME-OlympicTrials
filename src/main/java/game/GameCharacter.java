package game;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class GameCharacter implements Serializable {
    protected String name;
    protected Room currentRoom;
    protected double health;
    protected ArrayList<Item> inventory;
    private boolean stunned = false;
    protected transient GameIO io;


    public GameCharacter(String name, Room startingRoom, double health) {
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




