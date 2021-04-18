package fr.maximouz.cashprice;

import fr.maximouz.cashprice.events.DeathMatchEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.ChatPaginator;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String getCenteredText(String base) {
        StringBuilder builder = new StringBuilder();
        int startPos = getPosCenter(ChatColor.stripColor(base));

        for (int i = 0; i < startPos; i++) {
            builder.append(" ");
        }

        builder.append(base);

        return builder.toString();
    }

    private static int getPosCenter(String text) {
        return (ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH / 2) - (text.length() / 2);
    }

    public static String formatTime(long time) {
        long secs = TimeUnit.MILLISECONDS.toSeconds(time);
        if (secs < 60)
            return String.format("%ds", TimeUnit.MILLISECONDS.toSeconds(time));
        else if (secs < 3600)
            return String.format("%dm%d", TimeUnit.MILLISECONDS.toMinutes(time), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        else
            return String.format("%dh%d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)));
    }

    public static void createArea(Location center) {
        int radius = 16;

        for (int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x++) {
            for (int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z++) {
                if ((center.getBlockX() - x) * (center.getBlockX() - x) + (center.getBlockZ() - z) * (center.getBlockZ() - z) <= (radius * radius)) {
                    Block block = new Location(center.getWorld(), x, center.getY(), z).getBlock();
                    block.setType(Material.RED_WOOL);
                }
            }
        }
    }

    public static void removeArea(Location center) {
        int radius = 16;

        double blockX = Math.ceil(center.getBlockX());
        double blockZ = Math.ceil(center.getBlockZ());

        for (double x = blockX - radius; x <= blockX + radius; x++) {
            for (double z = blockZ - radius; z <= blockZ + radius; z++) {
                if ((blockX - x) * (blockX - x) + (blockZ - z) * (blockZ - z) <= (radius * radius)) {
                    new Location(center.getWorld(), x, center.getY(), z).getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public static boolean isInCenterArea(Location location) {
        return CashPrice.getInstance().getManager().getSpawnPoint() != null && CashPrice.getInstance().getManager().getSpawnPoint().getWorld() == location.getWorld() && location.distance(CashPrice.getInstance().getManager().getSpawnPoint()) < 16.5;
    }

    public static void spawnFireWork(Location location) {

        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.setPower(2);
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED)
                .flicker(true)
                .withFade(Color.AQUA)
                .build()
        );

        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();

    }

    public static void deathMatch() {

        Bukkit.getPluginManager().callEvent(new DeathMatchEvent());

        Cuboid deathMatchArea = CashPrice.getInstance().getManager().getDeathMatchArea();

        Bukkit.broadcastMessage("§4§m                                                       ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§c§lDeathMatch"));
        Bukkit.broadcastMessage(" §c⁕ §fLa partie touche à sa fin, battez vous jusqu'à la mort");
        Bukkit.broadcastMessage(" §c⁕ §fQue le meilleur gagne");
        Bukkit.broadcastMessage(Utils.getCenteredText("§c§lBonne Chance"));
        Bukkit.broadcastMessage("§4§m                                                       ");

        Bukkit.getOnlinePlayers().forEach(player -> {

            double randomX = ThreadLocalRandom.current().nextDouble(deathMatchArea.getLowerX(), deathMatchArea.getUpperX());
            double randomZ = ThreadLocalRandom.current().nextDouble(deathMatchArea.getLowerZ(), deathMatchArea.getUpperZ());
            double y = deathMatchArea.getWorld().getHighestBlockYAt(deathMatchArea.getCenter());

            if (player.isOp() || !CashPrice.getInstance().getManager().isParticipating(player) || !CashPrice.getInstance().getManager().isAlive(player)) {

                Location randomLocation = new Location(deathMatchArea.getWorld(), randomX, y + 10, randomZ);
                player.teleport(randomLocation);

            } else {

                Location randomLocation = new Location(deathMatchArea.getWorld(), randomX, y + 1, randomZ);
                player.teleport(randomLocation);

            }

        });

        CashPrice.getInstance().getEventManager().getEvents().forEach(cashPriceEvent -> {

            if (cashPriceEvent.getTask() != null)
                cashPriceEvent.getTask().cancel();

            cashPriceEvent.setTask(null);
            HandlerList.unregisterAll(cashPriceEvent);

        });

    }

    public static Location getRandomLocation(Location loc1, Location loc2) {
        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.max(loc1.getX(), loc2.getX());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        return new Location(loc1.getWorld(), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
    }

    public static Location getRandomLocation(Location center, int radius) {
        int maxX = center.getBlockX() + radius;
        int minX = center.getBlockX() - radius;

        int maxZ = center.getBlockZ() + radius;
        int minZ = center.getBlockZ() - radius;

        Random r = new Random();

        int ix = r.nextInt(Math.max(Math.abs(maxX - minX), 1)) + minX;
        double x = ix + 0.5;
        int iz = r.nextInt(Math.max(Math.abs(maxZ - minZ), 1)) + minZ;
        double z = iz + 0.5;

        return new Location(center.getWorld(), x, center.getWorld().getHighestBlockYAt(ix, iz), z);
    }

    private static double randomDouble(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble(Math.abs(max - min + 1));
    }

}
