package fr.maximouz.cashprice.scoreboard;

import fr.maximouz.cashprice.scoreboard.scoreboards.PlayingPlayerScoreboard;
import fr.maximouz.cashprice.scoreboard.scoreboards.WaitingPlayerScoreboard;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public enum PlayerScoreboardType {

    WAITING(WaitingPlayerScoreboard.class),
    PLAYING(PlayingPlayerScoreboard.class);

    private final Class<?> clazz;

    PlayerScoreboardType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public PlayerScoreboard getNewPlayerScoreboard(Player player) {
        try {
            return (PlayerScoreboard) clazz.getDeclaredConstructor(Player.class).newInstance(player);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}
