package fr.maximouz.cashprice.scoreboard.scoreboards;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Pool;
import fr.maximouz.cashprice.Statistic;
import fr.maximouz.cashprice.Utils;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboard;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboardType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.TimeUnit;

public class PlayingPlayerScoreboard extends PlayerScoreboard {

    private Pool pool;
    private Statistic statistic;

    private final Team playersTeam;
    private final Team operatorTeam;

    public PlayingPlayerScoreboard(Player player) {
        super(PlayerScoreboardType.PLAYING, player);
        this.pool = null;
        this.statistic = null;

        playersTeam = getScoreboard().registerNewTeam("Joueurs");
        playersTeam.setPrefix("§a");
        playersTeam.setColor(ChatColor.GREEN);
        playersTeam.setAllowFriendlyFire(true);
        playersTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        playersTeam.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);

        operatorTeam = getScoreboard().registerNewTeam("Opérateurs");
        operatorTeam.setPrefix("§c");
        operatorTeam.setColor(ChatColor.RED);
        operatorTeam.setAllowFriendlyFire(false);
        operatorTeam.setCanSeeFriendlyInvisibles(false);
        operatorTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        operatorTeam.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);

        if (player.isOp())
            operatorTeam.addEntry(player.getName());
        else
            playersTeam.addEntry(player.getName());
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    private int pvpLine;
    private int deathMatchLine;

    @Override
    public void update() {

        int i = 15;

        setLine(i--, "§1");

        int alivePlayers = CashPrice.getInstance().getManager().getAlivePlayers().size();
        int total = alivePlayers + CashPrice.getInstance().getManager().getDeadPlayers().size();

        setLine(i--, "§fˣ En vie: §a" + alivePlayers + "/" + total);
        setLine(i--, "§fˣ Argent total: §b" + CashPrice.getInstance().getPoolManager().getAllAmount() + "€");

        setLine(i--, "§2§1");
        setLine(i--, "§7§lINFORMATIONS");
        String pvp = CashPrice.getInstance().getManager().getTimePassed() < TimeUnit.MINUTES.toMillis(10) ? Utils.formatTime(TimeUnit.MINUTES.toMillis(10) - CashPrice.getInstance().getManager().getTimePassed()) : "§aON";
        pvpLine = i;
        setLine(i--, "§fˣ PvP: §e" + pvp);
        String deathMatch = CashPrice.getInstance().getManager().getTimeLeft() <= 0 ? "§cON" : Utils.formatTime(CashPrice.getInstance().getManager().getTimeLeft());
        deathMatchLine = i;
        setLine(i--, "§fˣ DeathMatch: §e" + deathMatch);

        if (pool != null && statistic != null) {

            setLine(i--, "§2§2");
            setLine(i--, "§7§lSTATISTIQUES");
            setLine(i--, "§fˣ Cagnotte: §e" + pool.getAmount().toString() + "€");
            setLine(i--, "§fˣ Kills: §a" + statistic.getKills());

        }

        setLine(i--, "§2§3");
        setLine(i--, "§7§lCERCLE ROUGE");
        Location circle = CashPrice.getInstance().getManager().getSpawnPoint();
        setLine(i--, "§7x: §f" + circle.getBlockX() + " §7y: §f" + circle.getBlockY() + " §7z: §f" + circle.getBlockZ());

        setLine(i, "§1§9");

    }

    public void updateTimers() {
        String pvp = CashPrice.getInstance().getManager().hasPvp() ? "§aON" : Utils.formatTime(CashPrice.getInstance().getManager().getPvpTimeLeft());
        setLine(pvpLine, "§fˣ PvP: §e" + pvp);

        String deathMatch = CashPrice.getInstance().getManager().hasDeathMatch() ? "§cON" : Utils.formatTime(CashPrice.getInstance().getManager().getTimeLeft());
        setLine(deathMatchLine, "§fˣ DeathMatch: §e" + deathMatch);
    }

    public Team getPlayersTeam() {
        return playersTeam;
    }

    public Team getOperatorTeam() {
        return operatorTeam;
    }
}
