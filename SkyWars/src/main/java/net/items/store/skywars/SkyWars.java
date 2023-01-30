package net.items.store.skywars;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.ICountdownManager;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.location.LocationState;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.api.message.IMessageManager;
import net.items.store.minigames.api.team.GameTeam;
import net.items.store.minigames.api.team.ITeamManager;
import net.items.store.minigames.core.actionbar.ActionBarManager;
import net.items.store.minigames.core.coin.CoinManager;
import net.items.store.minigames.core.countdown.CountdownManager;
import net.items.store.minigames.core.game.Game;
import net.items.store.minigames.core.map.location.LocationManager;
import net.items.store.minigames.core.map.MapManager;
import net.items.store.minigames.core.message.MessageManager;
import net.items.store.minigames.core.mysql.MySQL;
import net.items.store.minigames.core.mysql.MySQLConnector;
import net.items.store.minigames.core.team.TeamManager;
import net.items.store.minigames.core.voting.MapVotingManager;
import net.items.store.skywars.listener.countdown.SkyWarsCountdownCountListener;
import net.items.store.skywars.listener.countdown.SkyWarsGameStateChangedListener;
import net.items.store.skywars.listener.countdown.SkyWarsWaitingCountListener;
import net.items.store.skywars.listener.entity.EntityDamageListener;
import net.items.store.skywars.listener.inventory.InventoryClickListener;
import net.items.store.skywars.listener.inventory.InventoryCloseListener;
import net.items.store.skywars.listener.inventory.InventoryOpenListener;
import net.items.store.skywars.listener.player.*;
import net.items.store.skywars.player.PlayerDeathManager;
import net.items.store.skywars.scoreboard.SkyWarsScoreboardManager;
import net.items.store.skywars.stats.StatsManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SkyWars extends JavaPlugin {

    @Override
    public void onLoad() {
        MiniGame.setJavaPlugin(this);
        MiniGame.register(new LocationManager());
        MiniGame.register(new TeamManager());
        MiniGame.register(new MapManager());

        registerMapLocations();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerListener();

        connectMySQL();
        registerManager();

        MiniGame.get(LocationManager.class).loadLocations();
        MiniGame.get(MapManager.class).getMapJsonManager().loadMapLocations();
        MiniGame.get(MapManager.class).loadMapVoting();

        loadMessages();
        updateMinAndMaxPlayers();
    }

    private void registerMapLocations(){
        IMapManager mapManager = MiniGame.get(MapManager.class);
        mapManager.addLocationState("Lobby", LocationState.LOBBY);
        mapManager.addLocationState("Spectator", LocationState.NORMAL);
        mapManager.addLocationState("Spawn", LocationState.TEAM);
        mapManager.addLocationState("Chest", LocationState.COUNT);
        mapManager.addLocationState("MidChest", LocationState.COUNT);
        mapManager.loadMaps();
        mapManager.deleteOldMaps();
    }

    private void updateMinAndMaxPlayers(){
        ITeamManager teamManager = MiniGame.get(TeamManager.class);
        List<GameTeam> gameTeamList = teamManager.getTeams();
        int minPlayers = 1;
        int maxPlayers = 0;

        if(gameTeamList.size() > 0){
            minPlayers = gameTeamList.get(0).getMaxPlayers() + 1;
        }
        for(GameTeam gameTeam : gameTeamList){
            maxPlayers += gameTeam.getMaxPlayers();
        }

        AbstractGame abstractGame = MiniGame.get(AbstractGame.class);
        abstractGame.setMinPlayers(minPlayers);
        abstractGame.setMaxPlayers(maxPlayers);
    }

    private void registerCountdowns(){
        getServer().getPluginManager().registerEvents(new LobbyCountdownCountListener(GameState.WAITING), this);
        getServer().getPluginManager().registerEvents(new WaitCountdownCountListener(GameState.INGAME), this);
        getServer().getPluginManager().registerEvents(new IngameCountdownCountListener(), this);
        getServer().getPluginManager().registerEvents(new RestartCountdownCountListener(), this);
        getServer().getPluginManager().registerEvents(new CountdownGameStateChangedListener(), this);
    }

    private void registerManager(){
        MiniGame.register(new MessageManager());
        MiniGame.register(new MapVotingManager());
        MiniGame.register(new CountdownManager());
        MiniGame.register(new CoinManager());
        MiniGame.register(new StatsManager());
        MiniGame.register(new SkyWarsScoreboardManager());
        MiniGame.register(new ActionBarManager());
        MiniGame.register(new PlayerDeathManager());
        MiniGame.register(new Game());
        MiniGame.registerDefaults();

        registerCountdowns();
        MiniGame.get(ICountdownManager.class).registerDefaultCountdowns();
        MiniGame.get(ICountdownManager.class).scheduleCountdown();
    }

    private void registerListener(){
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new SkyWarsGameStateChangedListener(), this);
        getServer().getPluginManager().registerEvents(new SkyWarsCountdownCountListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new SkyWarsWaitingCountListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerSwapHandItemsListener(), this);
    }

    private void connectMySQL(){
        String host = getConfig().getString("host");
        String database = getConfig().getString("database");
        String user = getConfig().getString("user");
        String password = getConfig().getString("password");

        MySQLConnector mySQLConnector = new MySQLConnector(host, database, user, password);
        MySQL mySQL = new MySQL(mySQLConnector);
        mySQL.connectMySQL();

        MiniGame.register(mySQL);
    }

    private void loadMessages() {
        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        messageManager.addMessage("Prefix", "§aSkyWars §8| §7");
        messageManager.addMessage("PlayerKilled", "{PREFIX}§e{ENTITY} §7wurde von §e{KILLER} §7getötet§8.");
        messageManager.addMessage("PlayerDied", "{PREFIX}§e{ENTITY} §cist gestorben§8.");
        messageManager.addMessage("WinTitle", "§7Das Team {TEAM_PREFIX}{TEAM_NAME}");
        messageManager.addMessage("WinSubtitle", "§7hat das Spiel §agewonnen§8.");
    }
}
