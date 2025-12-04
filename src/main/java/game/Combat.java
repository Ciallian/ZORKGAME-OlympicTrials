package game;

import javafx.application.Platform;
import java.util.List;

public class Combat {
    private Player player;
    private Enemy enemy;
    private Room previousRoom;
    private transient GameIO io;
    private boolean inCombat = true;
    private OlympicTrials game;
    private CombatChecker combatCheck;

    public Combat(Player player, Enemy enemy, Room previousRoom, GameIO io, OlympicTrials game) {
        this.player = player;
        this.enemy = enemy;
        this.previousRoom = previousRoom;
        this.io = io;
        this.game = game;
    }

    public void start() {
        io.print("You are ambushed by " + enemy.getName() + "!");
        nextTurn();
    }

    private void nextTurn() {
        if (!inCombat || game.getGameEnd() != GameEnd.NONE) {
            io.clearChoices();
            return;
        }

        io.print("\nXXXXX COMBAT XXXXX");
        io.print("\nYour Health: " + player.getHealth());
        io.print(enemy.getName() + " Health: " + enemy.getHealth());

        List<String> options = List.of("Attack", "Block", "Feint", "Run");
        io.promptChoices(options);

        io.setChoiceListener(new GameIO.ChoiceListener() {
            @Override
            public void choiceSelected(int choice) {
                if (game.getGameEnd() != GameEnd.NONE) {
                    io.clearChoices();
                    inCombat = false;
                    return;
                }

                CombatState playerState;
                switch (choice) {
                    case 0:
                        playerState = CombatState.ATTACK;
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
        if (game.getGameEnd() != GameEnd.NONE || !inCombat) {
            return;
        }

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
            if (player.getHealth() <= 0) {
                System.out.println("You have been slain by " + enemy.getName() + "...");
                System.out.println("GAME OVER");
                endCombat();
                game.setGameEnd(GameEnd.PLAYER_DIED);
                return;
            }

            if (enemy.getHealth() <= 0) {
                enemy.onDeath(player);
                endCombat();
            }
        }
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setCombatListener(CombatChecker check) {
        this.combatCheck = check;
    }

    private void notifyEnd() {
        if (combatCheck != null) {
            Platform.runLater(() -> combatCheck.onCombatEnded());
        }
    }

    private void endCombat() {
        inCombat = false;
        io.clearChoices();
        notifyEnd();
    }

}

