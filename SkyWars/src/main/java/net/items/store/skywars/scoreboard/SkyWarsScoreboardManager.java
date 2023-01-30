package net.items.store.skywars.scoreboard;

import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.game.AbstractGame;
import net.items.store.minigames.api.scoreboard.ScoreboardReplaces;
import net.items.store.minigames.api.stats.IStatsManager;
import net.items.store.minigames.core.scoreboard.DefaultScoreboardManager;
import net.items.store.skywars.stats.SkyWarsStats;
import net.items.store.skywars.stats.StatsManager;
import org.bukkit.entity.Player;

public class SkyWarsScoreboardManager extends DefaultScoreboardManager {

    public SkyWarsScoreboardManager(){
        super();
    }

    @Override
    public String getReplacedLine(Player player, String line, ScoreboardReplaces scoreboardReplaces) {
        String currentLine = super.getReplacedLine(player, line, scoreboardReplaces);
        IStatsManager statsManager = MiniGame.get(StatsManager.class);
        SkyWarsStats skyWarsStats = statsManager.loadStats(player.getUniqueId());

        if(skyWarsStats != null){
            switch (scoreboardReplaces){
                case KILLS:
                    currentLine = currentLine.replace(scoreboardReplaces.getReplaceString(),
                            String.valueOf(skyWarsStats.getGamesKills()));
                    break;
                case DEATHS:
                    currentLine = currentLine.replace(scoreboardReplaces.getReplaceString(),
                            String.valueOf(skyWarsStats.getDeaths()));
                    break;
                case WINS:
                    currentLine = currentLine.replace(scoreboardReplaces.getReplaceString(),
                            String.valueOf(skyWarsStats.getGamesWon()));
                    break;
                case POINTS:
                    currentLine = currentLine.replace(scoreboardReplaces.getReplaceString(),
                            String.valueOf(skyWarsStats.getPoints()));
                    break;
                case KD:
                    currentLine = currentLine.replace(scoreboardReplaces.getReplaceString(),
                            String.valueOf(skyWarsStats.getKd()));
                    break;
                case LOSES:
                    currentLine = currentLine.replace(scoreboardReplaces.getReplaceString(),
                            String.valueOf(skyWarsStats.getGamesLost()));
                    break;
                case PLAYER_ALIVE:
                    currentLine = currentLine.replace(scoreboardReplaces.getReplaceString(),
                            String.valueOf(MiniGame.get(AbstractGame.class).getAlivePlayers().size()));
                    break;
                default:
                    break;
            }
        }

        return currentLine;
    }
}
