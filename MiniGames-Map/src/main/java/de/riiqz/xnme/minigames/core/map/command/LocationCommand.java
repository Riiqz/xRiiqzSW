package de.riiqz.xnme.minigames.core.map.command;

import de.riiqz.xnme.minigames.core.map.location.LocationManager;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.location.ILocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;

            if(command.getName().equalsIgnoreCase("location")){
                if(player.hasPermission("System.Location")){
                    ILocationManager locationManager = MiniGame.get(LocationManager.class);
                    locationManager.openLocationInventory(player);
                }
            }
        }
        return false;
    }
}
