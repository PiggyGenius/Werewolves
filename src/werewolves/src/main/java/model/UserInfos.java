package model;

public class UserInfos {
	private Role role;
	private Boolean dead;
	private Boolean chosen;
	private Boolean isNight;
	private Boolean powerUsed;

	public UserInfos(Role role, Boolean dead, Boolean chosen, Boolean isNight, Boolean powerUsed) {
		this.role = role;
		this.dead = dead;
		this.chosen = chosen;
		this.isNight = isNight;
		this.powerUsed = powerUsed;
	}

	public Role getRole() {
		return this.role;
	}

	public Boolean isDead() {
		return this.dead;
	}

	public Boolean spiritualChosen() {
		return this.chosen;
	}

	public Boolean powerIsUsed() {
		return this.powerUsed;
	}

	public String toJSON() {
		return "{\"role\": \"" + this.role + "\", \"dead\": " + this.dead + ", \"chosen\": " + this.chosen + ", \"night\": " + this.isNight + ", \"powerUsed\": " + this.powerUsed + "}";
	}
}
