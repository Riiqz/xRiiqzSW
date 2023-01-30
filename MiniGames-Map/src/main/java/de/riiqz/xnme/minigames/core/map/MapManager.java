package de.riiqz.xnme.minigames.core.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.riiqz.xnme.minigames.core.map.command.LocationCommand;
import de.riiqz.xnme.minigames.core.map.command.MapCommand;
import de.riiqz.xnme.minigames.core.map.command.MapsCommand;
import de.riiqz.xnme.minigames.core.map.listener.InventoryClickListener;
import de.riiqz.xnme.minigames.core.map.listener.PlayerJoinListener;
import de.riiqz.xnme.minigames.core.map.location.LocationManager;
import lombok.Getter;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.item.ItemBuilder;
import net.items.store.minigames.api.location.ILocationManager;
import net.items.store.minigames.api.location.LocationState;
import net.items.store.minigames.api.message.IMessageManager;
import net.items.store.minigames.api.team.GameTeam;
import net.items.store.minigames.api.team.ITeamManager;
import net.items.store.minigames.api.voting.IVotingManager;
import net.items.store.minigames.api.voting.VotingDetail;
import net.items.store.minigames.api.voting.VotingHeader;
import net.items.store.minigames.core.data.FileBuilder;
import net.items.store.minigames.core.data.MapBuilder;
import net.items.store.minigames.core.message.MessageManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.FileUtil;
import org.json.simple.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class MapManager implements IMapManager {

    private List<GameMap> gameMapList;
    private GameMap currentGameMap;
    private GameMap currentForceMap;
    @Getter
    private IMapJsonManager mapJsonManager;
    private Map<String, LocationState> locationStateMap;
    private ItemStack itemStackMHFRight;
    private ItemStack itemStackMHFLeft;
    private VotingHeader votingHeader;
    private VotingDetail votingDetail;
    private boolean mapLoaded;

    @Getter
    private MapHandler mapHandler;

    public MapManager(){
        this.mapJsonManager = new MapJsonManager();
        this.locationStateMap = Maps.newHashMap();
        this.gameMapList = Lists.newArrayList();
        this.itemStackMHFRight = ItemBuilder.modify().setMaterial(Material.PLAYER_HEAD).setDisplayName("§eNächste Seite")
                .setSkullValue("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").buildItem();
        this.itemStackMHFLeft = ItemBuilder.modify().setMaterial(Material.PLAYER_HEAD).setDisplayName("§eVorherige Seite")
                .setSkullValue("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").buildItem();
        this.currentGameMap = null;
        this.currentForceMap = null;
        this.mapHandler = new MapHandler();
        this.mapLoaded = false;
    }

    @Override
    public void updateGameMaps() {
        this.mapJsonManager.updateGameMapsToJson(this.gameMapList);
    }

    @Override
    public void addMap(Player player, GameMap gameMap) {
        Optional<GameMap> optionalGameMap = gameMapList.stream()
                .filter(x -> x.getMapName().equalsIgnoreCase(gameMap.getMapName())).findFirst();

        Map<Object, Object> objectObjectMap = Maps.newHashMap();
        objectObjectMap.put("{MAP}", gameMap.getMapName());

        IMessageManager messageManager = MiniGame.get(MessageManager.class);

        if(!optionalGameMap.isPresent()){
            this.gameMapList.add(gameMap);
            this.mapJsonManager.addGameMapToJson(gameMap);

            player.sendMessage(messageManager.getMessage("AddedMap", objectObjectMap));
        } else {
            player.sendMessage(messageManager.getMessage("MapAlreadyExists", objectObjectMap));
        }
    }

    @Override
    public void addLocationState(String identifier, LocationState locationState) {
        this.locationStateMap.put(identifier, locationState);
    }

    @Override
    public void loadMaps() {
        this.gameMapList = this.mapJsonManager.loadMaps();
    }

    @Override
    public void loadMapVoting() {
        IVotingManager votingManager = MiniGame.get(IVotingManager.class);
        if(votingManager != null){
            JSONObject jsonObject = FileBuilder.loadJSONObject("mapVoting.json", true);
            JSONObject inventoryJsonObject = (JSONObject) jsonObject.get("mapVotingInventory");
            JSONObject itemJsonObject = (JSONObject) jsonObject.get("mapVotingItem");

            String votingIdentifier = "Map";
            String inventoryName = inventoryJsonObject.get("inventoryName").toString();
            int inventorySize = Integer.valueOf(inventoryJsonObject.get("inventorySize").toString());
            ItemStack playerItem = ItemBuilder.modify()
                    .setMaterial(Material.valueOf(itemJsonObject.get("material").toString()))
                    .setDisplayName(itemJsonObject.get("displayName").toString()).buildItem();
            int playerItemSlot = 4;

            votingHeader = new VotingHeader(votingIdentifier, inventoryName, inventorySize, playerItem, playerItemSlot);
            votingDetail = new VotingDetail(votingHeader, "Map", "", null, 0);
            votingHeader.getVotingDetailList().add(votingDetail);

            votingManager.addVoting(votingHeader);
        }

        if(this.votingHeader != null && this.votingDetail != null){
            List<GameMap> currentGameMapList = this.gameMapList.stream().collect(Collectors.toList());
            Random random = new Random();
            IMessageManager messageManager = MiniGame.get(MessageManager.class);
            Map<Object, Object> objectObjectMap = Maps.newHashMap();

            for(int i = 0; i < 3; i++){
                if(currentGameMapList.size() > 0){
                    GameMap gameMap = currentGameMapList.remove(random.nextInt(currentGameMapList.size()));
                    objectObjectMap.put("{MAP}", gameMap.getMapName());

                    String identifier = gameMap.getMapName();
                    ItemBuilder itemBuilder = ItemBuilder.modify()
                            .setMaterial(gameMap.getMapMaterial())
                            .setDisplayName(messageManager.getMessage("MapVotingDisplayName", objectObjectMap));
                    int itemSlot = 0;

                    this.votingDetail.addToVotingTrailer(identifier, itemBuilder, itemSlot);
                }
            }
        }
    }

    @Override
    public List<GameMap> getMaps() {
        return this.gameMapList;
    }

    @Override
    public void takeRandomMap() {
        if(this.gameMapList.size() <= 0){
            System.out.println("ERROR!: Cannot take Random Map because no Maps exist.");
            return;
        }
        Random random = new Random();
        int next = random.nextInt(this.gameMapList.size());

        this.currentGameMap = this.gameMapList.get(next);
    }

    @Override
    public GameMap getCurrentMap() {
        return currentForceMap != null ? currentForceMap : currentGameMap;
    }

    @Override
    public GameMap getMapByName(String mapName) {
        GameMap gameMap = null;

        for(GameMap currentGameMap : this.gameMapList){
            if(currentGameMap.getMapName().equalsIgnoreCase(mapName)){
                gameMap = currentGameMap;
                break;
            }
        }
        return gameMap;
    }

    @Override
    public void openMapsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§eMaps");

        for(int i = 0; i < 9; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        for(int i = 45; i < 54; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        inventory.setItem(4, ItemBuilder.modify().setMaterial(Material.GRASS_BLOCK).setDisplayName("§eMaps").buildItem());
        inventory.setItem(49, ItemBuilder.modify().setMaterial(Material.PAPER).setDisplayName("§7Seite§8: §e1").buildItem());

        for(GameMap gameMap : gameMapList){
            inventory.addItem(ItemBuilder.modify().setMaterial(gameMap.getMapMaterial())
                    .setDisplayName("§a" + gameMap.getMapName()).addMapData(gameMap.getMapName()).buildItem());
        }

        player.openInventory(inventory);
    }

    @Override
    public void openMapBlockInventory(Player player, String mapName) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§eMap §8- §6Blöcke");
        for(int i = 0; i < 9; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        for(int i = 45; i < 54; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        inventory.setItem(4, ItemBuilder.modify().setMaterial(Material.GRASS_BLOCK)
                .setDisplayName("§6" + mapName).buildItem());
        inventory.setItem(49, ItemBuilder.modify().setMaterial(Material.PAPER).setDisplayName("§7Seite§8: §e1").buildItem());
        inventory.setItem(53, this.itemStackMHFRight);

        for(ItemStack itemStack : MapBlocks.getBlockMap().get(1)){
            inventory.addItem(itemStack);
        }

        player.openInventory(inventory);
    }

    @Override
    public void openMapBlockInventory(Player player, String mapName, int currentSite, MapBlockDirection mapBlockDirection) {
        int nextSite = (mapBlockDirection == MapBlockDirection.RIGHT ? currentSite + 1 : currentSite - 1);

        Inventory inventory = Bukkit.createInventory(null, 54, "§eMap §8- §6Blöcke");
        for(int i = 0; i < 9; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        for(int i = 45; i < 54; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        inventory.setItem(4, ItemBuilder.modify().setMaterial(Material.GRASS_BLOCK)
                .setDisplayName("§6" + mapName).buildItem());
        inventory.setItem(49, ItemBuilder.modify().setMaterial(Material.PAPER).setDisplayName("§7Seite§8: §e" + nextSite).buildItem());

        if(nextSite > 1){
            inventory.setItem(45, this.itemStackMHFLeft);
        }
        if(nextSite < MapBlocks.getLastSite()) {
            inventory.setItem(53, this.itemStackMHFRight);
        }
        for(ItemStack itemStack : MapBlocks.getBlockMap().get(nextSite)){
            inventory.addItem(itemStack);
        }

        player.openInventory(inventory);
    }

    @Override
    public void openMapLocationInventory(Player player, String mapName) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§eMap §8- §6" + mapName);
        ILocationManager locationManager = MiniGame.get(LocationManager.class);
        ITeamManager teamManager = MiniGame.get(ITeamManager.class);

        for(int i = 0; i < 9; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        inventory.setItem(4, ItemBuilder.modify().setMaterial(Material.GRASS_BLOCK)
                .setDisplayName("§6" + mapName).buildItem());
        inventory.addItem(ItemBuilder.modify().setMaterial(Material.PAPER)
                .setDisplayName("§eBlock ändern").buildItem());

        for (String key : this.locationStateMap.keySet()){
            LocationState locationState = this.locationStateMap.get(key);

            switch (locationState){
                case COUNT:
                    inventory.addItem(ItemBuilder.modify().setMaterial(Material.PAPER).setDisplayName("§eCount " + key).buildItem());
                    break;
                case NORMAL:
                    Location location = locationManager.getLocation(key, locationState, mapName);
                    ItemBuilder itemBuilder = ItemBuilder.modify().setMaterial(Material.PAPER)
                            .setDisplayName("§e" + key).setModifiedBuilder(location, key);
                    inventory.addItem(itemBuilder.buildItem());
                    break;
                default:
                    break;
            }
        }
        if(this.locationStateMap.values().contains(LocationState.TEAM)){
            for(GameTeam gameTeam : teamManager.getTeams()){
                inventory.addItem(ItemBuilder.modify().setMaterial(Material.PAPER)
                        .setDisplayName("§eTeam " + gameTeam.getTeamName()).buildItem());
            }
        }

        player.openInventory(inventory);
    }

    @Override
    public void openMapTeamLocationInventory(Player player, String mapName, String teamName) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§eMap §8- §6" + mapName);
        ITeamManager teamManager = MiniGame.get(ITeamManager.class);
        ILocationManager locationManager = MiniGame.get(LocationManager.class);

        for(int i = 0; i < 9; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }

        GameTeam gameTeam = teamManager.getTeamFromName(teamName);
        if(gameTeam != null){
            inventory.setItem(5, gameTeam.getItemStack("§6Team " + gameTeam.getTeamName()));
        }
        inventory.setItem(3, ItemBuilder.modify().setMaterial(Material.GRASS_BLOCK)
                .setDisplayName("§6" + mapName).buildItem());

        for (String key : this.locationStateMap.keySet()) {
            LocationState locationState = this.locationStateMap.get(key);
            if(locationState == LocationState.TEAM){
                Location location = locationManager.getLocation(key, locationState, mapName, teamName);
                ItemBuilder itemBuilder = ItemBuilder.modify().setMaterial(Material.PAPER)
                        .setDisplayName("§e" + key).setModifiedBuilder(location, key, "§7das Team §6" + teamName);
                inventory.addItem(itemBuilder.buildItem());
            }
        }
        player.openInventory(inventory);
    }

    @Override
    public void openMapCountLocationInventory(Player player, String mapName, String countName) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§eMap §8- §6" + mapName);
        ILocationManager locationManager = MiniGame.get(LocationManager.class);

        for(int i = 0; i < 9; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        for(int i = 45; i < 54; i++){
            inventory.setItem(i, ItemBuilder.modify().setMaterial(Material.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("§0").buildItem());
        }
        inventory.setItem(3, ItemBuilder.modify().setMaterial(Material.GRASS_BLOCK)
                .setDisplayName("§6" + mapName).buildItem());
        inventory.setItem(5, ItemBuilder.modify().setMaterial(Material.PAPER)
                .setDisplayName("§6Count " + countName).buildItem());
        inventory.setItem(49, ItemBuilder.modify().setMaterial(Material.GOLD_INGOT)
                .setDisplayName("§aAdd " + countName + " Location").buildItem());

        for(Location location : locationManager.getLocations(countName, LocationState.COUNT, mapName)){
            inventory.addItem(ItemBuilder.modify().setMaterial(Material.GOLD_NUGGET).setDisplayName("§e" + countName)
                    .setModifiedBuilder(location).buildItem());
        }
        player.openInventory(inventory);
    }

    @Override
    public void teleportPlayerToLobby(Player player) {
        ILocationManager locationManager = MiniGame.get(LocationManager.class);
        Location location = locationManager.getLocation("Lobby", LocationState.LOBBY);

        if(location != null){
            player.teleport(location);
        }
    }

    @Override
    public void setCurrentMap(GameMap gameMap) {
        this.currentGameMap = gameMap;
    }

    @Override
    public void setForceMap(GameMap gameMap) {
        this.currentForceMap = gameMap;
    }

    @Override
    public void teleportPlayerToCurrentMap(Player player, int spawn) {
        ILocationManager locationManager = MiniGame.get(LocationManager.class);

        if(locationStateMap.containsKey("Spawn") == false){
            throw new NullPointerException("Location Identifier 'Spawn' not found.");
        }

        String mapName = getCurrentMap().getMapName();
        List<Location> locationList = locationManager.getLocations("Spawn", LocationState.COUNT, mapName);
        if(locationList.size() < spawn){
            throw new IndexOutOfBoundsException("Spawn '" + spawn + "' is not in Range.");
        }

        Location location = locationManager.getLocations("Spawn", LocationState.COUNT, mapName).get(spawn);
        if(location != null){
            player.teleport(location);
        }
    }

    @Override
    public boolean isMapLoaded() {
        return mapLoaded;
    }

    @Override
    public void loadMap(GameMap gameMap) {
        try {
            int count = 0;

            if (getCurrentMap() == null) {
                takeRandomMap();
            }

            generateMap(gameMap);

            while (isMapLoaded() == false) {
                Thread.sleep(99);
                count += 1;

                if (count >= 100) {
                    break;
                }
            }

            ITeamManager teamManager = MiniGame.get(ITeamManager.class);
            if (teamManager != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    teamManager.randomTeamForPlayer(player);
                    teleportPlayerToCurrentMap(player);
                }
            } else if (teamManager == null) {
                int spawn = 0;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    teleportPlayerToCurrentMap(player, spawn++);
                }
            }
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }

    private void generateMap(GameMap gameMap){
        if (gameMap == null){
            System.out.println("Es konnte keine Map gefunden werden, breche ab.");
            return;
        }

        String basePath = Paths.get(MiniGame.getJavaPlugin().getDataFolder().getAbsolutePath())
                .getParent().getParent().toString();
        Optional<List<Location>> optionalLocations = gameMap.getLocationList().values().stream()
                .filter(x -> x.size() > 0).findAny();

        if (optionalLocations.isPresent() == false){
            System.out.println("optionalLocations is not present");
            return;
        }

        Optional<Location> optionalLocation = optionalLocations.isPresent()
                ? optionalLocations.get().stream().findAny() : null;

        if (optionalLocation == null || optionalLocation.isPresent() == false){
            System.out.println("optionalLocation is not present or null");
            return;
        }

        String worldName = optionalLocation != null && optionalLocation.isPresent()
                ? optionalLocation.get().getWorld().getName() : null;

        if (worldName == null){
            System.out.println("Es konnte keine World gefunden werden, breche ab.");
            return;
        }

        try {
            FileUtils.copyDirectory
                    (
                            Paths.get(basePath, worldName).toFile(),
                            Paths.get(basePath, worldName + "_InGame_Map").toFile()
                    );

            Path uidPath = Paths.get(basePath, worldName + "_InGame_Map", "uid.dat");
            if (Files.exists(uidPath)){
                FileUtils.delete(uidPath.toFile());
            }
        } catch (Exception exception){
            System.out.println("Cannot copy File: " + exception.getMessage());
        }

        Bukkit.getScheduler().callSyncMethod(MiniGame.getJavaPlugin(), () ->{
            if(!Bukkit.getWorlds().stream()
                    .map(currentWorld -> currentWorld.getName())
                    .collect(Collectors.toList())
                    .contains(worldName + "_InGame_Map")) {
                WorldCreator worldCreator = new WorldCreator(worldName + "_InGame_Map");
                worldCreator.createWorld();
                Bukkit.createWorld(worldCreator);
            }

            World world = Bukkit.getWorld(worldName + "_InGame_Map");
            world.setThundering(false);
            world.setStorm(false);
            world.setAutoSave(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setThunderDuration(Integer.MAX_VALUE);
            world.setTime(1000);
            world.setDifficulty(Difficulty.NORMAL);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, false);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

            for (String key : gameMap.getLocationList().keySet()){
                List<Location> locationList = gameMap.getLocationList().get(key);

                for (Location location : locationList){
                    location.setWorld(world);
                }
            }
            this.mapLoaded = true;
            return null;
        });
    }

    @Override
    public void teleportPlayerToCurrentMap(Player player) {
        ITeamManager teamManager = MiniGame.get(ITeamManager.class);

        if(teamManager != null){
            GameMap currentMap = getCurrentMap();

            ILocationManager locationManager = MiniGame.get(LocationManager.class);
            GameTeam gameTeam = teamManager.getPlayerTeam(player);
            String mapName = getCurrentMap().getMapName();
            String locationIdentifier = locationManager.getReplacedIdentifier("Spawn",
                    LocationState.TEAM, mapName, gameTeam.getTeamName());

            if (currentMap.getLocationList().containsKey(locationIdentifier)){
                Optional<Location> optionalLocation = currentMap.getLocationList()
                        .get(locationIdentifier).stream().findAny();

                if(optionalLocation != null && optionalLocation.isPresent()){
                    Bukkit.getScheduler().callSyncMethod(MiniGame.getJavaPlugin(), () ->{
                        player.teleport(optionalLocation.get());
                        return null;
                    });
                }
            }
        }
    }

    @Override
    public Map<String, LocationState> getLocationStateMap() {
        return locationStateMap;
    }

    @Override
    public List<String> getLocationStateKeysFromState(LocationState locationState) {
        List<String> stringList = Lists.newArrayList();

        for(String key : this.locationStateMap.keySet()){
            if(this.locationStateMap.get(key) == locationState){
                stringList.add(key);
            }
        }
        return stringList;
    }

    @Override
    public void registerDefault() {
        PluginManager pluginManager = MiniGame.getJavaPlugin().getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new InventoryClickListener(), MiniGame.getJavaPlugin());

        MiniGame.getJavaPlugin().getCommand("location").setExecutor(new LocationCommand());
        MiniGame.getJavaPlugin().getCommand("map").setExecutor(new MapCommand());
        MiniGame.getJavaPlugin().getCommand("maps").setExecutor(new MapsCommand());

        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        messageManager.addMessage("AddedMap", "{PREFIX}§7Die Map §e{MAP} §7wurde §ahinzugefügt§8.");
        messageManager.addMessage("UpdatedBlock", "{PREFIX}§7Der Block für die Map §e{MAP} §7wurde §ageupdated§8.");
        messageManager.addMessage("MapAlreadyExists",
                "{PREFIX}§cDie Map §e{MAP} §ckonnte nicht hinzugefügt werden, da sie bereits existiert.");
        messageManager.addMessage("MapVotingDisplayName", "§e{MAP}");
    }

    @Override
    public void deleteOldMaps() {
        String basePath = Paths.get(MiniGame.getJavaPlugin().getDataFolder().getAbsolutePath())
                .getParent().getParent().toString();

        System.out.println(basePath);

        for (GameMap gameMap : this.gameMapList){
            String worldName = gameMap.getWorldName();
            Path path = Paths.get(basePath, worldName + "_InGame_Map");

            System.out.println("File exists: " + path.toString());


            if (Files.exists(path)){
                System.out.println("Deleting Directory: " + path.toString());
                try {
                    FileUtils.deleteDirectory(path.toFile());
                    System.out.println("Deleted Directory: " + path.toString());
                } catch (Exception exception){
                    System.out.println(exception.getMessage());
                }
            }
        }
    }
}
