package fr.maximouz.cashprice.managers;

import fr.maximouz.cashprice.*;
import fr.maximouz.cashprice.Statistic;
import fr.maximouz.cashprice.cashpriceevent.CashPriceEvent;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboard;
import fr.maximouz.cashprice.scoreboard.PlayerScoreboardType;
import fr.maximouz.cashprice.scoreboard.scoreboards.PlayingPlayerScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Manager {

    private Location previousSpawnPoint;
    private Location spawnPoint;

    private long startedAt;
    private long endAt;

    private final Map<Player, Boolean> players;

    private final Cuboid deathMatchArea;
    private BukkitTask deathMatchTriggerTask;

    private BukkitTask updateTimersTask;

    private long pvpStartAt;
    private long vulnerabilityStartAt;

    public static boolean DROP_ITEM_TO_REWARD = false;

    public Manager(World deathMatchWorld) {
        startedAt = -1;
        endAt = -1;
        pvpStartAt = -1;
        vulnerabilityStartAt = -1;
        players = new HashMap<>();
        // zone DeathMatch
        Location l1 = new Location(deathMatchWorld, 24, 0, -24);
        Location l2 = new Location(deathMatchWorld, -24, 256, 24);
        deathMatchArea = new Cuboid(l1, l2);
    }

    public Location getPreviousSpawnPoint() {
        return previousSpawnPoint;
    }

    public void setPreviousSpawnPoint(Location previousSpawnPoint) {
        this.previousSpawnPoint = previousSpawnPoint;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public boolean hasStarted() {
        return getStartedAt() != -1 && getEndAt() != -1;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - startedAt;
    }

    public boolean hasPvp() {
        return pvpStartAt != -1 && System.currentTimeMillis() >= pvpStartAt;
    }

    public boolean hasVulnerability() {
        return vulnerabilityStartAt != -1 && System.currentTimeMillis() >= vulnerabilityStartAt;
    }

    public long getPvpTimeLeft() {
        return pvpStartAt - System.currentTimeMillis();
    }

    public long getEndAt() {
        return endAt;
    }

    public boolean hasDeathMatch() {
        return System.currentTimeMillis() >= endAt;
    }

    public long getTimeLeft() {
        return endAt - System.currentTimeMillis();
    }

    public Cuboid getDeathMatchArea() {
        return deathMatchArea;
    }

    public Map<Player, Boolean> getPlayers() {
        return players;
    }

    public void start() {

        Bukkit.broadcastMessage("§6§m                                                           ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§6§lCashPrice"));
        Bukkit.broadcastMessage(" §e⁕§7 Vous êtes invulnérable pendant 5 minutes");
        Bukkit.broadcastMessage(" §e⁕§7 Le PvP est actif dans 10 minutes");
        Bukkit.broadcastMessage(" §e⁕§7 Le DeathMatch final démarre dans 1h");
        Bukkit.broadcastMessage(Utils.getCenteredText("§e§lBonne Chance"));
        Bukkit.broadcastMessage("§6§m                                                           ");

        startedAt = System.currentTimeMillis();
        endAt = startedAt + TimeUnit.HOURS.toMillis(1);
        pvpStartAt = startedAt + TimeUnit.MINUTES.toMillis(10);
        vulnerabilityStartAt = startedAt + TimeUnit.MINUTES.toMillis(5);

        CashPrice.getInstance().getStatisticManager().getStatistics().clear();
        CashPrice.getInstance().getPoolManager().getPools().clear();
        this.players.clear();

        deathMatchTriggerTask = Bukkit.getScheduler().runTaskLater(CashPrice.getInstance(), Utils::deathMatch, 20 * getTimeLeft() / 1000);

        Bukkit.getOnlinePlayers().forEach(player -> {

            if (player.isOp()) {

                player.setGameMode(GameMode.CREATIVE);
                player.setDisplayName(ChatColor.RED + player.getDisplayName());
                player.setFlying(true);
                player.sendMessage("\n§aLa partie commence, n'oubliez pas de vous mettre invisible avec le §e/vanish");

                CashPrice.getInstance().getPlayerScoreboardManager().setPlayerScoreboardType(player, PlayerScoreboardType.PLAYING);

            } else {

                this.players.put(player, true);

                Pool pool = new Pool(player);
                Statistic statistic = new Statistic(player);
                CashPrice.getInstance().getPoolManager().getPools().add(pool);
                CashPrice.getInstance().getStatisticManager().getStatistics().add(statistic);

                PlayingPlayerScoreboard playerScoreboard = (PlayingPlayerScoreboard) CashPrice.getInstance().getPlayerScoreboardManager().setPlayerScoreboardType(player, PlayerScoreboardType.PLAYING);

                playerScoreboard.setPool(pool);
                playerScoreboard.setStatistic(statistic);

                player.setHealth(20);
                player.setFoodLevel(20);
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(Utils.getRandomLocation(spawnPoint, 10).add(0, 1.5, 0));

            }

            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f);

        });

        CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoards().forEach(PlayerScoreboard::update);
        CashPrice.getInstance().getEventManager().getEvents().forEach(CashPriceEvent::schedule);

        updateTimersTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CashPrice.getInstance(), () -> CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoards().forEach(playerScoreboard -> ((PlayingPlayerScoreboard) playerScoreboard).updateTimers()), 0L, 20L);

        CashPrice.getInstance().setupWorldBorder();

    }

    public void stop(Player winner) {

        if (updateTimersTask != null) {
            updateTimersTask.cancel();
            updateTimersTask = null;
        }

        CashPrice.getInstance().getEventManager().getEvents().forEach(event -> {

            if (event.getTask() != null) {
                event.getTask().cancel();
                event.setTask(null);
                HandlerList.unregisterAll(event);
            }

        });

        Pool pool = CashPrice.getInstance().getPoolManager().getPool(winner);

        Bukkit.broadcastMessage("§6§m                                                            ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§e§lCashPrice"));
        if (winner != null) {
            Bukkit.broadcastMessage(" §7⁕ §aGagnant: §f" + winner.getName());
            if (pool != null) {
                Bukkit.broadcastMessage(" §7⁕ §6Cagnotte: §e" + pool.getAmount().toString() + "€");
            }
            Bukkit.broadcastMessage(" §7⁕ §fVie: §c" + BigDecimal.valueOf(winner.getHealth()).setScale(2, RoundingMode.HALF_EVEN).toString() + "❤");
        } else {
            Bukkit.broadcastMessage(" §7⁕ §aGagnant: §cPersonne");
        }
        Bukkit.broadcastMessage(" §7⁕ §bArgent réuni: §e" + CashPrice.getInstance().getPoolManager().getAllAmount() + "€");
        Bukkit.broadcastMessage(Utils.getCenteredText("§e§lBien joué !"));
        Bukkit.broadcastMessage("§6§m                                                            ");

        Bukkit.getOnlinePlayers().forEach(player -> {

            if (!player.isOp())
                player.teleport(winner.getLocation().add(ThreadLocalRandom.current().nextDouble(-10, 10), ThreadLocalRandom.current().nextDouble(2, 12), ThreadLocalRandom.current().nextDouble(-10, 10)));

            CashPrice.getInstance().getPlayerScoreboardManager().setPlayerScoreboardType(player, PlayerScoreboardType.WAITING);

        });

        Utils.spawnFireWork(winner.getLocation());
        Utils.spawnFireWork(winner.getLocation());
        Utils.spawnFireWork(winner.getLocation());

        startedAt = -1;
        endAt = -1;

    }

    public void check() {

        World world = Bukkit.getWorld("world");
        WorldBorder worldBorder = world.getWorldBorder();

        List<Player> alivePlayers = getAlivePlayers();
        int alivePlayersCount = alivePlayers.size();

        if (alivePlayersCount == 1) {

            Player winner = alivePlayers.get(0);
            stop(winner);

        } else if (alivePlayersCount == 75) {

            worldBorder.setSize(1500, 60 * 5);
            broadcastBorder(75, 750);

        } else if (alivePlayersCount == 50) {

            worldBorder.setSize(1000, 60 * 5);
            broadcastBorder(50, 500);

        } else if (alivePlayersCount == 25) {

            worldBorder.setSize(500, 60 * 5);
            broadcastBorder(25, 250);

        } else if (alivePlayersCount == 10) {

            worldBorder.setSize(200, 60 * 5);
            broadcastBorder(10, 100);

        } else if (alivePlayersCount <= 0) {

            stop(null);

        }

    }

    public void dead(Player player) {
        players.put(player, false);
    }

    private void broadcastBorder(int survivors, int size) {
        Bukkit.broadcastMessage("§6§m                                                            ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§6§lCashPrice"));
        Bukkit.broadcastMessage(" §f⁕ La partie ne compte plus que §a" + survivors + " survivants");
        Bukkit.broadcastMessage(" §f⁕ Réduction de la bordure à §e" + size + " blocs");
        Bukkit.broadcastMessage(" §f⁕ Vous avez §b5 minutes§f pour vous rapprocher du centre");
        Bukkit.broadcastMessage("§6§m                                                            ");
    }

    public boolean isParticipating(Player player) {
        return players.containsKey(player);
    }

    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();
        this.players.forEach((player, alive) -> {
            if (alive)
                players.add(player);
        });
        return players;
    }

    public List<Player> getDeadPlayers() {
        List<Player> players = new ArrayList<>();
        this.players.forEach((player, alive) -> {
            if (!alive)
                players.add(player);
        });
        return players;
    }

    public boolean isAlive(Player player) {
        return players.get(player);
    }

    public void saveGame() {

        CashPrice.getInstance().getConfig().set("game.time_passed", getTimePassed());
        CashPrice.getInstance().getConfig().set("game.red_circle", Cuboid.locationToString(spawnPoint));

        for (Map.Entry<Player, Boolean> entry : players.entrySet()) {

            Player player = entry.getKey();
            boolean alive = entry.getValue();

            Pool pool = CashPrice.getInstance().getPoolManager().getPool(player);
            Statistic statistic = CashPrice.getInstance().getStatisticManager().getStatistic(player);

            String key = "game.players." + player.getUniqueId().toString();
            CashPrice.getInstance().getConfig().set(key + ".alive", alive);
            CashPrice.getInstance().getConfig().set(key + ".health", player.getHealth());
            CashPrice.getInstance().getConfig().set(key + ".food", player.getFoodLevel());
            CashPrice.getInstance().getConfig().set(key + ".location", Cuboid.locationToString(player.getLocation()));
            CashPrice.getInstance().getConfig().set(key + ".cagnotte", pool.getAmount().toString());

            CashPrice.getInstance().getConfig().set(key + ".statistiques.kills", statistic.getKills());

            CashPrice.getInstance().getConfig().set(key + ".statistiques.iron_crafted", statistic.getIronBlockCrafted());
            CashPrice.getInstance().getConfig().set(key + ".statistiques.iron_placed", statistic.getIronBlockPlaced());

            CashPrice.getInstance().getConfig().set(key + ".statistiques.gold_crafted", statistic.getGoldBlockCrafted());
            CashPrice.getInstance().getConfig().set(key + ".statistiques.gold_placed", statistic.getGoldBlockPlaced());

            CashPrice.getInstance().getConfig().set(key + ".statistiques.diamond_crafted", statistic.getDiamondBlockCrafted());
            CashPrice.getInstance().getConfig().set(key + ".statistiques.diamond_placed", statistic.getDiamondBlockPlaced());

            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null)
                    CashPrice.getInstance().getConfig().set(key + ".inventory." + i, item);
            }

            if (player.getInventory().getHelmet() != null)
                CashPrice.getInstance().getConfig().set(key + ".helmet", player.getInventory().getHelmet());

            if (player.getInventory().getChestplate() != null)
                CashPrice.getInstance().getConfig().set(key + ".chestplate", player.getInventory().getChestplate());

            if (player.getInventory().getLeggings() != null)
                CashPrice.getInstance().getConfig().set(key + ".leggings", player.getInventory().getLeggings());

            if (player.getInventory().getBoots() != null)
                CashPrice.getInstance().getConfig().set(key + ".boots", player.getInventory().getBoots());

        }

        CashPrice.getInstance().saveConfig();

    }

    public void loadGame() {

        CashPrice.getInstance().saveDefaultConfig();

        long timePassed = CashPrice.getInstance().getConfig().getLong("game.time_passed");
        startedAt = System.currentTimeMillis();
        endAt = startedAt + TimeUnit.HOURS.toMillis(1) - timePassed;
        pvpStartAt = startedAt + TimeUnit.MINUTES.toMillis(10)  - timePassed;
        vulnerabilityStartAt = startedAt + TimeUnit.MINUTES.toMillis(5) - timePassed;

        Location spawnPoint = Cuboid.locationFromString(CashPrice.getInstance().getConfig().getString("game.red_circle"));
        this.spawnPoint = spawnPoint;
        Utils.createArea(spawnPoint);

        Bukkit.broadcastMessage("§6§m                                                           ");
        Bukkit.broadcastMessage(Utils.getCenteredText("§6§lCashPrice"));
        Bukkit.broadcastMessage(" §e⁕§7 " + (hasVulnerability() ? "Vous êtes vulnérable" : "Vous êtes invulnérable pendant " + Utils.formatTime(vulnerabilityStartAt - System.currentTimeMillis())));
        Bukkit.broadcastMessage(" §e⁕§7 Le PvP est actif" + (!hasPvp() ? " dans " + Utils.formatTime(getPvpTimeLeft()) : ""));
        Bukkit.broadcastMessage(" §e⁕§7 Le DeathMatch final " + (getTimeLeft() <= 0 ? "est en cours" : "démarre dans " + Utils.formatTime(getTimeLeft())));
        Bukkit.broadcastMessage(Utils.getCenteredText("§e§lBonne Chance"));
        Bukkit.broadcastMessage("§6§m                                                           ");

        Bukkit.getOnlinePlayers().forEach(this::loadPlayer);
        CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoards().forEach(PlayerScoreboard::update);

        if (endAt <= 0)
            Utils.deathMatch();

        CashPrice.getInstance().getEventManager().getEvents().forEach(CashPriceEvent::schedule);

        updateTimersTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CashPrice.getInstance(), () -> CashPrice.getInstance().getPlayerScoreboardManager().getPlayerScoreBoards().forEach(playerScoreboard -> ((PlayingPlayerScoreboard) playerScoreboard).updateTimers()), 0L, 20L);

        CashPrice.getInstance().setupWorldBorder();

        CashPrice.getInstance().getConfig().set("game", null);
        CashPrice.getInstance().saveConfig();
    }

    public void loadPlayer(Player player) {

        String key = "game.players." + player.getUniqueId().toString();

        PlayingPlayerScoreboard playerScoreboard = (PlayingPlayerScoreboard) CashPrice.getInstance().getPlayerScoreboardManager().setPlayerScoreboardType(player, PlayerScoreboardType.PLAYING);

        if (CashPrice.getInstance().getConfig().contains(key)) {

            boolean alive = CashPrice.getInstance().getConfig().getBoolean(key + ".alive");
            Location location = Cuboid.locationFromString(CashPrice.getInstance().getConfig().getString(key + ".location"));

            if (alive) {

                double health = CashPrice.getInstance().getConfig().getDouble(key + ".health");
                int food = CashPrice.getInstance().getConfig().getInt(key + ".food");

                player.setHealth(health);
                player.setFoodLevel(food);
                player.setGameMode(GameMode.SURVIVAL);

                CashPrice.getInstance().getConfig().getConfigurationSection(key + ".inventory").getKeys(false).forEach(slot ->
                    player.getInventory().setItem(Integer.parseInt(slot), CashPrice.getInstance().getConfig().getItemStack(key + ".inventory." + slot))
                );

                if (CashPrice.getInstance().getConfig().contains(key + ".helmet"))
                    player.getInventory().setHelmet(CashPrice.getInstance().getConfig().getItemStack(key + ".helmet"));

                if (CashPrice.getInstance().getConfig().contains(key + ".chestplate"))
                    player.getInventory().setChestplate(CashPrice.getInstance().getConfig().getItemStack(key + ".chestplate"));

                if (CashPrice.getInstance().getConfig().contains(key + ".leggings"))
                    player.getInventory().setLeggings(CashPrice.getInstance().getConfig().getItemStack(key + ".leggings"));

                if (CashPrice.getInstance().getConfig().contains(key + ".boots"))
                    player.getInventory().setBoots(CashPrice.getInstance().getConfig().getItemStack(key + ".boots"));


            } else {

                player.setGameMode(GameMode.SPECTATOR);

            }

            player.teleport(location);
            if (player.isOp())
                player.setDisplayName(ChatColor.RED + player.getDisplayName());

            Pool pool = new Pool(player);
            Statistic statistic = new Statistic(player);

            BigDecimal amount = new BigDecimal(CashPrice.getInstance().getConfig().getString(key + ".cagnotte"));
            pool.setAmount(amount);
            CashPrice.getInstance().getPoolManager().getPools().add(pool);

            int kills = CashPrice.getInstance().getConfig().getInt(key + ".statistiques.kills");
            int ironCrafted = CashPrice.getInstance().getConfig().getInt(key + ".statistiques.iron_crafted");
            int ironPlaced = CashPrice.getInstance().getConfig().getInt(key + ".statistiques.iron_placed");
            int goldCrafted = CashPrice.getInstance().getConfig().getInt(key  + ".statistiques.gold_crafted");
            int goldPlaced = CashPrice.getInstance().getConfig().getInt(key  + ".statistiques.gold_placed");
            int diamondCrafted = CashPrice.getInstance().getConfig().getInt(key  + ".statistiques.diamond_crafted");
            int diamondPlaced = CashPrice.getInstance().getConfig().getInt(key  + ".statistiques.diamond_placed");

            statistic.setKills(kills);
            statistic.setIronBlockCrafted(ironCrafted);
            statistic.setIronBlockPlaced(ironPlaced);
            statistic.setGoldBlockCrafted(goldCrafted);
            statistic.setGoldBlockPlaced(goldPlaced);
            statistic.setDiamondBlockCrafted(diamondCrafted);
            statistic.setDiamondBlockPlaced(diamondPlaced);

            CashPrice.getInstance().getStatisticManager().getStatistics().add(statistic);

            playerScoreboard.setPool(pool);
            playerScoreboard.setStatistic(statistic);

            players.put(player, alive);

        } else {

            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(spawnPoint.clone().add(0, 2, 0));

        }

    }

}
