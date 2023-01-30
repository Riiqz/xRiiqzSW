package de.riiqz.xnme.minigames.core.team.listener;

import de.riiqz.xnme.minigames.core.team.TeamManager;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.team.ITeamManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent){
        ITeamManager teamManager = MiniGame.get(TeamManager.class);
        teamManager.leaveTeam(playerQuitEvent.getPlayer());
    }
}
