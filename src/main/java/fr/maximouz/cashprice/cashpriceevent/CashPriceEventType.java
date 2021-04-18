package fr.maximouz.cashprice.cashpriceevent;

import fr.maximouz.cashprice.cashpriceevent.events.ChestCashPriceEvent;

public enum CashPriceEventType {

    CHEST(ChestCashPriceEvent.class);

    private final Class<?> clazz;

    CashPriceEventType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public CashPriceEvent getNewInstance() {
        try {
            return (CashPriceEvent) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
