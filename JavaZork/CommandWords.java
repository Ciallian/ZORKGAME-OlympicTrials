import java.util.HashMap;
import java.util.Map;

public class CommandWords {
    private Map<String, String> validCommands;

    public CommandWords() {
        validCommands = new HashMap<>();
        validCommands.put("go", "Move to another room");
        validCommands.put("quit", "End the game");
        validCommands.put("help", "Show help");
        validCommands.put("look", "Look around");
        validCommands.put("eat", "Eat something");
        validCommands.put("inventory", "Show Inventory");
        validCommands.put("honour", "Show Honour");
        validCommands.put("health", "Show Health");
        validCommands.put("godofise", "Add's necessary winning items to inventory and teleports to the end");
        validCommands.put("take", "Picks up a specific item in the room");
        validCommands.put("drop", "Drops an item from inventory and puts it in the current room");
        validCommands.put("save", "Saves game");
        validCommands.put("load", "Loads game");
        validCommands.put("interact", "Interact with an object, NPC, or environment feature");
        validCommands.put("equip", "Equips a weapon or shield for a player");

    }

    public boolean isCommand(String commandWord) {
        return validCommands.containsKey(commandWord);
    }

    public void showAll() {
        System.out.print("Valid commands are: ");
        for (String command : validCommands.keySet()) {
            if (command.equalsIgnoreCase("godofISE")) {
                continue;
            }
            System.out.print(command + " ");
        }
        System.out.println();
    }
}
