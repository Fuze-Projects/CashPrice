package fr.maximouz.cashprice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (event.getView().getTitle().startsWith("§aInventaire de §f"))
            event.setCancelled(true);

    }

}
