
package model.vote;

import model.User;

public class Vote {
	private boolean isDead;
	private int count;
	private boolean hasVoted;

	public Vote() {
		this.count = count;
		this.hasVoted = false;
		this.isDead = false;
	}

	public void inc() {
		this.count ++;
	}

	public boolean hasVoted() {
		return this.hasVoted;
	}

	public void setHasVoted() {
		this.hasVoted = true;
	}

	public boolean isDead() {
		return this.isDead;
	}

	public void setIsDead() {
		this.isDead = true;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	// public String toJSON() {
	//     return "{ \"target\": \"" + this.target + "\", \"count\": " + this.count + ", \"hasVoted\": " + this.hasVoted + "}";
	// }

	public String toJSON() {
		if (this.count == 0)
			return "null";
		else
			return "{ \"count\": " + this.count + ", \"hasVoted\": " + this.hasVoted + "}";
	}
}
