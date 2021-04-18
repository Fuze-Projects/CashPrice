package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!CashPrice.getInstance().getManager().hasStarted()) {
            event.setCancelled(true);
            return;
        }

        boolean damagerIsPlayer = event.getDamager().getType() == EntityType.PLAYER;
        boolean damagedIsPlayer = event.getEntity().getType() == EntityType.PLAYER;

        if (damagerIsPlayer && damagedIsPlayer && !CashPrice.getInstance().getManager().hasPvp()) {
            event.setCancelled(true);
            event.getDamager().sendMessage("§cLe pvp n'est pas encore actif !");
            return;
        }

        if (damagedIsPlayer && !CashPrice.getInstance().getManager().hasVulnerability())
            event.setCancelled(true);

    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {

        if (!CashPrice.getInstance().getManager().hasStarted() || !CashPrice.getInstance().getManager().hasVulnerability())
            event.setCancelled(true);

    }

}
