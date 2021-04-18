package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Pool;
import fr.maximouz.cashprice.Utils;
import fr.maximouz.cashprice.managers.Manager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!CashPrice.getInstance().getManager().hasStarted() && !player.isOp()) {

            event.setCancelled(true);
            player.sendMessage("§cLa partie n'a pas encore commencée !");

        } else if (Utils.isInCenterArea(event.getBlock().getLocation()) && CashPrice.getInstance().getManager().isParticipating(player) && CashPrice.getInstance().getManager().isAlive(player)) {

            BigDecimal reward = new BigDecimal(
                    block.getType() == Material.IRON_BLOCK ? "0.50" :
                            block.getType() == Material.GOLD_BLOCK ? "1.00" :
                                    block.getType() == Material.DIAMOND_BLOCK ? "5.00" : "0.00"
            ).setScale(2, RoundingMode.HALF_UP);


            if (block.getType() != Material.IRON_BLOCK && block.getType() != Material.GOLD_BLOCK && block.getType() != Material.DIAMOND_BLOCK && !player.isOp()) {

                event.setCancelled(true);
                player.sendMessage("§cVous ne pouvez pas poser ce bloc ici..");
                return;

            }

            CashPrice.getInstance().getStatisticManager().getStatistic(player).place(event.getBlock().getType());

            Pool pool = CashPrice.getInstance().getPoolManager().getPool(player);
            pool.addAmount(reward);

            player.sendMessage("§e+" + reward.toString() + "€");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event)  {

        if (!Manager.DROP_ITEM_TO_REWARD)
            return;

        Player player = event.getPlayer();

        if (CashPrice.getInstance().getManager().isParticipating(player) && CashPrice.getInstance().getManager().isAlive(player)) {

            Item itemDrop = event.getItemDrop();

            if (Utils.isInCenterArea(itemDrop.getLocation())) {

                ItemStack item = itemDrop.getItemStack();

                if (item.getType() == Material.IRON_BLOCK || item.getType() == Material.GOLD_BLOCK || item.getType() == Material.DIAMOND_BLOCK) {

                    BigDecimal reward = new BigDecimal(
                            item.getType() == Material.IRON_BLOCK ? "0.50" :
                                    item.getType() == Material.GOLD_BLOCK ? "1.00" :
                                            "5.00"
                    ).setScale(2, RoundingMode.HALF_UP);

                    reward = reward.multiply(BigDecimal.valueOf(item.getAmount()));

                    Location location = itemDrop.getLocation();

                    CashPrice.getInstance().getStatisticManager().getStatistic(player).place(item.getType(), item.getAmount());

                    for (int i = 0; i < item.getAmount(); i++) {

                        location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
                        location.getBlock().setType(item.getType());

                    }

                    Pool pool = CashPrice.getInstance().getPoolManager().getPool(player);
                    pool.addAmount(reward);

                    player.sendMessage("§e+" + reward.toString() + "€");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

                    itemDrop.remove();

                }

            }
        }

    }

}
