package fr.maximouz.cashprice.commands;

import fr.maximouz.cashprice.CashPrice;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FakeOpCommand implements CommandExecutor, Listener {

    private boolean active;

    public FakeOpCommand() {
        active = false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("on")) {

                if (active) {
                    sender.sendMessage("§cLe FakeOp est déjà actif..");
                    return true;
                }

                Bukkit.getOnlinePlayers().forEach(target -> {

                    if (target.isOp()) {
                        target.sendMessage("§aFakeOp activé !");
                        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2f, 2f);
                    } else {
                        target.sendMessage("§7§o[Server: " + target.getName() + " est maintenant opérateur du serveur]");
                    }

                });

                toggle();
                return true;

            } else if (args[0].equalsIgnoreCase("off")) {

                if (!active) {
                    sender.sendMessage("§cLe FakeOp n'est pas actif..");
                    return true;
                }

                Bukkit.getOnlinePlayers().forEach(target -> {

                    if (target.isOp()) {
                        target.sendMessage("§cFakeOp désactivé !");
                        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2f, 2f);
                    }

                });

                toggle();
                return true;

            }

        }

        sender.sendMessage("§c/" + label + " on/off §7(actuellement: "  + (active ? "§aON" : "§cOFF") + "§7)");
        return false;
    }

    private void toggle() {

        if (active) HandlerList.unregisterAll(this);
        else Bukkit.getPluginManager().registerEvents(this, CashPrice.getInstance());

        active = !active;

    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();

        if (!player.isOp()) {

            Bukkit.getOnlinePlayers().forEach(target -> {

                if (target.isOp()) {

                    target.sendMessage("§cFakeOp ⁕ §f" + player.getName() + " §7execute : §e" + event.getMessage());
                    target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 2f);

                }

            });

        }


    }

}
