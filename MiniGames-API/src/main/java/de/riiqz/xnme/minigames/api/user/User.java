package de.riiqz.xnme.minigames.api.user;

import lombok.Getter;
import lombok.Setter;
import de.riiqz.xnme.minigames.api.team.GameTeam;

import java.util.UUID;

public class User {

	@Getter
	private UUID uniqueID;

	@Getter
	@Setter
	private GameTeam team;
	
	protected User(UUID uniqueID, GameTeam team) {
		this.uniqueID = uniqueID;
		this.team = team;
	}
	
	/**
	 * Creates a User instance with parameters
	 * @param uniqueID
	 * @return User instance
	 * @throws Exception when uniqueID is null
	 */
	public static User createUser(UUID uniqueID) {
		if(uniqueID == null) {
			throw new NullPointerException("UniqueID is null");
		}
		return new User(uniqueID, null);
	}
}
