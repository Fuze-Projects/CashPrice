package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {

        if (!CashPrice.getInstance().getManager().hasStarted() || Utils.isInCenterArea(event.getLocation()))
            event.setCancelled(true);

    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {

        if (!CashPrice.getInstance().getManager().hasStarted() || Utils.isInCenterArea(event.getBlock().getLocation()))
            event.setCancelled(true);

    }

}
