package fr.maximouz.cashprice;

import fr.maximouz.cashprice.scoreboard.PlayerScoreboard;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class Pool {

    private final Player player;
    private BigDecimal amount;

    public Pool(Player player, BigDecimal amount) {
        this.player = player;
        this.amount = amount;
    }

    public Pool(Player player) {
        this(player, new BigDecimal("0.0"));
    }

    public Player getPlayer() {
        return player;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        if (CashPrice.getInstance().getManager().hasStarted())
            CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoards().forEach(PlayerScoreboard::update);
    }

    public void addAmount(BigDecimal amount) {
        setAmount(this.amount.add(amount));
    }

    public void removeAmount(BigDecimal amount) {
        setAmount(this.amount.subtract(amount));
    }

    public void reset() {
        setAmount(new BigDecimal("0.00"));
    }

}
