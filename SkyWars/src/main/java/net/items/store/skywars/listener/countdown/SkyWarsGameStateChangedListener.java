package net.items.store.skywars.listener.countdown;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.countdown.ICountdownManager;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameScoreboard;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.api.scoreboard.IScoreboardManager;
import net.items.store.minigames.api.stats.IStatsManager;
import net.items.store.minigames.core.countdown.CountdownManager;
import net.items.store.minigames.core.event.CountdownGameStateChangedEvent;
import net.items.store.skywars.loot.SkyWarsLootManager;
import net.items.store.skywars.scoreboard.SkyWarsScoreboardManager;
import net.items.store.skywars.stats.SkyWarsStats;
import net.items.store.skywars.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkyWarsGameStateChangedListener implements Listener {

    @EventHandler
    public void onCountdownGameStateChanged(CountdownGameStateChangedEvent countdownGameStateChangedEvent){
        if (countdownGameStateChangedEvent.getOldGameState() == GameState.LOBBY){
            prepareLoot();

            ICountdownManager countdownManager = MiniGame.get(CountdownManager.class);

            AbstractCountdownTask abstractCountdownTask = countdownManager.getCountdown(GameState.WAITING);
            abstractCountdownTask.setWait(true);

            for (Player player : Bukkit.getOnlinePlayers()){
                player.setGameMode(GameMode.SURVIVAL);
            }

            if (countdownGameStateChangedEvent.getNewGameState() != GameState.RESTART){
                IStatsManager statsManager = MiniGame.get(StatsManager.class);

                for (Player player : Bukkit.getOnlinePlayers()){
                    SkyWarsStats skyWarsStats = statsManager.loadStats(player.getUniqueId());
                    skyWarsStats.setGamesPlayed(skyWarsStats.getGamesPlayed() + 1);
                    statsManager.saveStats(skyWarsStats);
                }
            }
        }

        if (countdownGameStateChangedEvent.getNewGameState() == GameState.RESTART){
            IMapManager mapManager = MiniGame.get(IMapManager.class);

            Bukkit.getScheduler().callSyncMethod(MiniGame.getJavaPlugin(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()){
                    player.closeInventory();
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);

                    mapManager.teleportPlayerToLobby(player);

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
                        onlinePlayer.showPlayer(MiniGame.getJavaPlugin(), player);
                        player.showPlayer(MiniGame.getJavaPlugin(), onlinePlayer);
                    }
                }
                return null;
            });
        }
    }

    private void prepareLoot(){
        MiniGame.getExecutorService().submit(() -> {
            IMapManager mapManager = MiniGame.get(IMapManager.class);
            int count = 0;

            while (mapManager.isMapLoaded() == false){
                try {
                    Thread.sleep(99);
                } catch (Exception exception){
                    System.out.println(exception.getMessage());
                }
                count += 1;

                if (count >= 100){
                    break;
                }
            }

            SkyWarsLootManager.prepareLoot();
        });
    }
}