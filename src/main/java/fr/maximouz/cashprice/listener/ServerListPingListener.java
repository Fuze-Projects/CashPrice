package fr.maximouz.cashprice.listener;

import fr.maximouz.cashprice.CashPrice;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

    @EventHandler
    public void onListPing(ServerListPingEvent event) {

        String state = CashPrice.getInstance().getManager().hasStarted() ? ChatColor.RED + "La partie est en cours" : ChatColor.YELLOW + "En attente de joueurs";
        event.setMotd("» " + ChatColor.GOLD + "" + ChatColor.BOLD + "CashPrice \n" + state);

    }

}
