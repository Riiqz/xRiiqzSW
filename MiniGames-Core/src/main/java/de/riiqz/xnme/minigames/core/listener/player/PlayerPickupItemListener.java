package de.riiqz.xnme.minigames.core.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.game.AbstractGame;
import de.riiqz.xnme.minigames.core.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItemListener implements Listener {

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent playerPickupItemEvent){
        Player player = playerPickupItemEvent.getPlayer();

        if(MiniGame.get(PlayerManager.class).canPlayerBuild(player.getUniqueId()) == false){
            if(MiniGame.get(AbstractGame.class).getGameInteraction().isPickupAble() == false){
                playerPickupItemEvent.setCancelled(true);
            }
        }
    }
}
