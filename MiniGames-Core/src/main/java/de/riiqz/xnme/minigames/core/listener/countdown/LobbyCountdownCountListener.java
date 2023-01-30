package de.riiqz.xnme.minigames.core.listener.countdown;

import com.google.common.collect.Maps;
import de.riiqz.xnme.minigames.core.actionbar.ActionBarManager;
import de.riiqz.xnme.minigames.core.command.StartCommand;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.actionbar.IActionBarManager;
import net.items.store.minigames.api.countdown.AbstractCountdownTask;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameScoreboard;
import net.items.store.minigames.api.game.GameState;
import net.items.store.minigames.api.game.GameStateChangeReason;
import net.items.store.minigames.api.map.IMapManager;
import net.items.store.minigames.api.message.IMessageManager;
import net.items.store.minigames.api.scoreboard.IScoreboardManager;
import net.items.store.minigames.api.team.GameTeam;
import net.items.store.minigames.api.team.ITeamManager;
import net.items.store.minigames.api.voting.IVotingManager;
import de.riiqz.xnme.minigames.core.event.CountdownCountEvent;
import de.riiqz.xnme.minigames.core.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class LobbyCountdownCountListener implements Listener {

    private GameState gameStateAfter;
    private int count = 30;

    public LobbyCountdownCountListener(GameState gameStateAfter){
        this.gameStateAfter = gameStateAfter;

        IMessageManager messageManager = MiniGame.get(MessageManager.class);
        messageManager.addMessage("LobbyCountdownMessage", "{PREFIX}§7Das Spiel startet in §e{COUNT} §7Sekunden.");
        messageManager.addMessage("LobbyCountdownMessageOne", "{PREFIX}§7Das Spiel startet in §e{COUNT} §7Sekunde.");
        messageManager.addMessage("LobbyCountdownFinishMessage", "{PREFIX}§7Das Spiel startet nun.");
        messageManager.addMessage("WaitingForMorePlayers", "{PREFIX}§cWarten auf weitere Spieler.");
        messageManager.addMessage("SelectYourTeamActionBar", "§fWähle dein Team");
        messageManager.addMessage("YourTeamActionBar", "{TEAMPREFIX}Team {TEAMNAME}");

        MiniGame.getJavaPlugin().getCommand("start").setExecutor(new StartCommand());
    }

    @EventHandler
    public void onCountdownCount(CountdownCountEvent countdownCountEvent){
        if(countdownCountEvent.getCountdownTask().getGameState() == GameState.LOBBY){
            AbstractCountdownTask abstractCountdownTask = countdownCountEvent.getCountdownTask();
            if (abstractCountdownTask.isWait() == true){
                return;
            }

            IMessageManager messageManager = MiniGame.get(MessageManager.class);
            ITeamManager teamManager = MiniGame.get(ITeamManager.class);
            IActionBarManager actionBarManager = MiniGame.get(ActionBarManager.class);

            if(teamManager != null){
                for(Player player : Bukkit.getOnlinePlayers()){
                    GameTeam gameTeam = teamManager.getPlayerTeam(player);
                    if(gameTeam == null){
                        actionBarManager.sendActionBarToPlayer(player,
                                messageManager.getMessage("SelectYourTeamActionBar"));
                    } else {
                        Map<Object, Object> objectObjectMap = Maps.newHashMap();
                        objectObjectMap.put("{TEAMPREFIX}", gameTeam.getTeamPrefix());
                        objectObjectMap.put("{TEAMNAME}", gameTeam.getTeamName());

                        actionBarManager.sendActionBarToPlayer(player,
                                messageManager.getMessage("YourTeamActionBar", objectObjectMap));
                    }
                }
            }

            if(Bukkit.getOnlinePlayers().size() >= MiniGame.get(AbstractGame.class).getMinPlayers()) {
                int currentCount = abstractCountdownTask.getCount();
                count = 30;

                Map<Object, Object> objectObjectMap = Maps.newHashMap();
                objectObjectMap.put("{COUNT}", currentCount);

                if (currentCount == 60 || currentCount == 30 || currentCount == 15 || currentCount == 10 || currentCount <= 5 && currentCount >= 2) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(messageManager.getMessage("LobbyCountdownMessage", objectObjectMap));
                    }

                    if (currentCount == 10) {
                        IVotingManager votingManager = MiniGame.get(IVotingManager.class);
                        if (votingManager != null) {
                            votingManager.endVoting();

                            IScoreboardManager scoreboardManager = MiniGame.get(IScoreboardManager.class);
                            GameScoreboard gameScoreboard = scoreboardManager.findScoreboardByIdentifier(GameState.LOBBY);

                            for(Player player : Bukkit.getOnlinePlayers()){
                                scoreboardManager.updateScoreboardForPlayer(gameScoreboard, player);
                            }
                        }
                    }
                } else if (currentCount == 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(messageManager.getMessage("LobbyCountdownMessageOne", objectObjectMap));
                    }
                } else if (currentCount == 0) {
                    MiniGame.get(AbstractGame.class).setGameState(this.gameStateAfter, GameStateChangeReason.NEXT_GAMESTATE);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(messageManager.getMessage("LobbyCountdownFinishMessage", objectObjectMap));
                    }
                    return;
                }

                currentCount -= 1;
                abstractCountdownTask.setCount(currentCount);
            } else {
                if(count == 0){
                    count = 30;

                    for(Player player : Bukkit.getOnlinePlayers()){
                        player.sendMessage(messageManager.getMessage("WaitingForMorePlayers"));
                    }
                } else {
                    count -= 1;
                }
                abstractCountdownTask.setCount(60);
            }
        }
    }

}
