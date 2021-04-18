package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Statistic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftItemListener implements Listener {

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {

        Player player = (Player) event.getWhoClicked();
        Statistic statistic = CashPrice.getInstance().getStatisticManager().getStatistic(player);

        if (statistic != null) {

            statistic.craft(event.getCurrentItem().getType());

            if (event.getCurrentItem().getType() == Material.ENCHANTED_GOLDEN_APPLE) {

                Bukkit.getOnlinePlayers().forEach(target -> {

                    if (target.isOp())
                        target.sendMessage("§6§k!!§r §f" + player.getName() + " §6a crafté une pomme de notch !");

                });

            }

        }

    }

}
