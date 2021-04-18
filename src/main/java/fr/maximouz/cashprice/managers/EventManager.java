package fr.maximouz.cashprice.managers;

import fr.maximouz.cashprice.cashpriceevent.CashPriceEvent;
import fr.maximouz.cashprice.cashpriceevent.CashPriceEventType;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final List<CashPriceEvent> events;

    public EventManager() {
        this.events = new ArrayList<>();

        events.add(CashPriceEventType.CHEST.getNewInstance());

    }

    public List<CashPriceEvent> getEvents() {
        return events;
    }
}
