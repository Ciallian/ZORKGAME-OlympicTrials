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
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class GUI extends Application implements RoomChange, CombatChecker {
    private OlympicTrials game;
    private TextArea outputArea;
    private TextField inputField;
    private ImageView roomImageView;
    private ImageView mapImageView;
    private ComboBox<String> interactDropdown;
    private ListView<String> inventoryList;
    private GuiIO guiGameIO;
    private HBox choiceButtonBox; //Choices for NPC and Combat
    private boolean roomInspected = false;


    @Override
    public void start(Stage stage) {
        game = new OlympicTrials();
        game.setRoomChange(this);

        // OUTPUT TERMINAL
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(180);

        redirectSystemOut();

        // CHOICE BUTTON BOX
        choiceButtonBox = new HBox(10);
        choiceButtonBox.setAlignment(Pos.CENTER_LEFT);

        // Initialize GuiIO
        guiGameIO = new GuiIO(outputArea, choiceButtonBox);
        for (Room room : game.getAllRooms()) {
            for (NPC npc : room.getNPCs().values()) {
                npc.setIO(guiGameIO);
                npc.setGUI(this);
            }
        }
        // INPUT BAR
        inputField = new TextField();
        inputField.setPromptText("Enter command...");
        Button sendButton = new Button("Send");
        HBox commandBar = new HBox(10, inputField, sendButton);
        commandBar.setAlignment(Pos.CENTER);

        sendButton.setOnAction(e -> handleInput());
        inputField.setOnAction(e -> handleInput());

        // MAP IMAGE
        mapImageView = new ImageView();
        mapImageView.setFitWidth(400);
        mapImageView.setFitHeight(250);
        mapImageView.setPreserveRatio(true);

        VBox mapBox = new VBox(5, new Label("Map"), mapImageView);


        // DROPDOWN & ACTION BUTTONS
        interactDropdown = new ComboBox<>();
        interactDropdown.setPromptText("Select target...");
        Button interactButton = new Button("Interact");
        Button takeButton = new Button("Take");
        Button dropButton = new Button("Drop");
        Button equipButton = new Button("Equip");

        interactButton.setOnAction(e -> runDropdownCommand("interact"));
        takeButton.setOnAction(e -> runDropdownCommand("take"));
        dropButton.setOnAction(e -> runDropdownCommand("drop"));
        equipButton.setOnAction(e -> runDropdownCommand("equip"));

        HBox actionBar = new HBox(10, interactDropdown, interactButton, takeButton, dropButton, equipButton);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        // INVENTORY
        inventoryList = new ListView<>();
        inventoryList.setPrefHeight(200);
        inventoryList.setOnMouseClicked(e -> {
            String selected = inventoryList.getSelectionModel().getSelectedItem();
            if (selected != null) interactDropdown.setValue(selected);
        });

        VBox inventoryBox = new VBox(5, new Label("Inventory"), inventoryList);

        // MOVEMENT BUTTONS
        Button northButton = new Button("North");
        Button southButton = new Button("South");
        Button eastButton = new Button("East");
        Button westButton = new Button("West");
        Button lookButton = new Button("Look");

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
        VBox leftColumn = new VBox(10, mapBox, inventoryBox, new Label("Terminal"), outputArea, commandBar, choiceButtonBox);
        leftColumn.setAlignment(Pos.TOP_LEFT);
        leftColumn.setPrefWidth(550);

        // RIGHT COLUMN
        VBox rightColumn = new VBox(10, mapBox, movementBar, actionBar);
        rightColumn.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane();
        root.setLeft(leftColumn);
        root.setRight(rightColumn);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("ZorkUL GUI");
        stage.setScene(scene);
        stage.show();
        game.printWelcome();

        // Initial UI refresh
        refreshUI();
    }

    private void handleInput() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            System.out.println("> " + text);
            process(text);
            inputField.clear();
            refreshUI();
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
            interactDropdown.getItems().add(item.getName());
        }
        if (room.getNPCs() != null) {
            for (NPC npc : room.getNPCs().values()) {
                interactDropdown.getItems().add(npc.getName());
            }
        }
        if (room.getStorages() != null) {
            for (Storage storage : room.getStorages().values()) {
                interactDropdown.getItems().add(storage.getName());
                if (storage.isOpen()) {
                    for (Item item : storage.getItems()) {
                        interactDropdown.getItems().add(item.getName());
                    }
                }
            }
        }
        for (Item item : game.getPlayer().getInventory()) {
            if (!interactDropdown.getItems().contains(item.getName())) {
                interactDropdown.getItems().add(item.getName());
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
            Combat combatSession = new Combat(game.getPlayer(), newRoom.getOnlyEnemy(), previousRoom, guiGameIO);
            combatSession.start();
        }
    }

    @Override
    public void onEnemyEncountered(Enemy enemy, Room previousRoom) {
        Combat combatSession = new Combat(game.getPlayer(), enemy, previousRoom, guiGameIO);
        combatSession.start();
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

