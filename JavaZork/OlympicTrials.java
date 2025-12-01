/* This game is a classic text-based adventure set in a university environment.
   The player starts outside the main entrance and can navigate through different rooms like a 
   lecture theatre, campus pub, computing lab, and admin office using simple text commands (e.g., "go east", "go west").
    The game provides descriptions of each location and lists possible exits.

Key features include:
Room navigation: Moving among interconnected rooms with named exits.
Simple command parser: Recognizes a limited set of commands like "go", "help", and "quit".
Player character: Tracks current location and handles moving between rooms.
Text descriptions: Provides immersive text output describing the player's surroundings and available options.
Help system: Lists valid commands to guide the player.
Overall, it recreates the classic Zork interactive fiction experience with a university-themed setting, 
emphasizing exploration and simple command-driven gameplay
*/

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OlympicTrials {
    private Parser parser;
    private Player player;
    private boolean olympusUnlocked = false;
    private RoomChange roomChange;
    private CombatChecker combatChecker;



    public OlympicTrials() {
        createRooms();
        parser = new Parser();
    }

    private Room cloudstep, zeus, balcony, secret, demeter, athena, study, ares, warRoom, haven, poseidon, midnight, midday, hera, olympus;
    private List<Room> allRooms = new ArrayList<>();

    private void createRooms() {
        // Create Rooms
        cloudstep = new Room("Cloudstep Plateau: A bright mystical place where you feel as if you're standing on a cloud", true);
        zeus = new Room("The Hall of Zeus: Grand marble pillars rise to the heavens, lightning crackles in the air", true);
        balcony = new Room("Overlooking Balcony: Views as far as the eye can see", true);
        secret = new Room("Secret Exit: A hidden path shrouded in mystery", false);
        demeter = new Room("Garden of Demeter: Lush greenery and golden wheat sway in a warm divine breeze", true);
        athena = new Room("Library of Athena: Shelves of endless wisdom, scrolls glowing faintly with divine light", true);
        study = new Room("Athena's Study: Quiet and filled with ancient scrolls", true);
        ares = new Room("Ares Battlefield: The ground trembles with echoes of clashing steel and roaring warriors", true);
        warRoom = new Room("War Room: Maps, weapons, and strategy marks cover the walls", true);
        haven = new Room("Traveller's Haven: A cozy inn for weary souls crossing divine lands", true);
        poseidon = new Room("Poseidon's Palace: Crystal walls ripple with the rhythm of the ocean", false);
        midnight = new Room("Midnight Moon: A tranquil courtyard bathed in silver moonlight", true);
        midday = new Room("Midday Morning: Sunbeams illuminate golden clouds in perfect harmony", true);
        hera = new Room("Hera’s Wrath: A storm of divine fury, lightning laced with vengeance", true);
        olympus = new Room("Olympus: You made it", false);

        allRooms.add(cloudstep);
        allRooms.add(zeus);
        allRooms.add(balcony);
        allRooms.add(secret);
        allRooms.add(demeter);
        allRooms.add(athena);
        allRooms.add(study);
        allRooms.add(ares);
        allRooms.add(warRoom);
        allRooms.add(haven);
        allRooms.add(poseidon);
        allRooms.add(midnight);
        allRooms.add(midday);
        allRooms.add(hera);
        allRooms.add(olympus);


        // ITEMS and SUBCLASSES
        Food ambrosia = new Food("Ambrosia big fat poo", "Food of the gods", 20);
        Food nectar = new Food("Nectar", "Brew of the gods", 20);
        Food potion1 = new Food("Red Potion", "Weak healing potion", 10);
        Food potion2 = new Food("Blue Potion", "Moderate healing potion", 25);
        Food potion3 = new Food("Green Potion", "Strong healing potion", 45);
        Food potion4 = new Food("Golden Potion", "Very strong healing potion", 75);
        Food potion5 = new Food("Elixir of Life", "Ultimate healing potion", 100);
        Item introscroll = new Item("Helpful Scroll",
                "You have been chosen by the gods as the lucky mortal.\n" +
                        "Here you may achieve godhood if you prove your worth.\n" +
                        "Complete these trials and ascend to your throne on Olympus!");
        Item magicalFlower = new Item("Magical Flower", "A glowing flower that heals the weary.");
        Key goldenKey = new Key("Key", "Unlocks divine secrets", "Golden");
        GodlyGemstone rubyGem = new GodlyGemstone("Ruby Gem", "A gem pulsing with fiery divine energy");
        GodlyGemstone sapphireGem = new GodlyGemstone("Sapphire Gem", "A gem infused with oceanic power");
        GodlyGemstone emeraldGem = new GodlyGemstone("Emerald Gem", "A gem blessed by nature spirits");
        GodlyGemstone celestialGold = new GodlyGemstone("Celestial Gold", "Metal touched by the gods");
        GodlyGemstone lapizLazuli = new GodlyGemstone("Lapis Lazuli", "A dark relic of underworld power");
        Key warKey = new Key("War Key", "A heavy iron key stained with blood", "Iron");
        Key tideKey = new Key("Tide Key", "A key shaped like a wave crest", "Ocean Blue");
        Key natureKey = new Key("Nature Key", "A wooden key covered in moss", "Nature");
        Key divineKey = new Key("Divine Key", "A key glowing with holy energy", "Divine");
        Key shadowKey = new Key("Shadow Key", "A cold black key humming with power", "Shadow");




        // WEAPONS & SHIELDS
        Weapon dagger = new Weapon("Rusty Dagger", "Old, barely sharp dagger", 5);
        Weapon ironSword = new Weapon("Iron Sword", "Standard iron sword", 15);
        Weapon shinySword = new Weapon("Shiny Sword", "Gleaming weapon of the gods", 25);
        Weapon warAxe = new Weapon("War Axe", "Heavy axe for serious battles", 35);
        Weapon divineSpear = new Weapon("Divine Spear", "Legendary spear, strikes with precision", 50);
        Shield woodenShield = new Shield("Wooden Shield", "Basic wooden shield", 5);
        Shield ironShield = new Shield("Iron Shield", "Solid shield for protection", 15);
        Shield goldenShield = new Shield("Golden Shield", "Bright golden shield", 25);
        Shield battleShield = new Shield("Battle Shield", "Reinforced for war", 35);
        Shield divineAegis = new Shield("Divine Aegis", "Shield of the gods", 70);



        //STORAGES
        Pithos pithos1 = new Pithos("Clay Pithos");
        Pithos pithos2 = new Pithos("Brown Pithos");
        pithos1.addItem(introscroll);
        pithos2.addItem(goldenKey);
        pithos2.addItem(ambrosia);
        pithos2.addItem(nectar);
        Chest silverChest = new Chest("Silver Chest", true, "Silver");
        silverChest.addItem(potion2);
        silverChest.addItem(celestialGold);
        Chest goldenChest = new Chest("Golden Chest", true, "Golden");
        goldenChest.addItem(potion4);
        goldenChest.addItem(shinySword);
        Chest secretChest = new Chest("Ancient Hidden Chest", false, "Divine");
        secretChest.addItem(potion5);
        secretChest.addItem(divineSpear);
        secretChest.addItem(celestialGold);
        Chest libraryChest = new Chest("Golden Library Chest", true, "Golden");
        libraryChest.addItem(goldenShield);
        Chest shadowChest = new Chest("Obsidian Chest", true, "Shadow");
        shadowChest.addItem(lapizLazuli);
        Chest gardenChest = new Chest("Rootbound Chest", true, "Nature");
        gardenChest.addItem(emeraldGem);
        Chest tideChest = new Chest("Coral Chest", true, "Ocean Blue");
        tideChest.addItem(sapphireGem);
        Chest warChest = new Chest("Bloodstained War Chest", true, "Iron");
        warChest.addItem(rubyGem);





        // NPCs
        NPC nymph = new NPC("Nymph of Cloudstep", cloudstep, 50);
        nymph.addDialogue("Greetings, traveler! The clouds are restless today.");
        nymph.addDialogue("I sense a burden on your shoulders...");
        nymph.addDialogue("Take this Magical Flower. May it aid you on your journey!");
        nymph.setTrade(introscroll, magicalFlower);

        NPC innkeeper = new NPC("Innkeeper", haven, 50);
        innkeeper.addDialogue("Rest your bones, traveler."); innkeeper.addDialogue("Dark things stir beneath Poseidon's halls...");
        innkeeper.setTrade(potion2, potion3); // trade Blue Potion for Green Potion

        NPC oracle = new NPC("Sylpha the Wind Oracle", cloudstep, 70);
        oracle.addDialogue("The winds whisper your fate, mortal...");
        oracle.addDialogue("Seek honour, for the gods are watching.");

        NPC scholar = new NPC("Old Scholar Theron", athena, 60);
        scholar.addDialogue("Knowledge is sharper than any blade.");
        scholar.addDialogue("The Golden Chest hides more than gold.");


        NPC veteran = new NPC("Broken Veteran", ares, 40);
        veteran.addDialogue("War never leaves you...");
        veteran.addDialogue("I once earned Ares' favour. It cost me everything.");




        // ENEMIES
        Enemy hydra = new Enemy("Hydra", demeter, 200, 3);
        hydra.addItem(divineAegis);
        hydra.addItem(dagger);
        Enemy wolf = new Enemy("Hydra", ares, 100, 20);
        hydra.addItem(divineAegis);
        hydra.addItem(dagger);



// ROOM FUNCTIONS

// cloudstep
        cloudstep.setExit("north", zeus);
        cloudstep.addItem(dagger);
        cloudstep.addItem(woodenShield);
        cloudstep.addStorage("Clay Pithos", pithos1);
        cloudstep.addNPC(nymph);
        cloudstep.addNPC(oracle);

// zeus
        zeus.setExit("south", cloudstep);
        zeus.setExit("north", balcony);
        zeus.setExit("east", athena);
        zeus.setExit("west", demeter);
        zeus.addStorage("Brown Pithos", pithos2);
        zeus.addStorage("Silver Chest", silverChest);
        zeus.addItem(rubyGem);
        zeus.addItem(divineSpear);


// balcony
        balcony.setExit("south", zeus);
        balcony.setExit("west", secret);
        balcony.addItem(natureKey);

// secret
        secret.setExit("east", balcony);
        secret.addStorage("Ancient Hidden Chest", secretChest);


// demeter
        demeter.setExit("east", zeus);
        demeter.setExit("north", ares);
        demeter.addEnemy(hydra);
        demeter.addStorage(gardenChest.getName(), gardenChest);

// athena
        athena.setExit("west", zeus);
        athena.setExit("north", haven);
        athena.setExit("east", study);
        athena.addStorage("Golden Chest", goldenChest);
        athena.addStorage("Library Chest", libraryChest);
        athena.addNPC(scholar);


// study
        study.setExit("west", athena);

// haven
        haven.setExit("south", athena);
        haven.setExit("east", poseidon);
        haven.setExit("north", midnight);
        haven.addNPC(innkeeper);

// poseidon
        poseidon.setExit("west", haven);
        poseidon.addStorage("coral chest", tideChest);

// ares
        ares.setExit("north", midday);
        ares.setExit("south", demeter);
        ares.setExit("west", warRoom);
        ares.addNPC(veteran);
        ares.addStorage("war chest", warChest);

// warRoom
        warRoom.setExit("east", ares);

// midnight
        midnight.setExit("south", haven);
        midnight.setExit("north", hera);
        midnight.addStorage("obsidian chest", shadowChest);

// midday
        midday.setExit("east", hera);
        midday.setExit("south", ares);

// hera
        hera.setExit("south", midnight);
        hera.setExit("west", midday);
        hera.setExit("north", olympus);

        // PLAYER
        player = new Player("Sigma", cloudstep, 1000, 2);

    }
    public Player getPlayer() {
        return player;
    }

    public void play() {
        printWelcome();
        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing. Goodbye.");
    }

    public void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the Trials of Olympus!");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
    }


    public boolean processCommand(Command command) {
        String commandWord = command.getCommandWord();

        if (commandWord == null) {
            System.out.println("I don't understand your command...");
            return false;
        }

        switch (commandWord) {
            case "help":
                printHelp();
                break;
            case "go":
                goRoom(command);
                checkHonourEvents();
                checkOlympusUnlock();
                break;
            case "quit":
                if (command.hasSecondWord()) {
                    System.out.println("Quit what?");
                    return false;
                } else {
                    return true; // signal to quit
                }
            case "inventory":
                player.showInventory();
                break;
            case "look":
                player.getCurrentRoom().look();
                break;
            case "take":
                takeItem(command);
                checkHonourEvents();
                break;
            case "drop":
                dropItem(command);
                break;
            case "save":
                saveGame(player.getName());
                break;
            case "load":
                loadGame(player.getName());
                break;
            case "interact":
                interact(command);
                checkHonourEvents();
                break;
            case "equip":
                equip(command);
                break;
            case "honour":
                player.showHonour();
                checkHonourEvents();
                break;
            case "health":
                player.showHealth();
                break;
            case "godofise":
                cheatEnd();
                break;
            default:
                System.out.println("I don't know what you mean...");
                break;
        }
        return false;
    }

    private void printHelp() {
        System.out.println("You've woken on Mount Olympus. The \"Gods\" are watching, you must please them to live. \n(Hint: Look makes the dropdown useful)");
        parser.showCommands();
    }

    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();
        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else if (nextRoom == olympus && olympusUnlocked) {
            System.out.println("The Gates of Olympus open before you...");
            System.out.println("The gods acknowledge your worth.");
            System.out.println("You ascend to your throne and into legend.");
            System.out.println(" YOU HAVE WON THE GAME ");
            System.exit(0);
        } else {
            Room previousRoom = player.getCurrentRoom();
            player.setCurrentRoom(nextRoom);
            System.out.println(player.getCurrentRoom().getLongDescription());
            if (roomChange != null) {
                roomChange.onRoomChanged(nextRoom, previousRoom);
            }
            if (nextRoom.hasEnemies() && combatChecker != null) {
                Enemy enemy = nextRoom.getOnlyEnemy();
                combatChecker.onEnemyEncountered(enemy, previousRoom);
            }
        }
    }

    public void setRoomChange(RoomChange listener) {
        this.roomChange = listener;
    }

    public List<Room> getAllRooms() {
        return allRooms;
    }

    private void takeItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Take What?");
            return;
        }

        String itemName = command.getSecondWord();
        Room currentRoom = player.getCurrentRoom();
        Item isItemThere = null;

        for (Item item : currentRoom.getItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                isItemThere = item;
                break;
            }
        }

        if (isItemThere == null) {
            for (Storage storage : currentRoom.getStorages().values()) {
                if (storage.isOpen) {
                    for (Item item : storage.getItems()) {
                        if (item.getName().equalsIgnoreCase(itemName)) {
                            isItemThere = item;
                            break;
                        }
                    }
                }
                if (isItemThere != null) break;
            }
        }

        if (isItemThere == null) {
            for (Storage storage : currentRoom.getStorages().values()) {
                if (!storage.isOpen()) {
                    for (Item item : storage.getItems()) {
                        if (item.getName().equalsIgnoreCase(itemName)) {
                            System.out.println("There may be a " + itemName + " somehwere in the room. The " + storage.getName() + " is closed. Maybe you need to open it first.");
                            return;
                        }
                    }
                }
            }
            System.out.println("That item isn't here!");
        } else {
            player.pickup(isItemThere, player.getCurrentRoom());
            player.modifyHonour("Poseidon", 60, "Bold");
            System.out.println("You picked up the " + itemName + "!");
        }
    }

    private void dropItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Drop What?");
            return;
        }

        String itemName = command.getSecondWord();
        Item isItemThere = null;

        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                isItemThere = item;
                break;
            }
        }

        if (isItemThere == null) {
            System.out.println("That item isn't in your Inventory!");
        } else {
            player.dropItem(isItemThere, player.getCurrentRoom());
            System.out.println("You dropped the " + itemName + "!");
        }
    }

    private void equip(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Equip what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item itemToEquip = null;

        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                itemToEquip = item;
                break;
            }
        }

        if (itemToEquip == null) {
            System.out.println("You don't have that item.");
            return;
        }

        if (itemToEquip instanceof Weapon w) {
            player.equipWeapon(w);
            System.out.println("You equipped the " + w.getName() + ".");
        }
        else if (itemToEquip instanceof Shield s) {
            player.equipShield(s);
            System.out.println("You equipped the " + s.getName() + ".");
        }
        else {
            System.out.println("You can't equip that!");
        }
    }


    private void interact(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Interact with what?");
            return;
        }

        String targetName = command.getSecondWord();
        Room currentRoom = player.getCurrentRoom();

        //NPCs
        NPC npc = currentRoom.getNPC(targetName);
        if (npc instanceof SharedUses interactable) {
            interactable.interact(player);
            return;
        }

        //Storages
        Storage storage = currentRoom.getStorage(targetName);
        if (storage instanceof SharedUses interactable) {
            interactable.interact(player);
            return;
        }

        //Items (Room and Player)
        for (Item item : currentRoom.getItems()) {
            if (item instanceof SharedUses interactable && item.getName().equalsIgnoreCase(targetName)) {
                interactable.interact(player);
                return;
            }
        }

        for (Item item : player.getInventory()) {
            if (item instanceof SharedUses interactable && item.getName().equalsIgnoreCase(targetName)) {
                interactable.interact(player);
                if (item instanceof Food && item.getName().equalsIgnoreCase(targetName))
                    player.getInventory().remove(item);
                return;
            }
        }

        System.out.println("You can’t interact with that right now.");
    }

    /*public void startCombat(Enemy enemy, Room previousRoom) {
        Scanner input = new Scanner(System.in);
        CombatState playerState = null;
        CombatState enemyState = null;
        boolean inCombat = true;

        System.out.println("You are ambushed by a " + enemy.getName() + "!");

        while (inCombat) {

            System.out.println("\nXXXXX COMBAT XXXXX ");
            System.out.println("Your Health: " + player.getHealth());
            System.out.println(enemy.getName() + " Health: " + enemy.getHealth());

            // Player Chooses action
                System.out.println("Choose your action:");
                System.out.println("1) Attack");
                System.out.println("2) Block");
                System.out.println("3) Feint");
                System.out.println("4) Run");

                String choice = input.nextLine();

                switch (choice) {
                    case "1":
                        playerState = CombatState.ATTACK;
                        break;
                    case "2":
                        playerState = CombatState.BLOCK;
                        break;
                    case "3":
                        playerState = CombatState.FEINT;
                        break;
                    case "4":
                        playerState = CombatState.RUN;
                        break;
                    default:
                        System.out.println("You hesitate...");
                        player.applyStun();
                        playerState = CombatState.STUN; //prevents state from becoming null
                        break;
                }

            // Enemy Chooses Action
            if (enemy.isStunned()) {
                System.out.println(enemy.getName() + " is stunned and cannot act this turn!");
                enemy.clearStun();
                enemyState = CombatState.STUN;
            } else {
                enemyState = enemy.chooseAction();
            }


            // Player does action
                switch (playerState) {
                    case ATTACK:
                        double damage = player.attack();
                        if (enemyState == CombatState.BLOCK) {
                            double blocked = enemy.block();
                            if (damage <= blocked + 20) {
                                System.out.println(enemy.getName() + " perfectly blocks your attack!");
                                System.out.println("The impact reverberates through your body — you are stunned!");
                                damage -= blocked;
                                if (damage < 0) damage = 0;
                                player.applyStun();
                            } else {
                                damage -= blocked;
                                System.out.println(enemy.getName() + " blocks, reducing incoming damage!");
                            }
                        }
                        enemy.takeDamage(damage);
                        if (damage > 0) {
                            System.out.println("You hit " + enemy.getName() + " for " + damage + " damage!");
                        } else {
                            System.out.println("Your attack dealt no damage!");
                        }
                        break;
                    case BLOCK:
                        System.out.println("You raise your shield, ready to block!");
                        if (enemyState == CombatState.FEINT) {
                            System.out.println(enemy.getName() + " feinted, leaving you wide open!");
                            player.applyStun();
                            playerState = CombatState.STUN;
                        }
                        break;
                    case FEINT:
                        System.out.println("You feint to confuse your opponent!");
                        break;
                    case RUN:
                        System.out.println("You flee from the " + enemy.getName() + "!");
                        player.setCurrentRoom(previousRoom);
                        System.out.println(player.getCurrentRoom().getLongDescription());
                        inCombat = false;
                        continue;
                }


            // Enemy does action
            if (enemy.getHealth() > 0) {
                if (player.isStunned()) {
                    enemyState = CombatState.ATTACK;
                    System.out.println("The " + enemy.getName() + " takes advantage of your weakened state!");
                    player.clearStun();
                }
                switch(enemyState) {
                    case ATTACK:
                        double enemyDamage = enemy.attack();
                        if (playerState == CombatState.BLOCK && player.getEquippedShield() != null) {
                            double blocked = player.block();
                            if (enemyDamage < blocked + 20) {
                                enemyDamage -= blocked;
                                if (enemyDamage < 0) enemyDamage = 0;
                                enemy.applyStun();
                            } else {
                                enemyDamage -= blocked;
                                if (enemyDamage < 0) enemyDamage = 0;
                                System.out.println("You block " + blocked + " damage!");
                            }
                        }
                        player.takeDamage(enemyDamage);
                        if (enemyDamage > 0) {
                            System.out.println(enemy.getName() + " strikes you for " + enemyDamage + " damage!");
                        } else {
                            System.out.println(enemy.getName() + "'s attack glances off your defence, stunning it for a turn!");
                        }
                        break;

                    case BLOCK:
                        if (playerState == CombatState.BLOCK) {
                            System.out.println(enemy.getName() + " raises it's shield.");
                        }
                        if (playerState == CombatState.FEINT) {
                        System.out.println(enemy.getName() + " tried to block but was deceived by your feint stunning it!");
                        enemy.applyStun();
                        }
                        break;

                    case FEINT:
                        if (playerState != CombatState.BLOCK) {
                        System.out.println(enemy.getName() + " threw a feint.");
                        }
                        break;
                }
            }

            // On death
            if (player.getHealth() <= 0) {
                System.out.println("You have been slain by " + enemy.getName() + "...");
                System.out.println("GAME OVER");
                System.exit(0);
            }

            if (enemy.getHealth() <= 0) {
                enemy.onDeath(player);
                inCombat = false;
            }
        }
    }*/

    private void checkHonourEvents() {
        if (player.getHonour("Poseidon") >= 30 && !poseidon.isVisible()) {
            poseidon.setVisible(true);
            System.out.println("Poseidon's Palace rises from the deep!");
        }


        if (player.getHonour("Athena") >= 20 && !secret.isVisible()) {
            secret.setVisible(true);
            System.out.println("A hidden passage behind the balcony reveals itself!");
        }


        if (player.getHonour("Ares") >= 50) {
            System.out.println("Ares roars proudly at your strength! (All attacks deal more damage)");
        }
    }

    private void checkOlympusUnlock() {
        if (!olympusUnlocked && player.checkGems() >= 3) {
            olympus.setVisible(true);
            olympusUnlocked = true;
            System.out.println("A divine rumble echoes...");
            System.out.println("THE PATH TO OLYMPUS REVEALS ITSELF!!!");
        }
    }

    private void cheatEnd() {
        GodlyGemstone cheatGem1 = new GodlyGemstone("Forbidden Gem 1", "A gem with unknown and uncontrollable power");
        GodlyGemstone cheatGem2 = new GodlyGemstone("Forbidden Gem 2", "A gem with unknown and uncontrollable power");
        GodlyGemstone cheatGem3 = new GodlyGemstone("Forbidden Gem 3", "A gem with unknown and uncontrollable power");
        player.getInventory().add(cheatGem1);
        player.getInventory().add(cheatGem2);
        player.getInventory().add(cheatGem3);
        player.setCurrentRoom(hera);
        checkOlympusUnlock();
    }


    public void saveGame(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(player);
            System.out.println(filename + "'s game saved.");
        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    public void loadGame(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            player = (Player) in.readObject();
            System.out.println("Loaded " + filename + "'s game.");
            System.out.println(player.getCurrentRoom().getLongDescription());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        OlympicTrials game = new OlympicTrials();
        game.play();

    }

}
