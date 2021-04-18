package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (!player.isOp()) {
            if (!CashPrice.getInstance().getManager().hasStarted()) {

                event.setCancelled(true);
                player.sendMessage("§cLa partie n'a pas encore commencée !");

            } else if (Utils.isInCenterArea(event.getBlock().getLocation())) {

                event.setCancelled(true);
                player.sendMessage("§cVous ne pouvez pas casser les blocs ici..");

            }
        }

    }

}
