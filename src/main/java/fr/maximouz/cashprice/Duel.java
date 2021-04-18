package fr.maximouz.cashprice;

import fr.maximouz.cashprice.commands.VanishCommand;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class Duel {

    private final Player player1;
    private final Location player1SpawnPoint;
    private final Location player1PreviousLocation;
    private final ItemStack[] player1PreviousContent;
    private final ItemStack[] player1PreviousArmor;
    private final double player1PreviousHealth;
    private final int player1PreviousFoodLevel;

    private final Player player2;
    private final Location player2SpawnPoint;
    private final Location player2PreviousLocation;
    private final ItemStack[] player2PreviousContent;
    private final ItemStack[] player2PreviousArmor;
    private final double player2PreviousHealth;
    private final int player2PreviousFoodLevel;

    private final BigDecimal reward;

    public Duel(Player player1, Location player1SpawnPoint, Player player2, Location player2SpawnPoint, BigDecimal reward) {
        this.player1 = player1;
        this.player1SpawnPoint = player1SpawnPoint;
        this.player1PreviousLocation = player1.getLocation();
        this.player1PreviousContent = player1.getInventory().getContents();
        this.player1PreviousArmor = player1.getInventory().getArmorContents();
        this.player1PreviousHealth = player1.getHealth();
        this.player1PreviousFoodLevel = player1.getFoodLevel();

        this.player2 = player2;
        this.player2PreviousLocation = player2.getLocation();
        this.player2SpawnPoint = player2SpawnPoint;
        this.player2PreviousContent = player2.getInventory().getContents();
        this.player2PreviousArmor = player2.getInventory().getArmorContents();
        this.player2PreviousHealth = player1.getHealth();
        this.player2PreviousFoodLevel = player1.getFoodLevel();

        this.reward = reward;
    }

    public void start() {

        setup(player1);
        setup(player2);

        player1.teleport(player1SpawnPoint);
        player1.sendMessage(" \n§9Vous entrez en duel contre §f" + player2.getName() +" §9 !\n§9Récompense: §e" + reward.toString() + "€\n ");

        player2.teleport(player2SpawnPoint);
        player2.sendMessage(" \n§9Vous entrez en duel contre §f" + player1.getName() +" §9 !\n§9Récompense: §e" + reward.toString() + "€\n ");

        player1.showPlayer(CashPrice.getInstance(), player2);
        VanishCommand.getInstance().getVanished().remove(player1.getUniqueId());
        player2.showPlayer(CashPrice.getInstance(), player1);
        VanishCommand.getInstance().getVanished().remove(player2.getUniqueId());

    }

    public void stop(Player winner) {

        Player opponent = getOpponent(winner);

        if (winner != null) {

            CashPrice.getInstance().getPoolManager().getPool(winner).addAmount(reward);

            Bukkit.getOnlinePlayers().forEach(target -> {

                if (target == winner) {

                    target.sendMessage("§eVous avez gagné votre duel !");
                    target.sendMessage("§e+" + reward.toString() + "€");
                    target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

                } else if (target == opponent) {

                    target.sendMessage("§cVous avez perdu votre duel !");
                    target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);

                } else {
                    target.sendMessage("§f" + winner.getName() + " §9a gagné le duel contre §f" + opponent.getName() + "§9 !");
                    sound(target);
                }

            });

            if (winner == player1) win(player1, player1PreviousLocation, player1PreviousContent, player1PreviousArmor, player1PreviousHealth, player1PreviousFoodLevel);
            else win(player2, player2PreviousLocation, player2PreviousContent, player2PreviousArmor, player2PreviousHealth, player2PreviousFoodLevel);

            opponent.setGameMode(GameMode.SPECTATOR);
            opponent.teleport(winner.getLocation().add(0, 2, 0));

            CashPrice.getInstance().getManager().dead(opponent);
            CashPrice.getInstance().getManager().check();

        } else {

            Bukkit.getOnlinePlayers().forEach(target -> {

                target.sendMessage("§cPersonne §9n'a gagné le duel entre §f" + player1.getName() + "§9 et §f" + player2.getName() + "§9..");
                sound(target);

            });

        }

    }

    private void setup(Player player) {
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
        player.getInventory().setItem(1, new ItemStack(Material.BOW));
        player.getInventory().setItem(2, new ItemStack(Material.GOLDEN_APPLE, 3));
        player.getInventory().setItem(7, new ItemStack(Material.COOKED_BEEF, 64));
        player.getInventory().setItem(8, new ItemStack(Material.ARROW, 16));
        player.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
        sound(player);
    }

    private void win(Player player, Location location, ItemStack[] contents, ItemStack[] armor, double previousHealth, int previousFoodLevel) {
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(location);
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
        player.setHealth(previousHealth);
        player.setHealth(previousFoodLevel);

        Statistic statistic = CashPrice.getInstance().getStatisticManager().getStatistic(player);
        if (statistic != null)
            statistic.setKills(statistic.getKills() + 1);

        Player opponent = getOpponent(player);
        opponent.getInventory().clear();

        ItemStack[] opponentContents = opponent == player1 ? player1PreviousContent : player2PreviousContent;

        for (ItemStack item : opponentContents)
            if (item != null)
                location.getWorld().dropItem(location, item);

    }

    private void sound(Player player) {
        for (int i = 0; i < 3; i++)
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 3f , 3f);
    }

    public Player getOpponent(Player player) {
        return player1 == player ? player2 : player2 == player ? player1 : null;
    }

}
