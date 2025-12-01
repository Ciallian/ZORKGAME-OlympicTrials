import javafx.application.Platform;
import out.production.JavaZork.CombatState;

import java.util.List;

import static out.production.JavaZork.CombatState.ATTACK;

public class Combat {
    private Player player;
    private Enemy enemy;
    private Room previousRoom;
    private GameIO io;
    private boolean inCombat = true;

    public Combat(Player player, Enemy enemy, Room previousRoom, GameIO io) {
        this.player = player;
        this.enemy = enemy;
        this.previousRoom = previousRoom;
        this.io = io;
    }

    public void start() {
        io.print("You are ambushed by " + enemy.getName() + "!");
        nextTurn();
    }

    private void nextTurn() {
        if (player.getHealth() <= 0) {
            System.out.println("You have been slain by " + enemy.getName() + "...");
            System.out.println("GAME OVER");
            System.exit(0);
        }

        if (enemy.getHealth() <= 0) {
            enemy.onDeath(player);
        }

        io.print("\nXXXXX COMBAT XXXXX");
        io.print("\nYour Health: " + player.getHealth());
        io.print(enemy.getName() + " Health: " + enemy.getHealth());

        List<String> options = List.of("Attack", "Block", "Feint", "Run");
        io.promptChoices(options);

        io.setChoiceListener(new GameIO.ChoiceListener() {
            @Override
            public void choiceSelected(int choice) {
                CombatState playerState;
                switch (choice) {
                    case 0:
                        playerState = ATTACK;
                        break;
                    case 1:
                        playerState = CombatState.BLOCK;
                        break;
                    case 2:
                        playerState = CombatState.FEINT;
                        break;
                    case 3:
                        playerState = CombatState.RUN;
                        break;
                    default:
                        playerState = CombatState.STUN;
                        break;
                }
                if (inCombat != false) {
                    resolveTurn(playerState);
                }
            }
        });
    }

    private void resolveTurn(CombatState playerState) {
        CombatState enemyState = null;
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
                        System.out.println("The impact reverberates through your body... you are stunned!");
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
                if (io instanceof GuiIO) {
                    Platform.runLater(() -> {
                        // trigger GUI's room refresh
                        // this uses the RoomChange callback already in GUI
                    });
                }
                endCombat();
                return;
        }

        // Enemy action
        if (enemy.getHealth() > 0) {
            if (player.isStunned()) {
                enemyState = CombatState.ATTACK;
                System.out.println("The " + enemy.getName() + " takes advantage of your weakened state!");
                player.clearStun();
            } switch(enemyState) {
                case ATTACK:
                    double enemyDamage = enemy.attack();
                    if (playerState == CombatState.BLOCK && player.getEquippedShield() == null) {
                        double blocked = player.block();
                    }
                    else if (playerState == CombatState.BLOCK && player.getEquippedShield() != null) {
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
        if (inCombat && enemy.getHealth() > 0 && player.getHealth() > 0) {
            nextTurn();
        } else {
            endCombat();
        }
    }

    private void endCombat() {
        inCombat = false;
        io.clearChoices();          // remove buttons
    }

}

