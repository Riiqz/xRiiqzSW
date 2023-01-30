package de.riiqz.xnme.minigames.core.map.command;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.map.IMapManager;
import de.riiqz.xnme.minigames.core.map.MapManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;

            if(command.getName().equalsIgnoreCase("maps")){
                if(player.hasPermission("System.Maps")){
                    IMapManager mapManager = MiniGame.get(MapManager.class);
                    mapManager.openMapsInventory(player);
                }
            }
        }
        return false;
    }
}
