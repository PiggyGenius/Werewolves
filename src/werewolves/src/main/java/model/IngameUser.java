package model;

import model.Role;

public class IngameUser extends User {
	private boolean dead;
	private String role;
	
	public IngameUser(String username, String role, boolean dead){
		super(username);
		this.role = role;
		this.dead = dead;
	}

	public IngameUser(String username, boolean dead){
		super(username);
		this.dead = dead;
	}


	/** @param dead Set dead of IngameUser */
	public void setDead(boolean dead) {
		this.dead = dead;
	}

	/** @return dead */
	public boolean getDead() {
		return dead;
	}

	/** @param role Set role of IngameUser */
	public void setRole(String role) {
		this.role = role;
	}

	/** @return role */
	public String getRole() {
		return role;
	}

	@Override
	public String toJSON() {
		return "{ \"username\": \"" + this.getUsername() + "\", \"dead\": \""+this.dead+"\"}";
	}
}
