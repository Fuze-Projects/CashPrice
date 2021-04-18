package fr.maximouz.cashprice.commands;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Pool;
import fr.maximouz.cashprice.Statistic;
import fr.maximouz.cashprice.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!CashPrice.getInstance().getManager().hasStarted()) {
            sender.sendMessage("§cLa partie n'a pas commencé..");
            return false;
        }

        if (args.length == 0 && sender instanceof Player) {

            Player player = (Player) sender;
            Statistic statistic = CashPrice.getInstance().getStatisticManager().getStatistic(player);
            Pool pool = CashPrice.getInstance().getPoolManager().getPool(player);

            if (statistic != null && pool != null) {

                sender.sendMessage("§6§m                                                            ");
                sender.sendMessage(Utils.getCenteredText("§6§lStatistiques"));
                sender.sendMessage(" §c⁕ Kills: §b" + statistic.getKills());
                sender.sendMessage(" §f⁕ Blocs de fer craftés: §b" + statistic.getIronBlockCrafted());
                sender.sendMessage(" §f⁕ Blocs de fer posés: §b" + statistic.getIronBlockPlaced());
                sender.sendMessage(" §e⁕ Blocs d'or craftés: §b" + statistic.getGoldBlockCrafted());
                sender.sendMessage(" §e⁕ Blocs d'or posés: §b" + statistic.getGoldBlockPlaced());
                sender.sendMessage(" §9⁕ Blocs de diamant craftés: §b" + statistic.getDiamondBlockCrafted());
                sender.sendMessage(" §9⁕ Blocs de diamant posés: §b" + statistic.getDiamondBlockPlaced());
                sender.sendMessage(" §6⁕ Cagnotte: §e" + pool.getAmount().toString() + "€");
                sender.sendMessage("§6§m                                                            ");

            } else {

                sender.sendMessage("§cVous ne participez pas à l'event");

            }

        } else if (args.length == 1) {

            if (!sender.isOp()) {
                sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande");
                return false;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {

                Statistic statistic = CashPrice.getInstance().getStatisticManager().getStatistic(target);
                Pool pool = CashPrice.getInstance().getPoolManager().getPool(target);

                if (statistic != null && pool != null) {

                    sender.sendMessage("§6§m                                                            ");
                    sender.sendMessage(Utils.getCenteredText("§f§l" + target.getName()));
                    sender.sendMessage(" §c⁕ Kills: §b" + statistic.getKills());
                    sender.sendMessage(" §f⁕ Blocs de fer craftés: §b" + statistic.getIronBlockCrafted());
                    sender.sendMessage(" §f⁕ Blocs de fer posés: §b" + statistic.getIronBlockPlaced());
                    sender.sendMessage(" §e⁕ Blocs d'or craftés: §b" + statistic.getGoldBlockCrafted());
                    sender.sendMessage(" §e⁕ Blocs d'or posés: §b" + statistic.getGoldBlockPlaced());
                    sender.sendMessage(" §9⁕ Blocs de diamant craftés: §b" + statistic.getDiamondBlockCrafted());
                    sender.sendMessage(" §9⁕ Blocs de diamant posés: §b" + statistic.getDiamondBlockPlaced());
                    sender.sendMessage(" §6⁕ Cagnotte: §e" + pool.getAmount().toString() + "€");
                    sender.sendMessage("§6§m                                                            ");

                } else {

                    sender.sendMessage("§cCe joueur ne participe pas à l'event");

                }

            } else if (args[0].equalsIgnoreCase("@all")) {

                sender.sendMessage("§6§m                                                            ");
                sender.sendMessage(Utils.getCenteredText("§6§lStatistiques"));
                sender.sendMessage(" §c⁕ Argent réuni: §e" + CashPrice.getInstance().getPoolManager().getAllAmount() + "€");
                sender.sendMessage(" §c⁕ Kills: §b" + CashPrice.getInstance().getStatisticManager().getAllKills());
                sender.sendMessage(" §f⁕ Blocs de fer craftés: §b" + CashPrice.getInstance().getStatisticManager().getAllIronBlockCrafted());
                sender.sendMessage(" §f⁕ Blocs de fer posés: §b" + CashPrice.getInstance().getStatisticManager().getAllIronBlockPlaced());
                sender.sendMessage(" §e⁕ Blocs d'or craftés: §b" + CashPrice.getInstance().getStatisticManager().getAllGoldBlockCrafted());
                sender.sendMessage(" §e⁕ Blocs d'or posés: §b" + CashPrice.getInstance().getStatisticManager().getAllGoldBlockPlaced());
                sender.sendMessage(" §9⁕ Blocs de diamant craftés: §b" + CashPrice.getInstance().getStatisticManager().getAllDiamondBlockCrafted());
                sender.sendMessage(" §9⁕ Blocs de diamant posés: §b" + CashPrice.getInstance().getStatisticManager().getAllDiamondBlockPlaced());
                Pool best = CashPrice.getInstance().getPoolManager().getBest();
                if (best != null)
                    sender.sendMessage(" §d⁕ Meilleure cagnotte: §e" + best.getAmount().toString() + "€ §7(" + best.getPlayer().getName() + ")");
                sender.sendMessage("§6§m                                                            ");

            } else {

                sender.sendMessage("§cLe joueur §f" + args[0] + "§c n'est pas connecté..");

            }

        }

        return false;
    }

}
