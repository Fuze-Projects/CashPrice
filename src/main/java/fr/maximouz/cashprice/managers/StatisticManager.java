package fr.maximouz.cashprice.managers;

import fr.maximouz.cashprice.Statistic;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatisticManager {

    private final List<Statistic> statistics;

    public StatisticManager() {
        statistics = new ArrayList<>();
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public Statistic getStatistic(Player player) {
        return statistics.stream().filter(statistic -> statistic.getPlayer().getUniqueId() == player.getUniqueId()).findFirst().orElse(null);
    }

    public void resetAll() {
        statistics.forEach(Statistic::reset);
    }

    public int getAllIronBlockCrafted() {
        return statistics.stream().mapToInt(Statistic::getIronBlockCrafted).sum();
    }

    public int getAllIronBlockPlaced() {
        return statistics.stream().mapToInt(Statistic::getIronBlockPlaced).sum();
    }

    public int getAllGoldBlockCrafted() {
        return statistics.stream().mapToInt(Statistic::getGoldBlockCrafted).sum();
    }

    public int getAllGoldBlockPlaced() {
        return statistics.stream().mapToInt(Statistic::getGoldBlockPlaced).sum();
    }

    public int getAllDiamondBlockCrafted() {
        return statistics.stream().mapToInt(Statistic::getDiamondBlockCrafted).sum();
    }

    public int getAllDiamondBlockPlaced() {
        return statistics.stream().mapToInt(Statistic::getDiamondBlockPlaced).sum();
    }

    public int getAllKills() {
        return statistics.stream().mapToInt(Statistic::getKills).sum();
    }

}
