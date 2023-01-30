package de.riiqz.xnme.minigames.api.countdown;

import de.riiqz.xnme.minigames.api.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public abstract class AbstractCountdownTask {

	@Setter
	private String identifier;
	private GameState gameState;
	@Setter
	private int count;
	@Setter
	private boolean wait;
	@Setter
	private CountDirection countDirection;

	/**
	 * Execute a specified Task
	 */
	public abstract void executeEvent();

	protected void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
}
