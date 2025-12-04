import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class GUI extends Application implements RoomChange, CombatChecker {
    private OlympicTrials game;
    private TextArea outputArea;
    private TextField inputField;
    private ImageView mapImageView;
    private ComboBox<String> interactDropdown;
    private ListView<String> inventoryList;
    private Button takeButton;
    private Button dropButton;
    private Button equipButton;
    private Button northButton;
    private Button southButton;
    private Button eastButton;
    private Button westButton;
    private Button lookButton;
    private Button interactButton;
    private Button saveButton;
    private Button loadButton;
    private GuiIO guiGameIO;
    private HBox choiceButtonBox; //Choices for NPC and Combat
    private boolean roomInspected = false;
    private Combat currentCombat;
    private boolean guiInCombat = false;




    @Override
    public void start(Stage stage) {
        game = new OlympicTrials();
        game.setRoomChange(this);

        // OUTPUT TERMINAL
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(300); // make terminal bigger
        redirectSystemOut();

        // INPUT BAR (under terminal, same width)
        inputField = new TextField();
        inputField.setPromptText("Enter command..........");
        inputField.setOnAction(e -> handleInput());
        HBox inputBar = new HBox(inputField);
        inputBar.setAlignment(Pos.CENTER_LEFT);
        inputBar.setPrefWidth(50);

        // CHOICE BUTTON BOX
        choiceButtonBox = new HBox(10);
        choiceButtonBox.setAlignment(Pos.CENTER_LEFT);


        // Initialize GuiIO for NPCs
        guiGameIO = new GuiIO(outputArea, choiceButtonBox);
        for (Room room : game.getAllRooms()) {
            for (NPC npc : room.getNPCs().values()) {
                npc.setIO(guiGameIO);
                npc.setGUI(this);
            }
        }

        // MAP IMAGE
        ImageView mapImageView = new ImageView(new javafx.scene.image.Image("file:C:/Users/djsqu/OneDrive/Pictures/Screenshots/map.png"));
        mapImageView.setFitWidth(400);
        mapImageView.setFitHeight(250);
        mapImageView.setPreserveRatio(true);
        VBox mapBox = new VBox(5, new Label("Map"), mapImageView);

        // INVENTORY
        inventoryList = new ListView<>();
        inventoryList.setPrefHeight(200);
        inventoryList.setOnMouseClicked(e -> {
            String selected = inventoryList.getSelectionModel().getSelectedItem();
            if (selected != null) interactDropdown.setValue(selected);
        });
        VBox inventoryBox = new VBox(5, new Label("Inventory"), inventoryList);

        // DROPDOWN & ACTION BUTTONS
        interactDropdown = new ComboBox<>();
        interactDropdown.setPromptText("Select target...");
        interactButton = new Button("Interact");
        takeButton = new Button("Take");
        dropButton = new Button("Drop");
        equipButton = new Button("Equip");
        saveButton = new Button("Save Game");
        loadButton = new Button("Load Game");

        interactButton.setOnAction(e -> runDropdownCommand("interact"));
        takeButton.setOnAction(e -> runDropdownCommand("take"));
        dropButton.setOnAction(e -> runDropdownCommand("drop"));
        equipButton.setOnAction(e -> runDropdownCommand("equip"));

        HBox actionBar = new HBox(10, interactDropdown, interactButton, takeButton, dropButton, equipButton);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        // Separate row for Save/Load buttons
        HBox saveLoadBox = new HBox(10, saveButton, loadButton);
        saveLoadBox.setAlignment(Pos.CENTER_LEFT);
        saveLoadBox.setPadding(new Insets(0, 0, 0, 100)); // align under Interact button

        saveButton.setOnAction(e -> process("save"));
        loadButton.setOnAction(e -> {
            process("load");
            if (game.getGameEnd() == GameEnd.PLAYER_QUIT) {
                game.setGameEnd(GameEnd.NONE);
                reEnableAllUI();
                refreshUI();
            }
        });

        // MOVEMENT BUTTONS
        northButton = new Button("North");
        southButton = new Button("South");
        eastButton = new Button("East");
        westButton = new Button("West");
        lookButton = new Button("Look..");

        northButton.setOnAction(e -> process("go north"));
        southButton.setOnAction(e -> process("go south"));
        eastButton.setOnAction(e -> process("go east"));
        westButton.setOnAction(e -> process("go west"));
        lookButton.setOnAction(e -> process("look"));

        GridPane compassPad = new GridPane();
        compassPad.setHgap(10);
        compassPad.setVgap(10);
        compassPad.setAlignment(Pos.CENTER);
        compassPad.add(northButton, 1, 0);
        compassPad.add(westButton, 0, 1);
        compassPad.add(lookButton, 1, 1);
        compassPad.add(eastButton, 2, 1);
        compassPad.add(southButton, 1, 2);

        VBox movementBar = new VBox(compassPad);
        movementBar.setAlignment(Pos.CENTER);

        // LEFT COLUMN
        VBox leftColumn = new VBox(10, outputArea, inputBar, choiceButtonBox, actionBar, movementBar);
        leftColumn.setAlignment(Pos.TOP_LEFT);
        leftColumn.setPrefWidth(550);
        actionBar.setAlignment(Pos.CENTER);
        movementBar.setAlignment(Pos.CENTER);
        movementBar.setPadding(new Insets(20, 0, 0, 0));

        // RIGHT COLUMN: map + inventory
        VBox rightColumn = new VBox(10, mapBox, inventoryBox, saveLoadBox);
        rightColumn.setAlignment(Pos.TOP_CENTER);

        // ROOT
        BorderPane root = new BorderPane();
        root.setLeft(leftColumn);
        root.setRight(rightColumn);
        root.setPadding(new Insets(10));

        // BACKGROUND IMAGE
        ImageView background = new ImageView(new javafx.scene.image.Image("file:C:/Users/djsqu/OneDrive/Pictures/castle.jpg"));
        background.setFitWidth(1100);
        background.setFitHeight(700);
        background.setPreserveRatio(false);

        StackPane stackRoot = new StackPane(background, root);
        Scene scene = new Scene(stackRoot, 1100, 700);
        stage.setTitle("ZorkUL GUI");
        stage.setScene(scene);
        stage.show();

        game.printWelcome();
        refreshUI(); // Initial UI refresh
    }


    private void handleInput() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            System.out.println("> " + text);
            process(text);
            inputField.clear();
            refreshUI();
            handleGameEnd();
        }
    }

    private void process(String input) {
        try {
            Command command = new Parser().getCommand(input);
            if (command == null || command.getCommandWord() == null) {
                throw new Exception("I don't understand that command.");
            }
            if (command.getCommandWord().equals("look")) {
                roomInspected = true;
                refreshUI();
            }
            game.processCommand(command);
        } catch (Exception e) {
            System.out.println("Command Error: " + e.getMessage() + "\nCheck spelling.");
        }

    }

    private void runDropdownCommand(String base) {
        String selected = interactDropdown.getValue();
        if (selected != null && !selected.isEmpty()) {
            process(base + " " + selected);
            refreshUI();
        }
    }

    public void refreshUI() {
        Platform.runLater(() -> {
            if (game.getGameEnd() != GameEnd.NONE) {
                handleGameEnd();
                return;
            }

            updateInventory();
            updateInteractDropdown();

        });
    }

    private void updateInventory() {
        inventoryList.getItems().clear();
        for (Item item : game.getPlayer().getInventory()) {
            inventoryList.getItems().add(item.getName());
        }
    }

    private void updateInteractDropdown() {
        interactDropdown.getItems().clear();
        if (!roomInspected) {
            return;
        }

        Room room = game.getPlayer().getCurrentRoom();

        for (Item item : room.getItems()) {
            if (item.isVisible()) {
                interactDropdown.getItems().add(item.getName());
            }
        }
        if (room.getNPCs() != null) {
            for (NPC npc : room.getNPCs().values()) {
                interactDropdown.getItems().add(npc.getName());
            }
        }
        if (room.getStorages() != null) {
            for (Storage<? extends Item> storage : room.getStorages().values()) {
                interactDropdown.getItems().add(storage.getName());
                if (storage.isOpen()) {
                    for (Item item : storage.getItems()) {
                        interactDropdown.getItems().add(item.getName());
                    }
                }
            }
        }
    }

    private void redirectSystemOut() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                Platform.runLater(() -> {
                    outputArea.appendText(String.valueOf((char) b));
                    if (outputArea.getText().length() > 10000) outputArea.deleteText(0, 2000);
                });
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    @Override
    public void onRoomChanged(Room newRoom, Room previousRoom) {
        roomInspected = false;
        updateInteractDropdown();

        // Check for enemies in the room
        if (newRoom.hasEnemies() && !newRoom.equals(previousRoom)) {
            currentCombat = new Combat(game.getPlayer(), newRoom.getOnlyEnemy(), previousRoom, guiGameIO, game);
            currentCombat.setCombatListener(this);
            guiInCombat = true;
            if (guiInCombat == true) {
                handleCombatGUI();
                currentCombat.start();
            }

        }
    }

    @Override
    public void onEnemyEncountered(Enemy enemy, Room previousRoom) {
        currentCombat = new Combat(game.getPlayer(), enemy, previousRoom, guiGameIO, game);
        currentCombat.setCombatListener(this);
        guiInCombat = true;
        if (guiInCombat == true) {
            handleCombatGUI();
            currentCombat.start();
        }
    }

    @Override
    public void onCombatEnded() {
        Platform.runLater(() -> {
            guiInCombat = false;
            if (guiInCombat == false) {
                currentCombat = null;
                reEnableAllUI();
                refreshUI();
            }
        });
    }

    public boolean isInCombat() {
        return currentCombat != null && currentCombat.isInCombat();
    }

    private void handleCombatGUI() {

        boolean combatActive = isInCombat();


        if (combatActive) {
            guiInCombat = true;
            inputField.setEditable(false);
            interactDropdown.setDisable(true);
            choiceButtonBox.getChildren().clear();
            interactButton.setDisable(true);
            takeButton.setDisable(true);
            dropButton.setDisable(true);
            equipButton.setDisable(true);
            northButton.setDisable(true);
            southButton.setDisable(true);
            eastButton.setDisable(true);
            westButton.setDisable(true);
            lookButton.setDisable(true);
            inventoryList.setDisable(true);
        }
    }

    private void handleGameEnd() {
        if (game.getGameEnd() != GameEnd.NONE) {

            inputField.setEditable(false);
            interactDropdown.setDisable(true);
            choiceButtonBox.getChildren().clear();
            interactButton.setDisable(true);
            takeButton.setDisable(true);
            dropButton.setDisable(true);
            equipButton.setDisable(true);
            northButton.setDisable(true);
            southButton.setDisable(true);
            eastButton.setDisable(true);
            westButton.setDisable(true);
            lookButton.setDisable(true);
            inventoryList.setDisable(true);
            if (game.getGameEnd() == GameEnd.PLAYER_DIED || game.getGameEnd() == GameEnd.PLAYER_WON) {
                saveButton.setDisable(true);
                loadButton.setDisable(true);
            }
        }
    }

    private void reEnableAllUI() {
        inputField.setEditable(true);
        interactDropdown.setDisable(false);
        interactButton.setDisable(false);
        takeButton.setDisable(false);
        dropButton.setDisable(false);
        equipButton.setDisable(false);

        northButton.setDisable(false);
        southButton.setDisable(false);
        eastButton.setDisable(false);
        westButton.setDisable(false);
        lookButton.setDisable(false);

        inventoryList.setDisable(false);
    }

    public static void main(String[] args) {
        Application.launch(GUI.class, args);
    }
}


class GuiIO implements GameIO {
    private TextArea outputArea;
    private HBox buttonBox;
    private GameIO.ChoiceListener choiceListener;

    public GuiIO(TextArea outputArea, HBox buttonBox) {
        this.outputArea = outputArea;
        this.buttonBox = buttonBox;
    }

    @Override
    public void print(final String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                outputArea.appendText(s + "\n");
            }
        });
    }

    @Override
    public void promptChoices(final List<String> options) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                buttonBox.getChildren().clear();
                for (int i = 0; i < options.size(); i++) {
                    final int idx = i;
                    Button btn = new Button(options.get(i));
                    btn.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                        @Override
                        public void handle(javafx.event.ActionEvent event) {
                            if (choiceListener != null) choiceListener.choiceSelected(idx);
                        }
                    });
                    buttonBox.getChildren().add(btn);
                }
            }
        });
    }

    @Override
    public void setChoiceListener(GameIO.ChoiceListener listener) {
        this.choiceListener = listener;
    }

    @Override
    public void clearChoices() {
        Platform.runLater(() -> {
            buttonBox.getChildren().clear();
            choiceListener = null;
        });
    }

}

