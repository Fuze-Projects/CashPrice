package fr.maximouz.cashprice.managers;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboard;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboardType;
import fr.maximouz.cashprice.scoreboard.scoreboards.PlayingPlayerScoreboard;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerScoreboardManager {

    private final List<PlayerScoreboard> scoreboards;

    public PlayerScoreboardManager() {

        scoreboards = new ArrayList<>();

    }

    public List<PlayerScoreboard> getPlayerScoreBoards() {
        return scoreboards;
    }

    public PlayerScoreboard getPlayerScoreBoard(Player player) {
        return getPlayerScoreBoards().stream()
                .filter(playerScoreBoard -> playerScoreBoard.getPlayer() == player)
                .findFirst()
                .orElse(null);
    }

    public PlayerScoreboard setPlayerScoreboardType(Player player, PlayerScoreboardType type) {
        unregister(player);
        PlayerScoreboard playerScoreboard = type.getNewPlayerScoreboard(player);
        player.setScoreboard(playerScoreboard.getScoreboard());
        scoreboards.add(playerScoreboard);
        return playerScoreboard;
    }

    public PlayerScoreboard register(Player player) {
        return setPlayerScoreboardType(player,
                CashPrice.getInstance().getManager().hasStarted()
                        ? PlayerScoreboardType.PLAYING
                        : PlayerScoreboardType.WAITING
        );
    }

    public void unregister(Player player) {
        PlayerScoreboard scoreboard = getPlayerScoreBoard(player);
        if (scoreboard != null)
            scoreboards.remove(scoreboard);
    }

}
