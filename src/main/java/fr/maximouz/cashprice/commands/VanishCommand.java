package fr.maximouz.cashprice.commands;

import fr.maximouz.cashprice.CashPrice;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VanishCommand implements CommandExecutor, Listener {

    private static VanishCommand INSTANCE = new VanishCommand();
    private final List<UUID> vanished;

    public VanishCommand() {
        vanished = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, CashPrice.getInstance());
    }

    public static VanishCommand getInstance() {
        return INSTANCE;
    }

    public List<UUID> getVanished() {
        return vanished;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (vanished.contains(player.getUniqueId())) {

                vanished.remove(player.getUniqueId());

                Bukkit.getOnlinePlayers().forEach(target -> {

                    if (target != player && !target.isOp())
                        target.showPlayer(CashPrice.getInstance(), player);

                });

                player.sendMessage("§eVous êtes visible.");

            } else {

                vanished.add(player.getUniqueId());

                Bukkit.getOnlinePlayers().forEach(target -> {

                    if (target != player && !target.isOp())
                        target.hidePlayer(CashPrice.getInstance(), player);

                });

                player.sendMessage("§aVous êtes invisible.");

            }

        }

        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        vanished.remove(event.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        Bukkit.getOnlinePlayers().forEach(target -> {

            if (target != player && !target.isOp())
                target.showPlayer(CashPrice.getInstance(), player);

        });

    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {

        if (vanished.contains(event.getEntity().getUniqueId()))
            event.setCancelled(true);

    }

}
