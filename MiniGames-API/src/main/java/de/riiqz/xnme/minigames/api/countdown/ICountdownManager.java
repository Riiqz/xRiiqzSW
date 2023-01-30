package de.riiqz.xnme.minigames.api.countdown;

import de.riiqz.xnme.minigames.api.game.GameState;

import java.util.List;

public interface ICountdownManager {

	/**
	 * Schedules the countdown manager and start every second all countdowns.
	 */
	void scheduleCountdown();

	/**
	 * Adds a countdown to the countdown-List
	 * @param countdown
	 */
	void addCountdown(AbstractCountdownTask countdown);

	/**
	 * Register all default countdowns
	 */
	void registerDefaultCountdowns();

	/**
	 * @return the countdown-List
	 */
	List<AbstractCountdownTask> getCountdowns();

	AbstractCountdownTask getCountdown(GameState gameState);

	String getCountAsString(int count);

}
