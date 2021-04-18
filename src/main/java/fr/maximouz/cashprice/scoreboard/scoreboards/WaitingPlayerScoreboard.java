package fr.maximouz.cashprice.scoreboard.scoreboards;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboard;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WaitingPlayerScoreboard extends PlayerScoreboard {

    public WaitingPlayerScoreboard(Player player) {
        super(PlayerScoreboardType.WAITING, player);
    }

    @Override
    public void update() {

        setLine(5, "§1");

        long players = Bukkit.getOnlinePlayers().stream().filter(target -> !target.isOp()).count();
        int min = CashPrice.minPlayers;
        String state = players >= min ? "§adu démarrage" : "§ede joueurs";

        setLine(4, "§fJoueurs: " + (players >= min ? "§a" : "§7") + players + "/" + min);
        setLine(3, "§fStatus: " + (players >= min ? "§a" : "§e") + "En attente");
        setLine(2, state);

        setLine(1, "§1§9");

    }

}
