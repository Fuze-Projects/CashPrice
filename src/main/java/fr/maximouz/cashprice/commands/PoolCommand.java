package fr.maximouz.cashprice.commands;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Pool;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PoolCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!CashPrice.getInstance().getManager().hasStarted()) {
            sender.sendMessage("§cLa partie n'a pas commencé..");
            return false;
        }

        Player player = sender instanceof Player ? (Player) sender : null;

        if (args.length == 0 && player != null) {
            Pool pool = CashPrice.getInstance().getPoolManager().getPool(player);
            player.sendMessage(pool != null ? "§6Cagnotte: §e" + pool.getAmount() + "€" : "§cVous ne participez pas à l'event..");
            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("help")) {

                sender.sendMessage("§6/" + label + " §7voir sa cagnotte");
                if (sender.isOp()) {
                    sender.sendMessage("§6/" + label + " <joueur> §7voir la cagnotte de la cible");
                    sender.sendMessage("§6/" + label + " add <joueur> <montant> §7augmenter une cagnotte");
                    sender.sendMessage("§6/" + label + " remove <joueur> <montant> §7réduire une cagnotte");
                }
                sender.sendMessage("§6/" + label + " help");

            } else {

                if (!sender.isOp()) {
                    sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande");
                    return false;
                }

                Player target = Bukkit.getPlayer(args[0]);

                if (target != null) {

                    Pool targetPool = CashPrice.getInstance().getPoolManager().getPool(target);
                    sender.sendMessage(targetPool != null ? "§6Cagnotte de §f" + target.getName() + "§6: §e" + targetPool.getAmount().toString() + "€" : "§cCe joueur ne participe pas à l'event..");

                } else {

                    sender.sendMessage("§cLe joueur §f" + args[0] + "§c n'est pas conecté..");

                }

            }

            return true;
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {

            if (!sender.isOp()) {
                sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande");
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target != null) {

                try {

                    BigDecimal amount = new BigDecimal(String.valueOf(Double.parseDouble(args[2])))
                            .setScale(2, RoundingMode.HALF_EVEN);
                    Pool targetPool = CashPrice.getInstance().getPoolManager().getPool(target);

                    if (targetPool == null) {
                        sender.sendMessage("§cCe joueur ne participe pas à l'event..");
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("add")) {

                        targetPool.addAmount(amount);
                        sender.sendMessage("§aVous avez ajouté §e" + amount.toString() + "€§a à la cagnotte de §f" + target.getName() + "§e.");
                        target.sendMessage("§aVotre cagnotte a gagné §e" + amount.toString() + "€");

                    } else {

                        targetPool.removeAmount(amount);
                        sender.sendMessage("§cVous avez retiré §e" + amount.toString() + "€§c à la cagnotte de §f" + target.getName() + "§e.");
                        target.sendMessage("§cVotre cagnotte a perdu §e" + amount.toString() + "€");

                    }

                } catch (NumberFormatException ex) {

                    sender.sendMessage("§cMontant invalide \"" + args[2] + "\"");

                }

            } else {

                sender.sendMessage("§cLe joueur §f" + args[1] + "§c n'est pas conecté..");

            }

        }

        return false;
    }


}
