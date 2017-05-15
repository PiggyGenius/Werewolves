package model;

import java.util.Objects;
import java.sql.Date;

public class Game {
	private int gameId;
	private String username;
	private int minPlayer;
	private int maxPlayer;
	private float dayStart;
	private float dayLength;
	private float nightLength;
	private Date gameStart;
	private float contamination;
	private float insomniac;
	private float fortuneTeller;
	private float spiritualist;
	private float werewolf;
	private boolean nightTime;

	/* TODO: Change gameStart to be a string (MVC) */
	public Game(int gameId, String username, int minPlayer, int maxPlayer, float dayStart, float dayLength, float nightLength, Date gameStart, float contamination, float insomniac, float fortuneTeller, float spiritualist, float werewolf, boolean nightTime){
		this.gameId = gameId;
		this.username = username;
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.dayStart = dayStart;
		this.dayLength = dayLength;
		this.nightLength = nightLength;
		this.gameStart = gameStart;
		this.contamination = contamination;
		this.insomniac = insomniac;
		this.fortuneTeller = fortuneTeller;
		this.spiritualist = spiritualist;
		this.werewolf = werewolf;
		this.nightTime = nightTime;
	}

	/** @return object information as a String using JSON format */
	public String toJSON(){
		return "{\"gameId\": \""+this.gameId+"\", "
			+ "\"username\": \""+this.username+"\", "
			+ "\"minPlayer\": \""+this.minPlayer+"\", "
			+ "\"maxPlayer\": \""+this.maxPlayer+"\", "
			+ "\"dayStart\": \""+this.dayStart+"\", "
			+ "\"dayLength\": \""+this.dayLength+"\", "
			+ "\"nightLength\": \""+this.nightLength+"\", "
			+ "\"gameStart\": \""+this.gameStart+"\", "
			+ "\"contamination\": \""+this.contamination+"\", "
			+ "\"insomniac\": \""+this.insomniac+"\", "
			+ "\"fortuneTeller\": \""+this.fortuneTeller+"\", "
			+ "\"spiritualist\": \""+this.spiritualist+"\", "
			+ "\"werewolf\": \""+this.werewolf+"\"}";
	}

	/**
	 * Redfining equals on game to consider the gameId
	 * @param o Object to compare this to
	 * @return true if both gameIds have the same value
	 */
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Game))
			return false;
		Game game = (Game) o;
		return game.getGameId() == this.getGameId();
	}

	/** @return hashCode based on the gameId */
	@Override
	public int hashCode(){
		return Objects.hash(this.gameId);
	}

	/** @param gameId Set gameId of Game */
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	/** @return gameId */
	public int getGameId() {
		return gameId;
	}

	/** @param username Set creator of Game */
	public void setUsername(String username) {
		this.username = username;
	}

	/** @return username */
	public String getUsername() {
		return username;
	}

	/** @param minPlayer Set minPlayer of Game */
	public void setMinPlayer(int minPlayer) {
		this.minPlayer = minPlayer;
	}

	/** @return minPlayer */
	public int getMinPlayer() {
		return minPlayer;
	}

	/** @param maxPlayer Set maxPlayer of Game */
	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

	/** @return maxPlayer */
	public int getMaxPlayer() {
		return maxPlayer;
	}

	/** @param dayStart Set dayStart of Game */
	public void setDayStart(float dayStart) {
		this.dayStart = dayStart;
	}

	/** @return dayStart */
	public float getDayStart() {
		return dayStart;
	}

	/** @param dayLength Set dayLength of Game */
	public void setDayLength(float dayLength) {
		this.dayLength = dayLength;
	}

	/** @return dayLength */
	public float getDayLength() {
		return dayLength;
	}

	/** @param dayLength Set nightLength of Game */
	public void setNightLength(float nightLength) {
		this.nightLength = nightLength;
	}

	/** @return nightLength */
	public float getNigthLength() {
		return nightLength;
	}

	/** @param gameStart Set gameStart of Game */
	public void setGameStart(Date gameStart) {
		this.gameStart = gameStart;
	}

	/** @return gameStart */
	public Date getGameStart() {
		return gameStart;
	}

	/** @param contamination Set contamination of Game */
	public void setContamination(float contamination) {
		this.contamination = contamination;
	}

	/** @return contamination */
	public float getContamination() {
		return contamination;
	}

	/** @param insomniac Set insomniac of Game */
	public void setInsomniac(float insomniac) {
		this.insomniac = insomniac;
	}

	/** @return insomniac */
	public float getInsomniac() {
		return insomniac;
	}

	/** @param fortuneTeller Set fortuneTeller of Game */
	public void setFortuneTeller(float fortuneTeller) {
		this.fortuneTeller = fortuneTeller;
	}

	/** @return fortuneTeller */
	public float getFortuneTeller() {
		return fortuneTeller;
	}

	/** @param spiritualist Set spiritualist of Game */
	public void setSpiritualist(float spiritualist) {
		this.spiritualist = spiritualist;
	}

	/** @return spiritualist */
	public float getSpiritualist() {
		return spiritualist;
	}

	/** @param werewolf Set werewolf of Game */
	public void setWerewolf(float werewolf) {
		this.werewolf = werewolf;
	}

	/** @return werewolf */
	public float getWerewolf() {
		return werewolf;
	}

	/** @param nightTime Set nightTime of Game */
	public void setNightTime(boolean nightTime) {
		this.nightTime = nightTime;
	}

	/** @return nightTime */
	public boolean getNightTime() {
		return nightTime;
	}
}
