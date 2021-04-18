package fr.maximouz.cashprice.managers;

import fr.maximouz.cashprice.Pool;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PoolManager {

    private final List<Pool> pools;

    public PoolManager() {
        pools = new ArrayList<>();
    }

    public List<Pool> getPools() {
        return pools;
    }

    public Pool getPool(Player player) {
        return pools.stream().filter(pool -> pool.getPlayer() == player)
                .findFirst()
                .orElse(null);
    }

    public void resetAll() {
        pools.forEach(Pool::reset);
    }

    public BigDecimal getAllAmount() {
        AtomicReference<BigDecimal> amount = new AtomicReference<>(new BigDecimal("0.00").setScale(2, BigDecimal.ROUND_HALF_EVEN));
        pools.forEach(pool -> amount.set(amount.get().add(pool.getAmount())));
        return amount.get();
    }

    public Pool getBest() {
        Pool best = null;
        for (Pool pool : pools) {
            if (best == null || pool.getAmount().compareTo(best.getAmount()) > 0)
                best = pool;
        }
        return best;
    }

    public void setPool(Player player, Pool pool) {
        Pool previousPool = getPool(player);
        if (previousPool != null)
            pools.remove(previousPool);
        pools.add(pool);
    }

}
