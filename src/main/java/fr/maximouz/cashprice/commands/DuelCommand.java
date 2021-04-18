package fr.maximouz.cashprice.commands;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Duel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class DuelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Commande utilisable en tant que joueur..");
                return false;
            }

            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("help")) {

                sender.sendMessage("§c/duel spawn1 §7définir le premier spawnpoint");
                sender.sendMessage("§c/duel spawn2 §7définir le deuxième spawnpoint");
                sender.sendMessage("§c/duel <joueur1> <joueur2> <récompense> §7commencer un duel");
                sender.sendMessage("§c/duel stop §7arrêter le duel en cours");
                return true;

            } else if (args[0].equalsIgnoreCase("spawn1") || args[0].equalsIgnoreCase("spawn2")) {

                Location location = player.getLocation();

                if (args[0].equalsIgnoreCase("spawn1"))
                    CashPrice.getInstance().getDuelManager().setFirstSpawnPoint(location);
                else
                    CashPrice.getInstance().getDuelManager().setSecondSpawnPoint(location);

                player.sendMessage("§aPoint d'apparition défini !");

                return true;

            } else if (args[0].equalsIgnoreCase("stop")) {

                Duel duel = CashPrice.getInstance().getDuelManager().getDuel();

                if (duel != null) {

                    duel.stop(null);
                    sender.sendMessage("§cVous avez stoppé le duel en cours..");

                } else {

                    sender.sendMessage("§cAucun duel n'est en cours..");

                }

                return true;

            }

        } else if (args.length == 3) {

            if (!CashPrice.getInstance().getManager().hasStarted()) {
                sender.sendMessage("§cLa partie n'a pas commencé..");
                return false;
            }

            if (CashPrice.getInstance().getDuelManager().getDuel() != null) {
                sender.sendMessage("§cUn duel est déjà en cours..");
                return false;
            }

            Player player1 = Bukkit.getPlayer(args[0]);

            if (player1 != null) {

                Player player2 = Bukkit.getPlayer(args[1]);

                if (player2 != null) {

                    if (player1 != player2) {

                        if (!CashPrice.getInstance().getManager().isParticipating(player1)) {
                            sender.sendMessage("§cLe joueur §f" + player1.getName() + "§c ne participe pas à l'event..");
                            return false;
                        } else if (!CashPrice.getInstance().getManager().isParticipating(player2)) {
                            sender.sendMessage("§cLe joueur §f" + player2.getName() + "§c ne participe pas à l'event..");
                            return false;
                        } else if (!CashPrice.getInstance().getManager().isAlive(player1)) {
                            sender.sendMessage("§cLe joueur §f" + player1.getName() + "§c est mort..");
                            return false;
                        } else if (!CashPrice.getInstance().getManager().isAlive(player2)) {
                            sender.sendMessage("§cLe joueur §f" + player2.getName() + "§c est mort..");
                            return false;
                        }

                        try {

                            BigDecimal reward = new BigDecimal(args[2]);

                            if (!CashPrice.getInstance().getDuelManager().hasDuel()) {

                                if (CashPrice.getInstance().getDuelManager().getFirstSpawnPoint() != null) {

                                    if (CashPrice.getInstance().getDuelManager().getSecondSpawnPoint() != null) {

                                        CashPrice.getInstance().getDuelManager().duel(player1, player2, reward);
                                        if (sender instanceof Player && sender != player1 && sender != player2)
                                            ((Player) sender).teleport(player1.getLocation().add(0, 2, 0));

                                    } else {

                                        sender.sendMessage("§cDéfinissez le spawnpoint2 avec /duel spawn2");

                                    }

                                } else {

                                    sender.sendMessage("§cDéfinissez le spawnpoint1 avec /duel spawn1");

                                }

                            } else {

                                sender.sendMessage("§cUn duel est déjà en cours..");

                            }

                        } catch (NumberFormatException e) {
                            sender.sendMessage("§7" + args[2] + "§c n'est pas un nombre valide..");
                        }

                    } else {

                        sender.sendMessage("§cVous avez entré deux joueurs identiques..");

                    }

                } else {

                    sender.sendMessage("§cLe joueur §f" + args[1] + "§c n'existe pas..");

                }

            } else {

                sender.sendMessage("§cLe joueur §f" + args[0] + "§c n'existe pas..");

            }

            return true;

        }

        sender.sendMessage("§c/duel help");

        return false;
    }

}
