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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OlympicTrials {
    private Parser parser;
    private Player player;
    private boolean olympusUnlocked = false;
    private RoomChange roomChange;
    private CombatChecker combatChecker;
    private GameEnd gameEnd = GameEnd.NONE;
    private GameIO io;
    private final Set<String> firedEvents = new HashSet<>();



    public OlympicTrials() {
        createRooms();
        parser = new Parser();
    }

    private Room cloudstep, zeus, balcony, secret, demeter, athena, study, ares, haven, poseidon, hera, olympus;
    private List<Room> allRooms = new ArrayList<>();

    private void createRooms() {
        // Create Rooms
        cloudstep = new Room("Cloudstep Plateau: A bright mystical place where you feel as if you're standing on a cloud", true);
        zeus = new Room("The Hall of Zeus: Grand marble pillars rise to the heavens, lightning crackles in the air", true);
        balcony = new Room("Overlooking Balcony: Views as far as the eye can see", true);
        secret = new Room("Secret Vault: A hidden cave littered with treasure", false);
        demeter = new Room("Garden of Demeter: Lush greenery and golden wheat sway in a warm divine breeze", true);
        athena = new Room("Library of Athena: Shelves of endless wisdom, scrolls glowing faintly with divine light", true);
        study = new Room("Athena's Study: Quiet and filled with knowledge and secrets known only by the wisest", true);
        ares = new Room("Ares Battlefield: The ground trembles with echoes of clashing steel and roaring warriors", true);
        haven = new Room("Traveller's Haven: A cozy inn for weary souls crossing divine lands", true);
        poseidon = new Room("Poseidon's Palace: Crystal walls ripple with the rhythm of the ocean", false);
        hera = new Room("Hera's Wrath: A storm of divine fury, lightning laced with vengeance", true);
        olympus = new Room("Olympus: You made it", false);

        allRooms.add(cloudstep);
        allRooms.add(zeus);
        allRooms.add(balcony);
        allRooms.add(secret);
        allRooms.add(demeter);
        allRooms.add(athena);
        allRooms.add(study);
        allRooms.add(ares);
        allRooms.add(haven);
        allRooms.add(poseidon);
        allRooms.add(hera);
        allRooms.add(olympus);


        //SEARCH ITEM
        SearchItems hearthstoke = new SearchItems("Hearthstoke", "A fire poker", "Shadow Key");
        SearchItems shovel = new SearchItems("Shovel", "A small shovel, maybe you can dig up something", "Ancient Scroll");
        SearchItems chisel = new SearchItems("Chisel", "A small shovel, maybe there's a hidden item under the floor.", "Nature Key");



        // HONOUR CHANGE ITEMS
        HonourItems trident = new HonourItems("Poseidon's Trident", "A lost weapon of the gods, it holds too much power for you to wield, but he will be delighted you found it.", "Poseidon", 30);
        HonourItems athenaHelmet = new HonourItems("Athena's Helmet", "Armour of the gods, it holds too much power for you to wear, but she will be delighted you found it.", "Athena", 50);

        // FOOD
        Food ambrosia = new Food("Ambrosia big fat poo", "Food of the gods", 20);
        Food nectar = new Food("Nectar", "Brew of the gods", 20);
        Food gyro = new Food("Gyro", "Meat deserving praise of the Gods", 30);
        Food potion1 = new Food("Red Potion", "Weak healing potion", 10);
        Food potion2 = new Food("Blue Potion", "Moderate healing potion", 25);
        Food potion3 = new Food("Green Potion", "Strong healing potion", 45);
        Food potion4 = new Food("Golden Potion", "Very strong healing potion", 75);
        Food potion5 = new Food("Elixir of Life", "Ultimate healing potion", 100);
        Item magicalFlower = new Food("Magical Flower", "A glowing flower that heals the weary.", 5);




        //KEYS
        Key warKey = new Key("War Key", "A heavy steel key stained with blood", "Steel");
        Key tideKey = new Key("Tide Key", "A key shaped like a wave crest", "Ocean Blue");
        Key natureKey = new Key("Nature Key", "A wooden key covered in moss", "Nature");
        natureKey.setVisible(false);
        Key divineKey = new Key("Divine Key", "A key glowing with holy energy", "Divine");
        Key shadowKey = new Key("Shadow Key", "A cold black key humming with power", "Shadow");
        shadowKey.setVisible(false);
        Key goldenKey = new Key("Golden Key", "Unlocks divine secrets", "Golden");
        Key silverKey = new Key("Silver Key", "Basic Key", "Silver");

        //HELPFUL SCROLLS
        Papyrus introscroll = new Papyrus("Intro Scroll", "You have been chosen by the gods as the lucky mortal.\n" + "Here you may achieve godhood if you prove your worth.\n" + "Complete these trials and ascend to your throne on Olympus! \n(Hint: Keys unlock chests of the same colour)");
        Papyrus helpscroll = new Papyrus("Useful Scroll", "");
        Papyrus hintscroll = new Papyrus("Hint Scroll", "There could be a secret room\nWisdom unlocks what is hidden.\n Athena guards the way.\nShadows conceal power. Seek the study of Athena.");
        Papyrus helpscroll2 = new Papyrus("Useful Scroll", "Enemies can block, feint, and attack. \nBlock beats attack, feint beats block and attack beats feint.");
        Papyrus hintscroll2 = new Papyrus("Help Scroll", "The sea yields only to its master's trident.\nBlood unlocks war's prize. The Hydra holds the key.");
        Papyrus helpscroll3 = new Papyrus("Useful Scroll", "");
        Papyrus ancientScroll = new Papyrus("Ancient Scroll", "A scroll filled with ancient wisdom.\n Words you don't understand dance across the page. \n Maybe somebody else wants it...");
        ancientScroll.setVisible(false);


        // WEAPONS & SHIELDS
        Weapon dagger = new Weapon("Rusty Dagger", "Old, barely sharp dagger", 5);
        Weapon ironSword = new Weapon("Iron Sword", "Standard iron sword", 15);
        Weapon shinySpear = new Weapon("Imperial Gold Spear", "Gleaming weapon of the gods", 25);
        Weapon shinySword = new Weapon("Celestial Bronze Sword", "Gleaming weapon of the gods", 25);
        Weapon warHammer = new Weapon("War Hammer", "Sturdy Hammer designed for strong wielders", 30);
        Weapon warAxe = new Weapon("War Axe", "Heavy axe for serious battles", 35);
        Weapon divineSpear = new Weapon("Divine Spear", "Legendary spear, strikes with precision", 50);
        Weapon club = new Weapon("Wooden Club", "A large, heavy looking club, fit for only the strongest to use.", 60);
        Shield woodenShield = new Shield("Wooden Shield", "Basic wooden shield", 5);
        Shield hide = new Shield("Wolf's Hide", "Tough hide from a wolf", 15);
        Shield ironShield = new Shield("Iron Shield", "Solid shield for protection", 15);
        Shield goldenShield = new Shield("Golden Shield", "Bright golden shield", 25);
        Shield battleShield = new Shield("Battle Shield", "Reinforced for war", 35);
        Shield hugeShield = new Shield("Huge Shield", "Nothing can get passed it", 60);
        Shield divineAegis = new Shield("Divine Aegis", "Shield of the gods", 70);


        //GEMS (WIN CONDITION)
        GodlyGemstone rubyGem = new GodlyGemstone("Ruby Gem", "A gem pulsing with fiery divine energy");
        GodlyGemstone sapphireGem = new GodlyGemstone("Sapphire Gem", "A gem infused with oceanic power");
        GodlyGemstone emeraldGem = new GodlyGemstone("Emerald Gem", "A gem blessed by nature spirits");
        GodlyGemstone celestialGold = new GodlyGemstone("Celestial Gold", "Metal touched by the gods");
        GodlyGemstone lapizLazuli = new GodlyGemstone("Lapis Lazuli", "A dark relic of underworld power");

        //STORAGES
        Pithos pithos1 = new Pithos("Clay Pithos");
        pithos1.addItem(introscroll);
        pithos1.addItem(potion1);
        pithos1.addItem(silverKey);
        Pithos pithos2 = new Pithos("Brown Pithos");
        pithos2.addItem(goldenKey);
        pithos2.addItem(ambrosia);
        Pithos generalPithos = new Pithos("Pithos");
        generalPithos.addItem(nectar);
        generalPithos.addItem(dagger);
        generalPithos.addItem(helpscroll2);
        Pithos endPithos = new Pithos("Fancy Pithos");
        endPithos.addItem(celestialGold);
        Chest silverChest = new Chest("Silver Chest", true, "Silver");
        silverChest.addItem(potion2);
        silverChest.addItem(ironSword);
        silverChest.addItem(nectar);
        silverChest.addItem(chisel);
        Chest secretChest = new Chest("Ancient Hidden Chest", true, "Divine");
        secretChest.addItem(potion5);
        secretChest.addItem(divineSpear);
        secretChest.addItem(trident);
        secretChest.addItem(divineAegis);
        Chest libraryChest = new Chest("Golden Library Chest", true, "Golden");
        libraryChest.addItem(goldenShield);
        libraryChest.addItem(potion3);
        libraryChest.addItem(shinySword);
        libraryChest.addItem(shovel);
        Chest shadowChest = new Chest("Shadow Chest", true, "Shadow");
        shadowChest.addItem(lapizLazuli);
        shadowChest.addItem(potion4);
        Chest gardenChest = new Chest("Rootbound Chest", true, "Nature");
        gardenChest.addItem(emeraldGem);
        Chest tideChest = new Chest("Coral Chest", true, "Ocean Blue");
        tideChest.addItem(sapphireGem);
        Chest warChest = new Chest("Bloodstained War Chest", true, "Steel");
        warChest.addItem(rubyGem);
        Shelves oldShelf = new Shelves("Worn Shelf");
        oldShelf.addItem(hintscroll);
        oldShelf.addItem(hintscroll2);
        WeaponRack steelRack = new WeaponRack("Steel Weapon Rack");
        steelRack.addItem(warAxe);
        steelRack.addItem(ironSword);
        steelRack.addItem(shinySword);
        steelRack.addItem(dagger);
        WeaponRack studyRack = new WeaponRack("Weapon Rack of Athena");
        studyRack.addItem(ironSword);
        studyRack.addItem(warHammer);
        Crate brownCrate = new Crate("Sturdy Crate");
        brownCrate.addItem(battleShield);
        brownCrate.addItem(ironShield);
        brownCrate.addItem(goldenShield);
        brownCrate.addItem(woodenShield);
        Basket wovenBasket = new Basket("Reed Basket");
        wovenBasket.addItem(gyro);
        wovenBasket.addItem(potion2);


        // NPCs
        NPC nymph = new NPC("Nymph of Cloudstep", cloudstep, 50);
        nymph.addDialogue("Greetings, traveler! The clouds are restless today.");
        nymph.addDialogue("I sense a burden on your shoulders...");
        nymph.addDialogue("Take this Magical Flower. May it aid you on your journey!");
        nymph.setTrade(introscroll, magicalFlower);

        NPC innkeeper = new NPC("Innkeeper", haven, 50);
        innkeeper.addDialogue("Rest your bones, traveler.");
        innkeeper.setTrade(potion1, hearthstoke);

        NPC wearyTraveller = new NPC("Exhausted Traveller", haven, 50);
        wearyTraveller.addDialogue("Im taking a rest, how am I alive.");
        wearyTraveller.addDialogue("\nBeware traveller, the room ahead contains a powerful fow.");
        wearyTraveller.addDialogue("\nAvoid it at all costs unless you are suitably prepared.");
        wearyTraveller.addDialogue("Im taking a rest.");



        NPC gardner = new NPC("Plant Gardner", demeter, 70);
        gardner.addDialogue("Fancy seeing someone else here");
        gardner.addDialogue("\nFor years I've tended to these gardens alone, undisturbed.");
        gardner.addDialogue("\nI recall hearing a tale of scrolls being hidden in these gardens...");


        NPC scholar = new NPC("Old Scholar Theron", athena, 60);
        scholar.addDialogue("Knowledge is sharper than any blade.\nSolve my riddle to find the Key of Shadow's.");
        scholar.addDialogue("\n Find a place where weary souls find rest.\n If you seek a potion coloured like blood, do not wander aimlessly, listen to the whispers near the hearth, where stories gather like embers.");
        scholar.addDialogue("\n Sometimes, the smallest trinket hides where warmth and tales entwine. \nUse a hearthstoke to reveal the secrets that hide.");
        scholar.setTrade(ancientScroll, athenaHelmet);

        NPC veteran = new NPC("Broken Veteran", ares, 40);
        veteran.addDialogue("War never leaves you...");
        veteran.addDialogue("Pleasing Ares may be difficult, but your power will increase tremendously.");
        veteran.setTrade(warHammer, divineKey);



        // ENEMIES
        Enemy hydra = new Enemy("Hydra", ares, 100,  5);
        hydra.addItem(goldenShield);
        hydra.addItem(shinySpear);
        hydra.addItem(warKey);
        Enemy wolf = new Enemy("Wolf", demeter, 50, 10);
        wolf.addItem(hide);
        Enemy cyclops = new Enemy("Hera's Cyclops of Doom", hera, 300,  20);
        cyclops.addItem(club);
        cyclops.addItem(hugeShield);
        cyclops.addItem(tideKey);

//            ROOM FUNCTIONS

// cloudstep
        cloudstep.setExit("north", zeus);
        cloudstep.addItem(dagger);
        cloudstep.addItem(woodenShield);
        cloudstep.addStorage("Clay Pithos", pithos1);
        cloudstep.addNPC(nymph);


// zeus
        zeus.setExit("south", cloudstep);
        zeus.setExit("north", balcony);
        zeus.setExit("east", athena);
        zeus.setExit("west", demeter);
        zeus.addStorage("Brown Pithos", pithos2);
        zeus.addStorage("Silver Chest", silverChest);


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
        demeter.addEnemy(wolf);
        demeter.addStorage(gardenChest.getName(), gardenChest);
        demeter.addItem(ancientScroll);
        demeter.addNPC(gardner);

// athena
        athena.setExit("west", zeus);
        athena.setExit("north", haven);
        athena.setExit("east", study);
        athena.addStorage(libraryChest.getName(), libraryChest);
        athena.addNPC(scholar);
        athena.addStorage(oldShelf.getName(), oldShelf);

// study
        study.setExit("west", athena);
        study.addStorage(shadowChest.getName(), shadowChest);
        study.addStorage(studyRack.getName(), studyRack);


// haven
        haven.setExit("south", athena);
        haven.setExit("east", poseidon);
        haven.setExit("north", hera);
        haven.addNPC(innkeeper);
        haven.addItem(shadowKey);
        haven.addStorage(generalPithos.getName(), generalPithos);
        haven.addNPC(wearyTraveller);

// poseidon
        poseidon.setExit("west", haven);
        poseidon.addStorage("Coral Chest", tideChest);

// ares
        ares.setExit("east", hera);
        ares.setExit("south", demeter);
        ares.addNPC(veteran);
        ares.addEnemy(hydra);
        ares.addStorage("War Chest", warChest);
        ares.addStorage(steelRack.getName(), steelRack);

// hera
        hera.setExit("south", haven);
        hera.setExit("west", ares);
        hera.setExit("north", olympus);
        hera.addEnemy(cyclops);
        hera.addStorage(endPithos.getName(), endPithos);

        // PLAYER
        player = new Player("Ciller", cloudstep, 200, 2);

    }
    public Player getPlayer() {
        return player;
    }

    public void play() {
        printWelcome();
        boolean finished = false;
        while (!finished && gameEnd == GameEnd.NONE) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }

        switch (gameEnd) {
            case PLAYER_DIED -> io.print("You have died. Game over.");
            case PLAYER_WON -> io.print("Congratulations! You won!");
            case PLAYER_QUIT -> io.print("You quit the game. Goodbye.");
        }
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
                    return true;
                } else {
                    setGameEnd(GameEnd.PLAYER_QUIT);
                    return false;
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
            setGameEnd(GameEnd.PLAYER_WON);
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
            for (Storage<? extends Item> storage : currentRoom.getStorages().values()) {
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
            for (Storage<? extends Item> storage : currentRoom.getStorages().values()) {
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
        if (npc != null) {
            npc.interact(player);
            return;
        }

        //Storages
        Storage storage = currentRoom.getStorage(targetName);
        if (storage != null) {
            storage.interact(player);
            return;
        }


        //Items (Room and Player)
        for (Item item : currentRoom.getItems()) {
            if (item.getName().equalsIgnoreCase(targetName)) {
                item.interact(player);
                return;
            }
        }
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(targetName)) {
                item.interact(player);
                if (item instanceof Food || item instanceof HonourItems || item instanceof SearchItems && item.getName().equalsIgnoreCase(targetName))
                    player.getInventory().remove(item);
                return;
            }
        }

        System.out.println("You can't interact with that right now.");
    }

    private void checkHonourEvents() {

        if (player.getHonour("Poseidon") >= 30 && !firedEvents.contains("POSEIDON_DONE")) {
            System.out.println("Poseidon believes in your courage!");
            poseidon.setVisible(true);
            System.out.println("Poseidon's Palace rises from the deep!");
            firedEvents.add("POSEIDON_DONE");

        }


        if (player.getHonour("Athena") >= 50 && !firedEvents.contains("ATHENA_DONE")) {
            System.out.println("Athena is pleased with your wisdom!");
            secret.setVisible(true);
            System.out.println("A hidden passage behind the balcony reveals itself!");
            firedEvents.add("ATHENA_DONE");
        }


        if (player.getHonour("Ares") >= 50 && !firedEvents.contains("ARES_DONE")) {
            System.out.println("Ares roars proudly at your strength! (All attacks deal more damage)");
            firedEvents.add("ARES_DONE");
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
        Shield iseRepellent = new Shield("Athena's Aegis", "Repellent of All", 1000);
        Weapon lightningBolt = new Weapon("Zeus's Lightning Bolt", "Destroys Everything in its Path.", 1000);
        player.getInventory().add(cheatGem1);
        player.getInventory().add(cheatGem2);
        player.getInventory().add(cheatGem3);
        player.getInventory().add(lightningBolt);
        player.getInventory().add(iseRepellent);
        player.setCurrentRoom(hera);
        player.setHealth(10000);
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

    public GameEnd getGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(GameEnd end) {
        gameEnd = end;
    }

    public static void main(String[] args) {
        OlympicTrials game = new OlympicTrials();
        game.play();

    }

}
