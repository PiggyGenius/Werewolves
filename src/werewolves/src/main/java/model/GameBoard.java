package model;

import java.util.HashSet;
import java.util.Iterator;

public class GameBoard {
	private HashSet<Game> gameSet;
	private int maxId;

	/** Empty GameBoard constructor */
	public GameBoard(){
		this.gameSet = new HashSet<Game>();
	}

	/** @param game Adds game to HashSet */
	public void add(Game game){
		this.gameSet.add(game);
		this.maxId = 0;
	}

	/** @return iterator of game hashSet */
	public Iterator iterator(){
		return this.gameSet.iterator();
	}

	/** @return every game inside HashSet as a JSON format string */
	public String toJSON(){
		Iterator<Game> iterator = this.gameSet.iterator();
		String json = "{\"maxId\": \""+this.maxId+"\", \"gameBoard\":[";
		if(iterator.hasNext()){
			json += iterator.next().toJSON();
		}
		while(iterator.hasNext()){
			json += ", " + iterator.next().toJSON();
		}
		return json + "]}";
	}

	/** 
	 * Full GameBoard constructor
	 * @param gameSet Initial set of game 
	 * @param maxId max lobby id for updates
	 */
	public GameBoard(HashSet<Game> gameSet, int maxId){
		this.gameSet = gameSet;
		this.maxId = maxId;
	}

	/** @param gameSet Set gameSet of GameBoard */
	public void setGameSet(HashSet<Game> gameSet) {
		this.gameSet = gameSet;
	}

	/** @return gameSet */
	public HashSet<Game> getGameSet() {
		return gameSet;
	}

	/** @param maxId Set maxId of GameBoard */
	public void setMaxId(int maxId) {
		this.maxId = maxId;
	}

	/** @return maxId */
	public int getMaxId() {
		return this.maxId;
	}
}
