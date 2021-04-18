package fr.maximouz.cashprice.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerScoreboard {

    private final PlayerScoreboardType type;
    private final Player player;

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final Map<Integer, String> oldTexts;

    public PlayerScoreboard(PlayerScoreboardType type, Player player) {
        this.type = type;
        this.player = player;

        ScoreboardManager scoreBoardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreBoardManager.getNewScoreboard();

        objective = scoreboard.registerNewObjective("fuzeiii", "dummy",
                "§6§lCashPrice"
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        oldTexts = new HashMap<>();
    }

    public PlayerScoreboardType getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public abstract void update();

    public void setLine(int index, String text) {
        String old = oldTexts.get(index);
        if (old != null) {
            if (old.equals(text))
                return;
            else
                scoreboard.resetScores(old);
        }
        oldTexts.put(index, text);
        Score score = objective.getScore(text);
        score.setScore(index);
    }

}
