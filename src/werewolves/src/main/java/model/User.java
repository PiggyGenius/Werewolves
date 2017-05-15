package model;

public class User {
	private String username;

	/**
	 * User Constructor
	 * @param username Username of the user
	 */
	public User(String username){
		this.username = username;
	}

	/** @param username Set username of User */
	public void setUsername(String username) {
		this.username = username;
	}

	/** @return username */
	public String getUsername() {
		return username;
	}

	public String toJSON() {
		return "{ \"username\": \"" + this.username + "\"}";
	}
}
