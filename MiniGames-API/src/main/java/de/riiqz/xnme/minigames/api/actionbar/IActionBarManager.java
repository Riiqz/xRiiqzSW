package de.riiqz.xnme.minigames.api.actionbar;

import org.bukkit.entity.Player;

public interface IActionBarManager {

    void sendActionBarToPlayer(Player player, String message);

    void sendActionBarToAll(String message);

}
