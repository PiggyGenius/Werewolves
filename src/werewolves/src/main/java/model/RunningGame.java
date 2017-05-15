package model;

import java.sql.Date;

public class RunningGame extends Game {
	private IngameUser user;

	public RunningGame(int gameId, String username, int minPlayer, int maxPlayer,
			float dayStart, float dayLength, float nightLength, Date gameStart,
			float contamination, float insomniac, float fortuneTeller, float
			spiritualist, float werewolf, boolean nightTime, IngameUser user){
		super(gameId, username, minPlayer, maxPlayer, dayStart,
				dayLength, nightLength, gameStart, contamination, insomniac,
				fortuneTeller, spiritualist, werewolf, nightTime);
		this.user = user;
	}

	/** @param user Set user of IngameUser */
	public void setUser(IngameUser user) {
		this.user = user;
	}

	/** @return user */
	public IngameUser getDead() {
		return user;
	}

}
