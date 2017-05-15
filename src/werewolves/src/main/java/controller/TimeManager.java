package controller;

import javax.sql.DataSource;
import javax.annotation.Resource;
import java.sql.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.Iterator;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import dao.GameBoardDAO;
import dao.GameDAO;
import model.GameBoard;
import model.GameThread;
import model.Game;

public class TimeManager implements Runnable {
	private volatile boolean running = true;
	private ArrayList<GameThread> gameThreadList;
	private DataSource dataSource;

	public TimeManager(DataSource dataSource){
		this.dataSource = dataSource;
	}

	@Override
	public void run(){
		this.gameThreadList = new ArrayList<GameThread>();
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		GameBoardDAO gameBoardDAO = new GameBoardDAO(this.dataSource);
		GameBoard gameBoard = gameBoardDAO.getPublicGameBoard(0);

		Iterator<Game> iterator;
		GameThread gameThread; GameTime gameTime; Game game;
		while(running){
			iterator = gameBoard.iterator();
			while(iterator.hasNext()){
				game = iterator.next();
				gameTime = new GameTime(game, this.dataSource);
				gameThread = new GameThread(gameTime, new Thread(gameTime));
				/* TODO: Changed so that we can fill the database */
				//if((game.getGameStart().getTime() - System.currentTimeMillis()) >= 0){
					scheduler.schedule(gameThread.getThread(), game.getGameStart().getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
					gameThreadList.add(gameThread);
				//}
			}
			/* TODO: Change to something like 30 seconds */
			try {
				Thread.sleep(3000);
			} catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
			gameBoard = gameBoardDAO.getPublicGameBoard(gameBoard.getMaxId());
		}
	}

	public void terminate(){
		running = false;
		try {
			for(GameThread gameThread: this.gameThreadList){
				gameThread.getGameTime().terminate();
				gameThread.getThread().join();
			}
		} catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
	}
}
