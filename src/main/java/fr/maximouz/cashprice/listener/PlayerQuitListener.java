package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.events.PlayerDeathTriggerEvent;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();

        CashPrice.getInstance().getPlayerScoreboardManager().unregister(player);

        if (CashPrice.getInstance().getManager().hasStarted()) {

            if (CashPrice.getInstance().getManager().isParticipating(player) && CashPrice.getInstance().getManager().isAlive(player)) {

                CashPrice.getInstance().getManager().dead(player);
                CashPrice.getInstance().getManager().check();
                event.setQuitMessage(player.getName() + "§c est mort déconnecté !");

                PlayerDeathTriggerEvent playerDeathTriggerEvent = new PlayerDeathTriggerEvent(player, null);
                Bukkit.getPluginManager().callEvent(playerDeathTriggerEvent);

                if (!playerDeathTriggerEvent.isCancelled()) {

                    for (ItemStack item : player.getInventory().getContents())
                        if (item != null)
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                    
                    player.getInventory().clear();

                }

            }

        }

        CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoards().forEach(PlayerScoreboard::update);

    }

}
