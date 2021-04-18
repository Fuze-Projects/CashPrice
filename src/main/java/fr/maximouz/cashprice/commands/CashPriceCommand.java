package fr.maximouz.cashprice.commands;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Utils;
import fr.maximouz.cashprice.managers.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class CashPriceCommand implements CommandExecutor, Listener {

    private final List<Player> settingArea;

    public CashPriceCommand() {
        settingArea = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, CashPrice.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {

                sender.sendMessage("§e/" + label + " setarea §7définir la zone");
                sender.sendMessage("§e/" + label + " start §7commencer la partie");
                sender.sendMessage("§e/" + label + " help");

                return true;

            } else if (args[0].equalsIgnoreCase("setarea")) {

                if (sender instanceof Player) {

                    Player player = (Player) sender;

                    if (CashPrice.getInstance().getManager().hasStarted()) {
                        sender.sendMessage("§cLa partie a déjà commencée");
                        return false;
                    }

                    player.sendMessage("§aSéléctionnez un bloc qui sera le centre !");
                    settingArea.add(player);

                } else {

                    sender.sendMessage("Commande utilisable en tant que joueur");

                }

                return true;

            } else if (args[0].equalsIgnoreCase("start")) {

                if (!CashPrice.getInstance().getManager().hasStarted()) {

                    if (CashPrice.getInstance().getManager().getSpawnPoint() != null) {
                        long playersCount = Bukkit.getOnlinePlayers().stream().filter(player -> !player.isOp()).count();
                        if (playersCount >= CashPrice.minPlayers) CashPrice.getInstance().getManager().start();
                        else
                            sender.sendMessage("§cIl manque des joueurs pour commencer la partie §7" + playersCount + "/" + CashPrice.minPlayers + "\n§7§o(Les joueurs op ne sont pas pris en compte.)");
                    } else sender.sendMessage("§cDéfinissez d'abord la zone avec /" + label + " setarea");

                } else sender.sendMessage("§cEvent déjà démarré..");

                return true;

            } else if (args[0].equalsIgnoreCase("save")) {

                if (CashPrice.getInstance().getManager().hasStarted()) {

                    sender.sendMessage("§eSauvegarde en cours..");
                    CashPrice.getInstance().getManager().saveGame();
                    sender.sendMessage("§aSauvegarde réussie.");
                    sender.sendMessage("§7Il faut maintenant redémarrer le serveur.");

                } else {

                    sender.sendMessage("§cLa partie n'a pas démarré..");

                }

                return true;

            } else if (args[0].equalsIgnoreCase("load")) {

                if (!CashPrice.getInstance().getManager().hasStarted()) {

                    sender.sendMessage("§eChargement en cours..");
                    CashPrice.getInstance().getManager().loadGame();

                } else {

                    sender.sendMessage("§cLa partie a déjà démarré..");

                }

            } else if (args[0].equalsIgnoreCase("setdrop")) {

                if (CashPrice.getInstance().getManager().hasStarted()) {

                    Manager.DROP_ITEM_TO_REWARD = !Manager.DROP_ITEM_TO_REWARD;
                    sender.sendMessage("§eLes joueurs peuvent jeter les blocs pour les comptabiliser : §b" + Manager.DROP_ITEM_TO_REWARD);

                } else {

                    sender.sendMessage("§cLa partie n'a pas démarré..");

                }

                return true;

            }

        } else if (args.length == 2 && args[0].equalsIgnoreCase("setmin")) {

            if (!CashPrice.getInstance().getManager().hasStarted()) {

                try {

                    CashPrice.minPlayers = Integer.parseInt(args[1]);
                    sender.sendMessage("§aNouveau minimum de joueurs : §b" + CashPrice.minPlayers);

                } catch (NumberFormatException e) {
                    sender.sendMessage("§cNombre invalide.");
                }

            } else {

                sender.sendMessage("§cLa partie a déjà commencé..");

            }

            return true;

        }

        sender.sendMessage("§c/" + label + " help");
        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        settingArea.remove(event.getPlayer());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (settingArea.contains(player)) {

            event.setCancelled(true);
            settingArea.remove(player);
            if (CashPrice.getInstance().getManager().getPreviousSpawnPoint() != null) {
                player.sendMessage("\n§cSuppression de l'ancienne zone..");
                Utils.removeArea(CashPrice.getInstance().getManager().getPreviousSpawnPoint());
                player.sendMessage("§cZone supprimée !");
            }
            CashPrice.getInstance().getManager().setPreviousSpawnPoint(event.getBlock().getLocation());
            CashPrice.getInstance().getManager().setSpawnPoint(event.getBlock().getLocation());
            player.sendMessage("§eCréation de la zone..");
            Utils.createArea(event.getBlock().getLocation());
            player.sendMessage("§aZone créée !\n ");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            World world = Bukkit.getWorld("world");
            world.getWorldBorder().setCenter(event.getBlock().getLocation());
            world.getWorldBorder().setSize(2000, 0L);

        }

    }

}
