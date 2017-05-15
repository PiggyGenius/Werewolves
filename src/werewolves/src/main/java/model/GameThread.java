package model;

import controller.GameTime;

public class GameThread {
	private GameTime gameTime;
	private Thread thread;
	

	public GameThread(GameTime gameTime, Thread thread){
		this.gameTime = gameTime;
		this.thread = thread;
	}

	/** @param gameTime Set gameTime of GameThread */
	public void setGameTime(GameTime gameTime) {
		this.gameTime = gameTime;
	}

	/** @return gameTime */
	public GameTime getGameTime() {
		return gameTime;
	}

	/** @param thread Set thread of GameThread */
	public void setThread(Thread thread) {
		this.thread = thread;
	}

	/** @return thread */
	public Thread getThread() {
		return thread;
	}
}
