package de.riiqz.xnme.minigames.core.game;

import de.riiqz.xnme.minigames.core.command.BuildCommand;
import de.riiqz.xnme.minigames.core.command.StatsCommand;
import de.riiqz.xnme.minigames.core.command.TopCommand;
import de.riiqz.xnme.minigames.core.event.CountdownGameStateChangedEvent;
import de.riiqz.xnme.minigames.core.listener.block.BlockPlaceListener;
import de.riiqz.xnme.minigames.core.listener.entity.EntityDamageListener;
import de.riiqz.xnme.minigames.core.listener.player.PlayerDropItemListener;
import de.riiqz.xnme.minigames.core.listener.player.PlayerJoinListener;
import de.riiqz.xnme.minigames.core.listener.player.PlayerPickupItemListener;
import de.riiqz.xnme.minigames.core.listener.player.PlayerQuitListener;
import de.riiqz.xnme.minigames.core.listener.world.FoodLevelChangeListener;
import de.riiqz.xnme.minigames.core.listener.world.WeatherChangeListener;
import de.riiqz.xnme.minigames.core.listener.world.WorldInitListener;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.countdown.ICountdownManager;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.game.GameStateChangeReason;
import net.items.store.minigames.api.message.IMessageManager;
import de.riiqz.xnme.minigames.core.listener.block.BlockBreakListener;
import net.items.store.minigames.core.listener.player.*;
import de.riiqz.xnme.minigames.core.message.MessageManager;
import de.riiqz.xnme.minigames.core.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

public class Game extends AbstractGame {

    public Game(){
        super();
    }

    @Override
    protected void loadMaps() {
        for(World world : Bukkit.getWorlds()){
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
        }
    }

    @Override
    public void setGameState(GameState gameState, GameStateChangeReason reason) {
        CountdownGameStateChangedEvent countdownGameStateChangedEvent = new CountdownGameStateChangedEvent(super.getGameState(), gameState, reason);
        MiniGame.getJavaPlugin().getServer().getPluginManager().callEvent(countdownGameStateChangedEvent);

        MiniGame.getExecutorService().submit(() ->{
            ICountdownManager countdownManager = MiniGame.get(ICountdownManager.class);
            AbstractCountdownTask abstractCountdownTask = countdownManager.getCountdown(GameState.LOBBY);

            if (countdownGameStateChangedEvent.isWait() == true){
                abstractCountdownTask.setWait(true);
            }

            while (countdownGameStateChangedEvent.isWait() == true){
                try {
                    Thread.sleep(999);
                } catch (Exception exception){
                    System.out.println(exception.toString());
                }
            }

            super.gameState = gameState;
        });
    }

    @Override
    public void registerDefault(){
        PluginManager pluginManager = MiniGame.getJavaPlugin().getServer().getPluginManager();
        pluginManager.registerEvents(new WeatherChangeListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new PlayerJoinListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new PlayerQuitListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new FoodLevelChangeListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new EntityDamageListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new BlockBreakListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new BlockPlaceListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new PlayerDropItemListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new PlayerPickupItemListener(), MiniGame.getJavaPlugin());
        pluginManager.registerEvents(new WorldInitListener(), MiniGame.getJavaPlugin());

        MiniGame.getJavaPlugin().getCommand("stats").setExecutor(new StatsCommand());
        MiniGame.getJavaPlugin().getCommand("build").setExecutor(new BuildCommand());
        MiniGame.getJavaPlugin().getCommand("top").setExecutor(new TopCommand());
        MiniGame.register(new PlayerManager());

        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        messageManager.addMessage("NoPermission", "{PREFIX}§cDu hast hierfür nicht genügend Permissions.");
        messageManager.addMessage("TeamSpectator", "{PREFIX}§7Du bist nun im Team §eSpectator§8!");
        messageManager.addMessage("WaitingForTeleport", "{PREFIX}§cDu wirst innerhalb der nächsten Sekunden teleportiert!");


    }
}
