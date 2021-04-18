package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Statistic;
import fr.maximouz.cashprice.events.PlayerDeathTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (event.getEntity().getType() != EntityType.PLAYER)
            return;

        if (!CashPrice.getInstance().getManager().hasStarted()) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) event.getEntity();

        if (!CashPrice.getInstance().getManager().hasVulnerability()) {
            event.setCancelled(true);
            return;
        }

        if (player.getHealth() - event.getFinalDamage() <= 0.0D) {

            if (CashPrice.getInstance().getManager().isParticipating(player) && CashPrice.getInstance().getManager().isAlive(player)) {
                trigger(player);
            } else {
                player.teleport(CashPrice.getInstance().getManager().getSpawnPoint().clone().add(0, 4, 0));
            }
            event.setCancelled(true);
        }
    }

    public void trigger(Player player) {

        Player killer = player.getKiller();
        String deathMessage = null;
        Location respawnLocation = player.getLocation().clone().add(0, 3, 0);

        PlayerDeathTriggerEvent event = new PlayerDeathTriggerEvent(player, killer);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        if (killer != null) {

            respawnLocation = killer.getLocation().add(0, 3, 0);
            Statistic statistic = CashPrice.getInstance().getStatisticManager().getStatistic(killer);

            if (statistic != null) {

                statistic.setKills(statistic.getKills() + 1);
                deathMessage = killer.getName() + "§c a tué §f" + player.getName() + "§c !";
                killer.playSound(killer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 2f);

            }

        } else if (player.getLastDamageCause() != null) {

            switch (player.getLastDamageCause().getCause()) {
                default:
                    deathMessage = player.getName() + "§c est mort !";
                    break;
                case DROWNING:
                    deathMessage = player.getName() + "§c est mort §bnoyé§c !";
                    break;
                case SUICIDE:
                    deathMessage = player.getName() + "§c a mis fin à ses jours !";
                    break;
                case LAVA:
                    deathMessage = player.getName() + "§c est mort dans la §4lave§c !";
                    break;
                case FALL:
                    deathMessage = player.getName() + "§c est mort d'une chute §4fatale§c !";
                    break;
                case ENTITY_EXPLOSION:
                    deathMessage = player.getName() + "§c a été tué par un §acreeper§c !";
                    break;
                case POISON:
                    deathMessage = player.getName() + "§c est mort empoisonné !";
                    break;
                case FIRE:
                    deathMessage = player.getName() + "§c a brûlé vif !";
                    break;
            }

        } else {

            deathMessage = player.getName() + "§c est mort !";

        }

        if (CashPrice.getInstance().getManager().isParticipating(player) && CashPrice.getInstance().getManager().isAlive(player)) {

            CashPrice.getInstance().getManager().dead(player);
            CashPrice.getInstance().getManager().check();

        }

        final String finalDeathMessage = deathMessage;

        Bukkit.getOnlinePlayers().forEach(target -> {

            if (finalDeathMessage != null)
                target.sendMessage(finalDeathMessage);
            CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoard(target).update();

        });

        ItemStack[] drops = player.getInventory().getContents();
        Location dropLocation = player.getLocation();

        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(respawnLocation);

        for (ItemStack item : drops)
            if (item != null)
                dropLocation.getWorld().dropItemNaturally(dropLocation, item);
    }

}
