package fr.maximouz.cashprice;

import fr.maximouz.cashprice.scoreboard.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Statistic {

    private final Player player;
    private int ironBlockCrafted;
    private int ironBlockPlaced;
    private int goldBlockCrafted;
    private int goldBlockPlaced;
    private int diamondBlockCrafted;
    private int diamondBlockPlaced;

    private int kills;

    public Statistic(Player player, int ironBlockCrafted, int ironBlockPlaced, int goldBlockCrafted, int goldBlockPlaced, int diamondBlockCrafted, int diamondBlockPlaced) {
        this.player = player;
        this.ironBlockCrafted = ironBlockCrafted;
        this.ironBlockPlaced = ironBlockPlaced;
        this.goldBlockCrafted = goldBlockCrafted;
        this.goldBlockPlaced = goldBlockPlaced;
        this.diamondBlockCrafted = diamondBlockCrafted;
        this.diamondBlockPlaced = diamondBlockPlaced;
    }

    public Statistic(Player player) {
        this(player, 0, 0, 0, 0, 0, 0);
    }

    public Player getPlayer() {
        return player;
    }

    public int getIronBlockCrafted() {
        return ironBlockCrafted;
    }

    public void setIronBlockCrafted(int ironBlockCrafted) {
        this.ironBlockCrafted = ironBlockCrafted;
    }

    public int getIronBlockPlaced() {
        return ironBlockPlaced;
    }

    public void setIronBlockPlaced(int ironBlockPlaced) {
        this.ironBlockPlaced = ironBlockPlaced;
    }

    public int getGoldBlockCrafted() {
        return goldBlockCrafted;
    }

    public void setGoldBlockCrafted(int goldBlockCrafted) {
        this.goldBlockCrafted = goldBlockCrafted;
    }

    public int getGoldBlockPlaced() {
        return goldBlockPlaced;
    }

    public void setGoldBlockPlaced(int goldBlockPlaced) {
        this.goldBlockPlaced = goldBlockPlaced;
    }

    public int getDiamondBlockCrafted() {
        return diamondBlockCrafted;
    }

    public void setDiamondBlockCrafted(int diamondBlockCrafted) {
        this.diamondBlockCrafted = diamondBlockCrafted;
    }

    public int getDiamondBlockPlaced() {
        return diamondBlockPlaced;
    }

    public void setDiamondBlockPlaced(int diamondBlockPlaced) {
        this.diamondBlockPlaced = diamondBlockPlaced;
    }

    public void craft(Material material) {
        if (material == Material.IRON_BLOCK) {
            ironBlockCrafted++;
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (target.isOp())
                    target.sendMessage(player.getName() + " a crafté un bloc de fer.");
            });
        } else if (material == Material.GOLD_BLOCK) {
            goldBlockCrafted++;
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (target.isOp())
                    target.sendMessage("§e" + player.getName() + " a crafté un bloc d'or.");
            });
        } else if (material == Material.DIAMOND_BLOCK) {
            diamondBlockCrafted++;
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (target.isOp())
                    target.sendMessage("§9" + player.getName() + " a crafté un bloc de diamant.");
            });
        }

    }

    public void place(Material material, int amount) {
        if (material == Material.IRON_BLOCK) {
            ironBlockPlaced += amount;
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (target.isOp())
                    target.sendMessage(player.getName() + " a posé x" + amount + " bloc de fer.");
            });
        } else if (material == Material.GOLD_BLOCK) {
            goldBlockPlaced += amount;
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (target.isOp())
                    target.sendMessage("§e" + player.getName() + " a posé x" + amount + " bloc d'or.");
            });
        } else if (material == Material.DIAMOND_BLOCK) {
            diamondBlockPlaced += amount;
            Bukkit.getOnlinePlayers().forEach(target -> {
                if (target.isOp())
                    target.sendMessage("§9" + player.getName() + " a posé x" + amount + " bloc de diamant.");
            });
        }
    }

    public void place(Material material) {
        place(material, 1);
    }

    public void reset() {
        ironBlockCrafted = 0;
        ironBlockPlaced = 0;
        goldBlockCrafted = 0;
        goldBlockPlaced = 0;
        diamondBlockCrafted = 0;
        diamondBlockPlaced = 0;
        kills = 0;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        if (CashPrice.getInstance().getManager().hasStarted())
            CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoards().forEach(PlayerScoreboard::update);
    }
}
