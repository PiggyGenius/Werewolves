
package model.vote;

import java.util.HashMap;
import model.User;

public class UserVoteMap extends HashMap<String, Vote> {

	public UserVoteMap() {
		super();
	}

	public String toJSON() {
		String json = "{ \"users\": [ ";
		for (String s : this.keySet()) {
			json += "\n" + "{ \"target\": \"" + s + "\", \"dead\": " + this.get(s).isDead() + ", \"vote\": " + this.get(s).toJSON() + "},";
		}
		return json.substring(0, json.length()-1) + "\n]}";
	}

}
