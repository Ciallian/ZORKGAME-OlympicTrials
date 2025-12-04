package game;

import java.io.Serializable;
import java.util.ArrayList;
import gui.GUI;

public class NPC extends GameCharacter implements SharedUses, Serializable {
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
