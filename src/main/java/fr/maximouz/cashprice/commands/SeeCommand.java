package fr.maximouz.cashprice.commands;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Statistic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SeeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!CashPrice.getInstance().getManager().hasStarted()) {
            sender.sendMessage("§cLa partie n'a pas commencé..");
            return false;
        }

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage("§c/" + label + " <joueur>");
                return false;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage("§cLe joueur §f" + args[0] + "§c n'est pas connecté..");
                return false;
            }

            boolean isAlive = CashPrice.getInstance().getManager().isAlive(target);

            Inventory inventory = Bukkit.createInventory(null, 6 * 9, "§aInventaire de §f" + target.getName());

            ItemStack glass = new ItemStack(isAlive ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,1);
            ItemMeta glassMeta = glass.getItemMeta();
            glassMeta.setDisplayName("§r" + (isAlive ? "§a" : "§c") + "§l" + target.getName());
            glass.setItemMeta(glassMeta);

            int[] glassSlots = {
                    36, 37, 38, 39, 40, 41, 42, 43, 44, 49
            };

            for (int slot : glassSlots)
                inventory.setItem(slot, glass);

            int[] armorSlots = {
                    45, 46, 47, 48
            };

            inventory.setItem(armorSlots[0], target.getInventory().getHelmet());
            inventory.setItem(armorSlots[1], target.getInventory().getChestplate());
            inventory.setItem(armorSlots[2], target.getInventory().getLeggings());
            inventory.setItem(armorSlots[3], target.getInventory().getBoots());

            ItemStack[] contents = target.getInventory().getContents();

            for (int i = 0; i < Math.min(35, contents.length); i++)
                inventory.setItem(i, contents[i]);

            ItemStack infoItem = new ItemStack(Material.PAPER);
            ItemMeta infoItemMeta = infoItem.getItemMeta();

            Statistic statistic = CashPrice.getInstance().getStatisticManager().getStatistic(target);

            if (statistic != null) {
                // Blocs de fer craftés & posés
                int ironBlockCrafted = statistic.getIronBlockCrafted();
                int ironBlockPlaced = statistic.getIronBlockPlaced();

                ItemStack blockPlacedItem = new ItemStack(Material.IRON_BLOCK);
                ItemMeta blockPlacedItemMeta = blockPlacedItem.getItemMeta();

                blockPlacedItemMeta.setDisplayName("§fBloc de fer");
                blockPlacedItemMeta.setLore(Arrays.asList(
                        "§fCraftés: §b" + ironBlockCrafted + " §7(" + CashPrice.getInstance().getStatisticManager().getAllIronBlockCrafted() + ")",
                        "§fPosés: §b" + ironBlockPlaced + " §7(" + CashPrice.getInstance().getStatisticManager().getAllIronBlockPlaced() + ")"
                ));
                blockPlacedItem.setItemMeta(blockPlacedItemMeta);

                inventory.setItem(50, blockPlacedItem);

                // Blocs d'or craftés & posés
                int goldBlockCrafted = statistic.getGoldBlockCrafted();
                int goldBlockPlaced = statistic.getGoldBlockPlaced();

                blockPlacedItem = new ItemStack(Material.GOLD_BLOCK);
                blockPlacedItemMeta.setDisplayName("§eBloc d'or");
                blockPlacedItemMeta.setLore(Arrays.asList(
                        "§eCraftés: §b" + goldBlockCrafted + " §7(" + CashPrice.getInstance().getStatisticManager().getAllGoldBlockCrafted() + ")",
                        "§ePosés: §b" + goldBlockPlaced + " §7(" + CashPrice.getInstance().getStatisticManager().getAllGoldBlockPlaced() + ")"
                ));
                blockPlacedItem.setItemMeta(blockPlacedItemMeta);

                inventory.setItem(51, blockPlacedItem);

                // Blocs de diamant craftés & posés
                int diamondBlockCrafted = statistic.getDiamondBlockCrafted();
                int diamondBlockPlaced = statistic.getDiamondBlockPlaced();

                blockPlacedItem = new ItemStack(Material.DIAMOND_BLOCK);
                blockPlacedItemMeta.setDisplayName("§9Bloc de diamant");
                blockPlacedItemMeta.setLore(Arrays.asList(
                        "§9Craftés: §b" + diamondBlockCrafted + " §7(" + CashPrice.getInstance().getStatisticManager().getAllDiamondBlockCrafted() + ")",
                        "§9Posés: §b" + diamondBlockPlaced + " §7(" + CashPrice.getInstance().getStatisticManager().getAllDiamondBlockPlaced() + ")"
                ));
                blockPlacedItem.setItemMeta(blockPlacedItemMeta);

                inventory.setItem(52, blockPlacedItem);

                // Infos supplémentaires
                infoItemMeta.setDisplayName("§7§lINFOS");
                infoItemMeta.setLore(Arrays.asList(
                        "§fKills: §a" + statistic.getKills(),
                        "§fCagnotte: §e" + CashPrice.getInstance().getPoolManager().getPool(target).getAmount().toString() + "€",
                        "§fVie: §c" + (isAlive ? target.getHealth() + "❤" : "MORT"),
                        "§fNourriture: §6" + (isAlive ? target.getFoodLevel() : "MORT")
                ));
            } else {

                infoItemMeta.setDisplayName("§f" + target.getName());
                infoItemMeta.setLore(Arrays.asList("§cCe joueur ne participe", "§cpas à l'event."));

            }

            infoItem.setItemMeta(infoItemMeta);

            inventory.setItem(53, infoItem);

            player.openInventory(inventory);

        } else {

            sender.sendMessage("Commande utilisable en tant que joueur");

        }

        return false;
    }

}
