package fr.maximouz.cashprice;

import fr.maximouz.cashprice.commands.*;
import fr.maximouz.cashprice.listener.*;
import fr.maximouz.cashprice.managers.*;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

public class CashPrice extends JavaPlugin {

    private static CashPrice INSTANCE;

    private Manager manager;
    private StatisticManager statisticManager;
    private PoolManager poolManager;
    private PlayerScoreboardManager playerScoreboardManager;
    private EventManager eventManager;
    private DuelManager duelManager;

    public static int minPlayers = 100;

    @Override
    public void onEnable() {

        INSTANCE = this;

        World deathMatchWorld = getServer().getWorld("deathmatch");
        if (deathMatchWorld == null) {

            getLogger().info("Monde 'deathmatch' introuvable, création en cours..");
            WorldCreator worldCreator = new WorldCreator("deathmatch");
            worldCreator.type(WorldType.FLAT);
            worldCreator.createWorld();
            getLogger().info("Monde 'deathmatch' créé.");

            deathMatchWorld = getServer().getWorld("deathmatch");

        }

        deathMatchWorld.getWorldBorder().setCenter(0, 0);
        deathMatchWorld.getWorldBorder().setSize(50);
        deathMatchWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        manager = new Manager(deathMatchWorld);
        statisticManager = new StatisticManager();
        poolManager = new PoolManager();
        playerScoreboardManager = new PlayerScoreboardManager();
        eventManager = new EventManager();
        duelManager = new DuelManager();

        registerListeners();
        registerCommands();

    }

    @Override
    public void onDisable() {

        if (manager.getSpawnPoint() != null)
            Utils.removeArea(manager.getSpawnPoint());

    }

    public static CashPrice getInstance() {
        return INSTANCE;
    }

    public void setupWorldBorder() {

        World world = getServer().getWorld("world");
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(manager.getSpawnPoint());

        int alivePlayersCount = CashPrice.getInstance().getManager().getAlivePlayers().size();
        worldBorder.setSize(alivePlayersCount > 75
                ? 2000 : alivePlayersCount > 50
                ? 1500 : alivePlayersCount > 25
                ? 1000 : alivePlayersCount > 10
                ? 500 : 200
        , 0);

    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new CraftItemListener(), this);
        getServer().getPluginManager().registerEvents(new ServerListPingListener(), this);
    }

    private void registerCommands() {
        getCommand("cashprice").setExecutor(new CashPriceCommand());
        getCommand("see").setExecutor(new SeeCommand());
        getCommand("pool").setExecutor(new PoolCommand());
        getCommand("statistics").setExecutor(new StatCommand());
        getCommand("fakeop").setExecutor(new FakeOpCommand());
        getCommand("help").setExecutor(new HelpCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("duel").setExecutor(new DuelCommand());
    }

    public Manager getManager() {
        return manager;
    }

    public StatisticManager getStatisticManager() {
        return statisticManager;
    }

    public PoolManager getPoolManager() {
        return poolManager;
    }

    public PlayerScoreboardManager getPlayerScoreboardManager() {
        return playerScoreboardManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }
}
