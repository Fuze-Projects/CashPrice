package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        event.setFormat((player.isOp() ? "§cOpérateur " : "§f") + "%s" + (player.isOp() ? "§f" : "§7") + ": %s");

        if (CashPrice.getInstance().getManager().hasStarted() && (!CashPrice.getInstance().getManager().isParticipating(player) || !CashPrice.getInstance().getManager().isAlive(player)) && !player.isOp()) {

            String msg = "§7[Spectateur] §f" + player.getName() + "§7: " + event.getMessage();

            Bukkit.getOnlinePlayers().forEach(target -> {
                if (target.isOp() && target != player)
                    target.sendMessage(msg);
            });

            CashPrice.getInstance().getManager().getDeadPlayers().forEach(target -> target.sendMessage(msg));

            event.setCancelled(true);

        }


    }

}
