package de.riiqz.xnme.minigames.api.coin;

import de.riiqz.xnme.minigames.api.IDefaultManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface ICoinManager extends IDefaultManager {

    void createUser(Player player);

    void addCoins(Player player, long coins);

    void removeCoins(Player player, long coins);

    void setCoins(Player player, long coins);

    long getCoins(UUID uniqueID);

}
