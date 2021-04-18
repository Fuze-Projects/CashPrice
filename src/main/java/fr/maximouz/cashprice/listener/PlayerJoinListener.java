package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (player.isOp())
            player.setDisplayName(ChatColor.RED + player.getDisplayName());

        CashPrice.getInstance().getPlayerScoreboardManager().register(player);

        if (CashPrice.getInstance().getManager().getSpawnPoint() != null)
            player.teleport(CashPrice.getInstance().getManager().getSpawnPoint().clone().add(0, 1, 0));

        if (CashPrice.getInstance().getManager().hasStarted()) {

            if (!player.isOp()) {

                player.sendMessage("§cLa partie a déjà commencé, vous êtes spéctateur.");
                player.setGameMode(GameMode.SPECTATOR);

            }

        }

        player.getInventory().clear();

        CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoards().forEach(PlayerScoreboard::update);

        Bukkit.getScheduler().runTaskLater(CashPrice.getInstance(), () -> player.setGameMode(CashPrice.getInstance().getManager().hasStarted() ? GameMode.SPECTATOR : GameMode.ADVENTURE), 2L);

        event.setJoinMessage(null);

    }

    @EventHandler
    public void onPreJoin(PlayerLoginEvent event) {

        if (CashPrice.getInstance().getManager().hasStarted() && (!event.getPlayer().isWhitelisted() && !event.getPlayer().isOp())) {

            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            event.setKickMessage("§cLa partie a déjà commencé..");

        }

    }

    @EventHandler
    public void onNether(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onNether(EntityPortalEvent event) {
        event.setCancelled(true);
    }

}
