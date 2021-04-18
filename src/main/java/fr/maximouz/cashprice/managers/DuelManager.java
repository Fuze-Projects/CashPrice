package fr.maximouz.cashprice.managers;

import fr.maximouz.cashprice.CashPrice;
import fr.maximouz.cashprice.Duel;
import fr.maximouz.cashprice.events.DeathMatchEvent;
import fr.maximouz.cashprice.events.PlayerDeathTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.math.BigDecimal;

public class DuelManager implements Listener {

    private Duel duel;

    private Location firstSpawnPoint;
    private Location secondSpawnPoint;

    public DuelManager() {
        Bukkit.getPluginManager().registerEvents(this, CashPrice.getInstance());
    }

    public Duel getDuel() {
        return duel;
    }

    public void setDuel(Duel duel) {
        this.duel = duel;
    }

    public boolean hasDuel() {
        return duel != null;
    }

    public Location getFirstSpawnPoint() {
        return firstSpawnPoint;
    }

    public void setFirstSpawnPoint(Location firstSpawnPoint) {
        this.firstSpawnPoint = firstSpawnPoint;
    }

    public Location getSecondSpawnPoint() {
        return secondSpawnPoint;
    }

    public void setSecondSpawnPoint(Location secondSpawnPoint) {
        this.secondSpawnPoint = secondSpawnPoint;
    }

    public void duel(Player p1, Player p2, BigDecimal reward) {
        if (duel == null && firstSpawnPoint != null && secondSpawnPoint != null) {
            setDuel(new Duel(p1, firstSpawnPoint, p2, secondSpawnPoint, reward));
            duel.start();
        }
        Bukkit.broadcastMessage("§9Un duel entre §f" + p1.getName() + "§9 et §f" + p2.getName() + "§9 est en cours");
        Bukkit.broadcastMessage("§9Récompense: §e" + reward.toString() + "€");
    }

    @EventHandler
    public void onTriggerDeath(PlayerDeathTriggerEvent event) {

        if (duel != null)
            event.setCancelled(true);

    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {

        if (duel == null || event.getEntity().getType() != EntityType.PLAYER)
            return;

        Player victim = (Player) event.getEntity();

        if (victim.getHealth() - event.getFinalDamage() <= 0.0D) {

            Player winner = duel.getOpponent(victim);

            if (winner != null) {

                event.setCancelled(true);
                duel.stop(winner);
                setDuel(null);

            }

        }

    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {

        if (duel == null)
            return;

        Player winner = duel.getOpponent(event.getPlayer());

        if (winner != null) {
            duel.stop(winner);
            setDuel(null);
        }

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {

        if (duel == null || event.getDamager().getType() != EntityType.PLAYER && event.getEntity().getType() != EntityType.PLAYER)
            return;

        if (event.getDamager().isOp() || duel.getOpponent((Player) event.getEntity()) == event.getDamager())
            event.setCancelled(false);

    }

    @EventHandler
    public void onDeathMatch(DeathMatchEvent event) {

        if (duel == null)
            return;

        duel.stop(null);
        setDuel(null);

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        if (duel != null && duel.getOpponent(event.getPlayer()) != null)
            event.setCancelled(true);

    }

}
