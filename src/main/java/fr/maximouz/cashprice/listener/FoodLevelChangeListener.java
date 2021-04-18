package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        if (!CashPrice.getInstance().getManager().hasStarted())
            event.setCancelled(true);

    }

}
