package de.riiqz.xnme.minigames.core.coin;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.coin.ICoinManager;
import de.riiqz.xnme.minigames.core.coin.listener.PlayerJoinListener;
import de.riiqz.xnme.minigames.core.mysql.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;

public class CoinManager implements ICoinManager {

    @Override
    public void createUser(Player player) {
        MiniGame.get(CoinTable.class).createPlayer(player.getUniqueId());
    }

    @Override
    public void addCoins(Player player, long coins) {
        long currentPlayerCoins = getCoins(player.getUniqueId());
        currentPlayerCoins += coins;

        setCoins(player, currentPlayerCoins);
    }

    @Override
    public void removeCoins(Player player, long coins) {
        long currentPlayerCoins = getCoins(player.getUniqueId());
        currentPlayerCoins -= coins;

        setCoins(player, currentPlayerCoins);
    }

    @Override
    public void setCoins(Player player, long coins) {
        MiniGame.get(CoinTable.class).setPlayerCoins(player.getUniqueId(), coins);
    }

    @Override
    public long getCoins(UUID uniqueID) {
        return MiniGame.get(CoinTable.class).getPlayerCoins(uniqueID);
    }

    @Override
    public void registerDefault() {
        MySQL mySQL = MiniGame.get(MySQL.class);
        MiniGame.register(new CoinTable(mySQL));

        PluginManager pluginManager = MiniGame.getJavaPlugin().getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), MiniGame.getJavaPlugin());
    }
}
