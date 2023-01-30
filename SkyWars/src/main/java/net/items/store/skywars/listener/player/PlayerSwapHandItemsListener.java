package net.items.store.skywars.listener.player;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerSwapHandItemsListener implements Listener {

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent playerSwapHandItemsEvent){
        AbstractGame abstractGame = MiniGame.get(AbstractGame.class);

        if (abstractGame.getGameState() == GameState.LOBBY){
            playerSwapHandItemsEvent.setCancelled(true);
        }
    }
}
