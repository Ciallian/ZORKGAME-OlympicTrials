package game;

public interface CombatChecker {
    void onEnemyEncountered(Enemy enemy, Room room);
    void onCombatEnded();
}
