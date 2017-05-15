package controller;

import javax.sql.DataSource;
import javax.annotation.Resource;
import java.sql.Date;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

import dao.GameBoardDAO;
import dao.GameDAO;

import model.Game;

public class GameTime implements Runnable {
	private volatile boolean running = true;
	private DataSource dataSource;
	private Game game;

	public GameTime(Game game, DataSource dataSource){
		this.game = game;
		this.dataSource = dataSource;
	}
	
	/* TODO: try it with wrong number of players to check */
	@Override
	public void run(){
		GameDAO gameDAO = new GameDAO(dataSource, this.game.getUsername());
		if(!gameDAO.checkMinPlayer(this.game.getGameId(), this.game.getMinPlayer())){
			gameDAO.leaveGame(this.game.getUsername(), this.game.getGameId());
			return ;
		}
		float nightLength = this.game.getNigthLength();
		float dayLength = this.game.getDayLength();
		long milliNightLength = TimeUnit.HOURS.toMillis((int) nightLength) + TimeUnit.MINUTES.toMillis((int) ((nightLength - (int) nightLength) * 100));
		long milliDayLength = TimeUnit.HOURS.toMillis((int) dayLength)+ TimeUnit.MINUTES.toMillis((int) ((dayLength - (int) dayLength) * 100));

		gameDAO.startGame(game.getGameId());
		while(running && gameDAO.checkEnd(this.game.getGameId()).equals("CONTINUE")){
			try {
				if(game.getNightTime())
					Thread.sleep(milliNightLength);
				else
					Thread.sleep(milliDayLength);
				gameDAO.resetSpirit(this.game.getGameId());
				gameDAO.resetPowers(this.game.getGameId());
				gameDAO.deleteVotes(this.game.getGameId());
				gameDAO.archiveMessages(this.game.getGameId(), true);
				game.setNightTime(!this.game.getNightTime());
				gameDAO.changePeriod(game.getGameId(), game.getNightTime());
			} catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		}
	}

	public void terminate(){
		running = false;
	}

	/** @param game Set game of GameTime */
	public void setGame(Game game) {
		this.game = game;
	}

	/** @return game */
	public Game getGame() {
		return game;
	}
}
