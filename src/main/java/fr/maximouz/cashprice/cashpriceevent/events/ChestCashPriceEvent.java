package fr.maximouz.cashprice.cashpriceevent.events;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Utils;
import fr.maximouz.cashprice.cashpriceevent.CashPriceEvent;
import fr.maximouz.cashprice.cashpriceevent.CashPriceEventType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class ChestCashPriceEvent extends CashPriceEvent {

    private Location currentChestLocation;
    private Inventory currentInventory;

    public ChestCashPriceEvent() {
        super(CashPriceEventType.CHEST,60 * 10, 6);
    }

    @Override
    public void trigger() {

        if (currentChestLocation != null) {

            currentInventory.clear();
            currentChestLocation.getBlock().setType(Material.AIR);

        }

        World world = Bukkit.getWorld("world");
        WorldBorder worldBorder = world.getWorldBorder();
        double size = worldBorder.getSize() / 2;

        Location randomLocation = Utils.getRandomLocation(CashPrice.getInstance().getManager().getSpawnPoint(), (int) size - 1);
        randomLocation.setY(world.getHighestBlockYAt(randomLocation) + 1);

        randomLocation.getBlock().setType(Material.CHEST);
        currentChestLocation = randomLocation;
        currentInventory = Bukkit.createInventory(null, 3 * 9, "§dCoffre Magique");

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
        book.setItemMeta(meta);

        currentInventory.setItem(13, book);

        currentInventory.setItem(3, new ItemStack(Material.IRON_ORE));
        currentInventory.setItem(4, new ItemStack(Material.IRON_ORE, 2));
        currentInventory.setItem(5, new ItemStack(Material.IRON_ORE, 3));
        currentInventory.setItem(12, new ItemStack(Material.IRON_ORE, 4));
        currentInventory.setItem(14, new ItemStack(Material.IRON_ORE, 5));
        currentInventory.setItem(21, new ItemStack(Material.GOLD_ORE, 2));
        currentInventory.setItem(22, new ItemStack(Material.GOLD_ORE, 3));
        currentInventory.setItem(23, new ItemStack(Material.DIAMOND, 3));

        Bukkit.broadcastMessage("§5§m                                                            ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§d§lCoffre Magique"));
        Bukkit.broadcastMessage("§f ⁕ Le coffre magique est apparu quelque part");
        Bukkit.broadcastMessage("§f ⁕ Il est utile pour monter la somme de votre cagnotte !");
        Bukkit.broadcastMessage(Utils.getCenteredText("§7x: §f" + randomLocation.getBlockX() + "  §7y: §f" + randomLocation.getBlockY() + "  §7z: §f" + randomLocation.getBlockZ()));
        Bukkit.broadcastMessage("§5§m                                                            ");

        Bukkit.getOnlinePlayers().forEach(player -> {

            for (int i = 0; i < 3; i++)
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 3f, 3f);

        });

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block != null) {

            Location loc = block.getLocation();

            if (currentChestLocation != null && loc.getBlockX() == currentChestLocation.getBlockX() && loc.getBlockY() == currentChestLocation.getBlockY() && loc.getBlockZ() == currentChestLocation.getBlockZ()) {

                event.setCancelled(true);
                player.openInventory(currentInventory);

            }

        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Location loc = event.getBlock().getLocation();
        if (currentChestLocation != null && loc.getBlockX() == currentChestLocation.getBlockX() && loc.getBlockY() == currentChestLocation.getBlockY() && loc.getBlockZ() == currentChestLocation.getBlockZ() && !currentInventory.isEmpty())
            event.setCancelled(true);

    }

}
