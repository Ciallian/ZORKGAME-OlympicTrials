package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room implements Serializable {
    private String description;
    private Map<String, Room> exits;
    private Map<String, Storage<? extends Item>> storages;
    private Map<String, NPC> npcs;
    private Map<String, Enemy> enemies;
    private ArrayList<Item> items;
    private boolean isVisible;

    public Room(String description, boolean isVisible) {
        this.description = description;
        this.exits = new HashMap<>();
        this.storages = new HashMap<>();
        this.npcs = new HashMap<>();
        this.enemies = new HashMap<>();
        this.items = new ArrayList<>();
        this.isVisible = isVisible;
    }

    public boolean isVisible() { return isVisible; }

    public void setVisible(boolean visible) { this.isVisible = visible; }

    public String getDescription() {
        return description;
    }

    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public Room getExit(String direction) {
        Room next = exits.get(direction);
        return (next != null && next.isVisible()) ? next : null;
    }

    public String getExitString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Room> entry : exits.entrySet()) {
            if (entry.getValue().isVisible()) {
                sb.append(entry.getKey()).append(" ");
            }
        }
        return sb.toString().trim();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void look() {
        boolean hasVisibleItems = false;
        for (Item item : items) {
            if (item.isVisible()) {
                hasVisibleItems = !items.isEmpty();
                break;
            }
        }
        boolean hasStorages = !storages.isEmpty();
        boolean hasNPCs = !npcs.isEmpty();


        System.out.println(getLongDescription());

        if (!hasVisibleItems && !hasStorages && !hasNPCs) {
            System.out.println("The room is empty, nothing catches your eye.");
            return;
        }

        // NPCs
        if (hasNPCs) {
            System.out.println("You see people:");
            for (NPC npc : npcs.values()) {
                System.out.println(" > " + npc.getName());
            }
        } else {
            System.out.println("Nobody else is here.");
        }

        // Items
        if (hasVisibleItems) {
            System.out.println("You look around and see these items:");
            for (Item item : items) {
                if (item.isVisible()) {
                    System.out.println(" > " + item.getName());
                }
            }
        } else {
            System.out.println("No items lying around here.");
        }

        // Storages
        if (hasStorages) {
            System.out.println("You also notice these storage objects:");
            for (String name : storages.keySet()) {
                System.out.println(" > " + storages.get(name).getName());
            }
        } else {
            System.out.println("No storages here.");
        }

    }


    public void addStorage(String name, Storage storage) {
        storages.put(name.toLowerCase(), storage);
    }

    public Storage getStorage(String name) {
        return storages.get(name.toLowerCase());
    }

    public Map<String, Storage<? extends Item>> getStorages() {
        return storages;
    }

    /*public void showStorages() {
        if (storages.isEmpty()) {
            System.out.println("No storages here.");
        } else {
            System.out.println("You see:");
            for (String name : storages.keySet()) {
                System.out.println("- " + name);
            }
        }
    }*/

    public void addItem(Item item) {
        items.add(item);
    }

    public void addNPC(NPC npc) {
        npcs.put(npc.getName().toLowerCase(), npc);
    }

    public NPC getNPC(String name) {
        return npcs.get(name.toLowerCase());
    }

    public Map<String, NPC> getNPCs() {
        return npcs;
    }

    public void addEnemy(Enemy enemy) {
        enemies.put(enemy.getName().toLowerCase(), enemy);
    }

    public Map<String, Enemy> getEnemies() {
        return enemies;
    }

    public Enemy getOnlyEnemy() {
        String name = enemies.keySet().iterator().next(); //Next returns first key in iterator
        return enemies.get(name);
    }


    public void removeEnemy(String name) {
        enemies.remove(name.toLowerCase());
    }

    public boolean hasEnemies() {
        return !enemies.isEmpty();
    }

    public String getLongDescription() {
        return "You are in " + description + ".\nExits: " + getExitString();
    }
}














