package fr.maximouz.cashprice.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DeathMatchEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public DeathMatchEvent() {}

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
