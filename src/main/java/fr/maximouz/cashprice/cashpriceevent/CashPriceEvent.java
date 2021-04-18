package fr.maximouz.cashprice.cashpriceevent;

import fr.maximouz.cashprice.CashPrice;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class CashPriceEvent implements ICashPriceEvent, Listener {

    private final CashPriceEventType type;
    private final long interval;
    private final int max;

    private BukkitTask task;

    public CashPriceEvent(CashPriceEventType type, long interval, int max) {
        this.type = type;
        this.interval = interval;
        this.max = max;
    }

    public void schedule() {

        Bukkit.getPluginManager().registerEvents(this, CashPrice.getInstance());

        task = Bukkit.getScheduler().runTaskTimer(CashPrice.getInstance(), new Runnable() {

            private int count = 0;

            @Override
            public void run() {

                if (count == Math.max(1, max)) {
                    Bukkit.getScheduler().cancelTask(task.getTaskId());
                    return;
                }

                trigger();

                count++;
            }
        }, 20 * interval, 20 * interval);

    }

    public CashPriceEventType getType() {
        return type;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public void trigger() {}
}
