package fr.maximouz.cashprice.commands;

import fr.maximouz.cashprice.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("concept")) {

                sender.sendMessage(Utils.getCenteredText("§7§lCONCEPT"));
                sender.sendMessage("§7Dans la map se trouve un grand cercle en laine");
                sender.sendMessage("§7rouge dans lequel vous devez y poser tous les");
                sender.sendMessage("§7§lblocs§r§7 de minerais que vous fabriquez au cours");
                sender.sendMessage("§7de la partie. Cependant, seul le §ldernier");
                sender.sendMessage("§7§lsurvivant§r§7 gagne l'argent réuni.");
                return true;

            } else if (args[0].equalsIgnoreCase("infos")) {

                sender.sendMessage(Utils.getCenteredText("§7§lINFOS"));
                sender.sendMessage("§f ⁕ Bloc de fer: 0.50€");
                sender.sendMessage("§e ⁕ Bloc d'or: 1€");
                sender.sendMessage("§9 ⁕ Bloc de diamant: 5€");
                sender.sendMessage("§cTeams: §l2§r§c joueurs max.");
                return true;

            } else if (args[0].equalsIgnoreCase("commandes")) {

                sender.sendMessage(Utils.getCenteredText("§7§lCOMMANDES"));
                if (sender.isOp()) {
                    sender.sendMessage("§7Définir le cercle rouge\n §e/cashprice setarea");
                    sender.sendMessage("§7Lancer la partie\n §e/cashprice start");
                    sender.sendMessage("§7Voir les statistiques d'un joueur\n §e/stats <joueur/@all>\n §e/cashprice see <joueur>");
                } else
                    sender.sendMessage("§7Voir vos statistiques:\n §e/cashprice stat");
                sender.sendMessage("§7Voir sa cagnotte:\n §e/cagnotte");
                sender.sendMessage("§7Comprendre le CashPrice\n §e/help concept");
                sender.sendMessage("§7Infos/Règles du CashPrice\n §e/help infos");
                return true;
            }

        }

        sender.sendMessage("§c/help <concept/infos/commandes>");

        return false;
    }

}
